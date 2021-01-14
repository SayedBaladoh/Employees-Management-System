package com.sayedbaladoh.ems.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sayedbaladoh.ems.EmsApplication;
import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.EmployeeEvent;
import com.sayedbaladoh.ems.model.EmployeeState;
import com.sayedbaladoh.ems.model.StateEvent;
import com.sayedbaladoh.ems.repository.EmployeeRepository;
import com.sayedbaladoh.ems.util.JsonUtil;

/**
 * Employee Rest Integration tests
 * 
 * Test the Employee rest web services' integration tests
 * 
 * @author Sayed Baladoh
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = EmsApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class EmployeeRestIntegrationTest {

	private final String API_URL = "/api/employees";
	private final Long INVALID_ID = -1L;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private EmployeeRepository repository;

	@AfterEach
	public void cleanUp() {
		// resetDb
		repository.deleteAll();
	}

	/**
	 * Validate get all employees
	 * 
	 * @throws Exception
	 */
	@Test
	// @Disabled
	public void givenEmployees_whenGetEmployees_thenReturnEmployeesWithStatus200()
			throws Exception {

		Employee employee1 = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		Employee employee2 = getEmployee("Ahmed", "Mohamed", "00201235478912", "test2@test.com", "12345789",
				"2005-01-01T00:00:00.000Z", "male", "EG", "Developer");
		Employee employee3 = getEmployee("Sara", "Ahmed", "00201235478921", "test3@test.com", "12345789",
				"2007-01-01T00:00:00.000Z", "female", "EG", "Tester");

		saveTestEmployee(employee1);
		saveTestEmployee(employee2);
		saveTestEmployee(employee3);

		// Method call and Verification
		mvc.perform(get(API_URL)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))))
				.andExpect(jsonPath("$.content[0].first_name", is(employee1.getFirstName())))
				.andExpect(jsonPath("$.content[1].first_name", is(employee2.getFirstName())))
				.andExpect(jsonPath("$.content[2].first_name", is(employee3.getFirstName())));
	}

	/**
	 * Validate findById with valid Id
	 */
	@Test
	public void givenEmployees_whenGetEmployeeByID_thenReturnEmployeeWithStatus200() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee = saveTestEmployee(employee);

		// Method call and Verification
		mvc.perform(get(API_URL + "/" + employee.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.first_name").exists())
				.andExpect(jsonPath("$.id").value(employee.getId()))
				.andExpect(jsonPath("$.first_name").value(employee.getFirstName()));
	}

	/**
	 * Validate findById with invalid Id
	 */
	@Test
	public void givenEmployee_whenGetInavlidEmployeeId_thenReturnNotFound() throws Exception {

		// Method call and Verification
		mvc.perform(get(API_URL + "/" + INVALID_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(404));
	}

	/**
	 * Verify post valid Employee
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	// @Disabled
	public void whenValidInput_thenAddAndReturnEmployeeWithAddedState() throws IOException, Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");

		// Method call and Verification
		mvc.perform(post(API_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil
						.toJson(((ObjectNode) JsonUtil.toJsonNode(employee)).put("password", employee.getPassword()))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.first_name").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.first_name", is(employee.getFirstName())))
				.andExpect(jsonPath("$.email", is(employee.getEmail())))
				.andExpect(jsonPath("$.state", is(EmployeeState.ADDED.toString())));

		List<Employee> found = repository.findAll();
		assertThat(found)
				.extracting(Employee::getFirstName)
				.contains(employee.getFirstName());
	}

	// @Rule
	// public ExpectedException exceptionRule = ExpectedException.none();

	/**
	 * Verify post invalid Employee
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	// @Disabled
	public void whenInvalidInput_thenNotAdded() throws IOException, Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "0020123547", "test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");

		// Method call and Verification
		mvc.perform(post(API_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil
						.toJson(((ObjectNode) JsonUtil.toJsonNode(employee)).put("password", employee.getPassword()))))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.id").doesNotExist());
	}

	/**
	 * Verify change the state of a given employee
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	public void whenStateEventInput_thenChangeTheEmployeeState() throws IOException, Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee.setState(EmployeeState.ADDED);
		employee = saveTestEmployee(employee);

		StateEvent stateEvent = new StateEvent();
		stateEvent.setEvent(EmployeeEvent.CHECK);

		// Method call and Verification
		mvc.perform(patch(API_URL + "/" + employee.getId() + "/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(stateEvent)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.state").exists())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.id").value(employee.getId()))
				.andExpect(jsonPath("$.state", is(EmployeeState.IN_CHECK.toString())));
	}

	/**
	 * Verify Put valid Employee
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Transactional
	@Test
	// @Disabled
	public void whenValidInput_thenEditAndReturnEmployeeWithUpdatedState() throws IOException, Exception {
		// Data preparation
		Employee employeeTest = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		Employee employee = saveTestEmployee(employeeTest);
		employee.setFirstName("Mohamed_test_update");
		employee.setEmail("test_update@test.com");

		// Method call and Verification
		mvc.perform(put(API_URL + "/" + employee.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil
						.toJson(((ObjectNode) JsonUtil.toJsonNode(employee)).put("password", employee.getPassword()))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.first_name").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.id").value(employee.getId()))
				.andExpect(jsonPath("$.first_name", is(employee.getFirstName())))
				.andExpect(jsonPath("$.email", is(employee.getEmail())))
				.andExpect(jsonPath("$.state", is(EmployeeState.UPDATED.toString())));
	}

	/**
	 * Verify Put inValid Employee ID
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	// @Disabled
	public void whenInValidInput_thenNotEdited() throws IOException, Exception {
		// Data preparation
		Employee employeeTest = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");

		employeeTest = saveTestEmployee(employeeTest);

		Employee employee = new Employee();
		employee.setId(employeeTest.getId());
		employee.setFirstName("Mohamed_test_update");
		employee.setEmail("test_update@test.com");

		// Method call and Verification
		mvc.perform(put(API_URL + "/" + INVALID_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil
						.toJson(((ObjectNode) JsonUtil.toJsonNode(employee)).put("password", employee.getPassword()))))
				.andExpect(status().is(404));
	}

	/**
	 * Verify Delete inValid Employee ID
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test
	// @Disabled
	public void whenInValidEmployeeId_thenEmployeeNotDeleted() throws IOException, Exception {
		// Method call and Verification
		mvc.perform(put(API_URL + "/" + INVALID_ID)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(400));
	}

	/**
	 * Save Test Employee
	 * 
	 * @param employee
	 * @return
	 */
	private Employee saveTestEmployee(Employee employee) {
		return repository.saveAndFlush(employee);
	}

	private Employee getEmployee(String firstName, String lastName, String phoneNumber, String email, String password,
			String birthDate, String gender, String countryCode, String position) {

		Employee employee = new Employee();
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setEmail(email);
		employee.setPhoneNumber(phoneNumber);
		employee.setGender(gender);
		employee.setCountryCode(countryCode);
		employee.setPosition(position);
		employee.setBirthDate(Date.from(Instant.parse(birthDate)));
		employee.setPassword(password);
		// employee.setCreatedAt(Instant.now());
		// employee.setUpdatedAt(Instant.now());
		return employee;
	}
}
