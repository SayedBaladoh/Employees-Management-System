/**
 * 
 */
package com.sayedbaladoh.ems.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.service.EmployeeService;
import com.sayedbaladoh.ems.util.JsonUtil;

/**
 * Employee controller units' test
 * 
 * Test the Employee rest web services' unit tests
 * 
 * @author Sayed Baladoh
 *
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(
		value = EmployeeController.class)
class EmployeeControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private PasswordEncoder crypt;

	@MockBean
	private EmployeeService service;

	/**
	 * Validate get all employees
	 * 
	 * Test method for
	 * {@link com.sayedbaladoh.ems.controller.EmployeeController#getEmployees(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenEmployeesList_whenGetEmployees_thenReturnJsonArray() throws Exception {
		// Data preparation
		Employee employee1 = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		Employee employee2 = getEmployee("Ahmed", "Mohamed", "00201235478912", "test2@test.com", "12345789",
				"2005-01-01T00:00:00.000Z", "male", "EG", "Developer");
		Employee employee3 = getEmployee("Sara", "Ahmed", "00201235478921", "test3@test.com", "12345789",
				"2007-01-01T00:00:00.000Z", "female", "EG", "Tester");

		List<Employee> employees = Arrays.asList(employee1, employee2, employee3);
		Page<Employee> page = new PageImpl<Employee>(employees);

		given(service.getAll(any(Pageable.class)))
				.willReturn(page);

		// Verification
		mvc.perform(get("/api/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(3)))
				.andExpect(jsonPath("$.content[0].first_name", is(employee1.getFirstName())))
				.andExpect(jsonPath("$.content[1].first_name", is(employee2.getFirstName())))
				.andExpect(jsonPath("$.content[2].first_name", is(employee3.getFirstName())));

		verify(service, VerificationModeFactory.times(1)).getAll(any(Pageable.class));
		reset(service);
	}

	/**
	 * Verify valid Employee Id to get
	 * 
	 * Test method for
	 * {@link com.sayedbaladoh.ems.controller.EmployeeController#getEmployeeProfile(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenEmployee_whenGetEmployee_thenReturnEmployee() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee.setId(1l);

		given(service.get(employee.getId()))
				.willReturn(Optional.of(employee));

		// Verification
		this.mvc.perform(get("/api/employees/" + employee.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.first_name").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.id").value(employee.getId()))
				.andExpect(jsonPath("$.first_name").value(employee.getFirstName()))
				.andExpect(jsonPath("$.email").value(employee.getEmail()))
				.andDo(print());

		verify(service, VerificationModeFactory.times(1)).get(employee.getId());
		reset(service);
	}

	/**
	 * Verify invalid Employee Id to get
	 * 
	 * Test method for
	 * {@link com.sayedbaladoh.ems.controller.EmployeeController#getEmployeeProfile(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenEmployee_whenGetInavlidEmployeeId_thenReturn404() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee.setId(1l);

		given(service.get(employee.getId()))
				.willReturn(Optional.of(employee));

		// Verification
		this.mvc.perform(get("/api/employees/55")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(404))
				.andDo(print());

		reset(service);
	}

	/**
	 * Test method for
	 * {@link com.sayedbaladoh.ems.controller.EmployeeController#checkEmailAvailability(java.lang.String)}.
	 * 
	 * @throws Exception
	 */
	@Test
	void testCheckEmailAvailability() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee.setId(1l);

		given(service.existsByEmail(employee.getEmail()))
				.willReturn(false);

		// Verification
		this.mvc.perform(get("/api/employees/availabile/email/" + employee.getEmail())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.available").exists())
				.andExpect(jsonPath("$.available").value(true))
				.andDo(print());

		reset(service);
	}

	/**
	 * Verify post a valid Employee
	 * 
	 * Test method for
	 * {@link com.sayedbaladoh.ems.controller.EmployeeController#addEmployee(com.sayedbaladoh.ems.model.Employee)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void whenPostValidEmployee_thenAddEmployee() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		given(service.add(Mockito.anyObject()))
				.willReturn(employee);

		// Verification
		mvc.perform(post("/api/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil
						.toJson(((ObjectNode) JsonUtil.toJsonNode(employee)).put("password", employee.getPassword()))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.first_name").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.first_name", is(employee.getFirstName())))
				.andExpect(jsonPath("$.email", is(employee.getEmail())));

		verify(service, VerificationModeFactory.times(1)).add(Mockito.anyObject());
		reset(service);
	}

	/**
	 * Verify update a valid Employee
	 *
	 * Test method for
	 * {@link com.sayedbaladoh.ems.controller.EmployeeController#editEmployee(java.lang.Long, com.sayedbaladoh.ems.model.Employee)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void whenPutValidEmployee_thenEditEmployee() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee.setId(1l);

		given(service.get(employee.getId()))
				.willReturn(Optional.of(employee));

		given(service.edit(Mockito.anyObject()))
				.willReturn(employee);

		// Verification
		mvc.perform(put("/api/employees/" + employee.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil
						.toJson(((ObjectNode) JsonUtil.toJsonNode(employee)).put("password", employee.getPassword()))))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.first_name").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.first_name", is(employee.getFirstName())))
				.andExpect(jsonPath("$.email").value(employee.getEmail()))
				.andDo(print());

		verify(service, VerificationModeFactory.times(1)).edit(Mockito.anyObject());
		reset(service);
	}

	/**
	 * Verify update invalid Employee Id
	 * 
	 * @throws Exception
	 */
	@Test
	public void whenPutInvalidEmployeeId_thenNotEditEmployee() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee.setId(1l);

		given(service.get(employee.getId()))
				.willReturn(Optional.of(employee));

		given(service.edit(Mockito.anyObject()))
				.willReturn(employee);

		// Verification
		mvc.perform(put("/api/employees/55")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil
						.toJson(((ObjectNode) JsonUtil.toJsonNode(employee)).put("password", employee.getPassword()))))
				.andExpect(status().is(404))
				.andDo(print());

		reset(service);
	}

	/**
	 * Verify a valid Employee Id to delete
	 *
	 * Test method for
	 * {@link com.sayedbaladoh.ems.controller.EmployeeController#deleteEmployee(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */

	@Test
	public void whenDeleteValidEmployee_thenRemoveEmployee() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee.setId(1l);

		given(service.get(employee.getId()))
				.willReturn(Optional.of(employee));

		// Verification
		mvc.perform(delete("/api/employees/" + employee.getId())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200))
				.andDo(print());

		verify(service, VerificationModeFactory.times(1)).delete(employee);
		reset(service);
	}

	/**
	 * Verify an invalid Employee Id to delete
	 * 
	 ** Test method for
	 * {@link com.sayedbaladoh.ems.controller.EmployeeController#deleteEmployee(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void whenDeleteInvalidEmployeeId_thenNotRemoveEmployee() throws Exception {
		// Data preparation
		Employee employee = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee.setId(1l);

		given(service.get(employee.getId()))
				.willReturn(Optional.of(employee));

		// Verification
		mvc.perform(delete("/api/employees/50")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(404))
				.andDo(print());

		reset(service);
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
		// employee.setPassword(crypt.encode(password));
		employee.setCreatedAt(Instant.now());
		employee.setUpdatedAt(Instant.now());
		return employee;
	}
}
