/**
 * 
 */
package com.sayedbaladoh.ems.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.EmployeeEvent;
import com.sayedbaladoh.ems.model.EmployeeState;
import com.sayedbaladoh.ems.repository.EmployeeRepository;

/**
 * Employee ServiceImp units' test
 * 
 * @author Sayed Baladoh
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmployeeServiceImpTest {

	@TestConfiguration
	static class EmployeeServiceImpTestContextConfiguration {

		@Bean
		public EmployeeService employeeService() {
			return new EmployeeServiceImp();
		}
	}

	@Autowired
	private EmployeeService employeeService;

	@MockBean
	private EmployeeRepository employeeRepository;

	@MockBean
	private PasswordEncoder crypt;

	private final Long INVALID_ID = -1L;
	private final Long FIRST_EMPLOYEE_ID = 1L;
	private Employee employee1;

	@BeforeEach
	void setUp() {
		// Data preparation
		employee1 = getEmployee("Mohamed", "Ahmed", "00201235478915", "test1@test.com", "12345789",
				"2006-01-01T00:00:00.000Z", "male", "EG", "Web Admin");
		employee1.setId(FIRST_EMPLOYEE_ID);
		Employee employee2 = getEmployee("Ahmed", "Mohamed", "00201235478912", "test2@test.com", "12345789",
				"2005-01-01T00:00:00.000Z", "male", "EG", "Developer");
		Employee employee3 = getEmployee("Sara", "Ahmed", "00201235478921", "test3@test.com", "12345789",
				"2007-01-01T00:00:00.000Z", "female", "EG", "Tester");

		List<Employee> allEmployees = Arrays.asList(employee1, employee2, employee3);
		Page<Employee> employeesPage = new PageImpl<Employee>(allEmployees);

		Mockito.when(employeeRepository.findById(employee1.getId())).thenReturn(Optional.of(employee1));
		Mockito.when(employeeRepository.getOne(employee1.getId())).thenReturn(employee1);
		Mockito.when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeesPage);
		Mockito.when(employeeRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

		Mockito.when(employeeRepository.existsByEmail(employee1.getEmail())).thenReturn(true);
		Mockito.when(employeeRepository.existsByEmail(employee2.getEmail())).thenReturn(true);
		Mockito.when(employeeRepository.existsByEmail("wrong_email")).thenReturn(false);

		Mockito.when(employeeRepository.existsByPhoneNumber(employee1.getPhoneNumber())).thenReturn(true);
		Mockito.when(employeeRepository.existsByPhoneNumber(employee2.getPhoneNumber())).thenReturn(true);
		Mockito.when(employeeRepository.existsByPhoneNumber("wrong_phone_number")).thenReturn(false);

		Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);
	}

	/**
	 * Validate get all employees
	 */
	@Test
	public void given3Employees_whenGetAll_thenReturn3Records() {

		// Method call
		Page<Employee> allEmployees = employeeService.getAll(PageRequest.of(0, 5));

		// Verification
		assertThat(allEmployees.getContent())
				.hasSize(3)
				.extracting(Employee::getFirstName)
				.contains("Mohamed", "Ahmed", "Sara");

		Mockito.verify(employeeRepository, Mockito.times(1)).findAll(PageRequest.of(0, 5));
		Mockito.verifyNoMoreInteractions(employeeRepository);
		Mockito.reset(employeeRepository);
	}

	/**
	 * Validate get employee by Id, findById and employee exists
	 */
	// @Disabled
	@Test
	public void whenValidId_thenEmployeeShouldBeFound() {
		// Method call
		Optional<Employee> employee = employeeService.get(FIRST_EMPLOYEE_ID);

		// Verification
		verifyFindByIdIsCalledOnce();
		assertThat(employee)
				.isNotNull()
				.isNotEmpty();
		assertThat(employee.get().getId())
				.isEqualTo(FIRST_EMPLOYEE_ID);
		assertThat(employee.get().getFirstName())
				.isEqualTo(employee1.getFirstName());
	}

	/**
	 * Validate get employee by Id using invalid Id, findById and employee IsNull
	 */
	// @Disabled
	@Test
	public void whenInValidId_thenEmployeeShouldNotBeFound() {
		// Method call
		Optional<Employee> employee = employeeService.get(INVALID_ID);

		// Verification
		verifyFindByIdIsCalledOnce();
		assertThat(employee).isEmpty();
		assertThat(employee.isPresent()).isEqualTo(false);
	}

	/**
	 * Validate employee exists by email, existsByEmail
	 */
	@Test
	public void whenValidEmail_thenEmployeeShouldExist() {
		String mail = employee1.getEmail();
		boolean doesEmployeeExist = employeeService.existsByEmail(mail);

		assertThat(doesEmployeeExist).isEqualTo(true);

		Mockito.verify(employeeRepository, VerificationModeFactory.times(1)).existsByEmail(mail);
		Mockito.reset(employeeRepository);
	}

	@Test
	public void whenNonExistingEmail_thenEmployeeShouldNotExist() {
		String wrongMail = "test_wrong@test.com";
		boolean doesEmployeeExist = employeeService.existsByEmail(wrongMail);

		assertThat(doesEmployeeExist).isEqualTo(false);

		Mockito.verify(employeeRepository, VerificationModeFactory.times(1)).existsByEmail(wrongMail);
		Mockito.reset(employeeRepository);
	}

	/**
	 * Validate save employee with valid employee
	 */
	@Test
	public void whenValidEmployee_thenEmployeeShouldBeSavedAndReturned() {
		// Data preparation
		Employee employee = employee1;

		// Method call
		Employee savedEmployee = employeeService.add(employee);

		// Verification
		assertThat(savedEmployee)
				.isNotNull()
				.extracting(Employee::getId).isNotNull();
		assertThat(savedEmployee.getEmail())
				.isEqualTo(employee.getEmail());
		assertThat(savedEmployee.getFirstName())
				.isEqualTo(employee.getFirstName());

		Mockito.verify(employeeRepository, Mockito.times(1)).save(any(Employee.class));
		Mockito.verifyNoMoreInteractions(employeeRepository);
	}

	/**
	 * Validate edit employee with valid employee
	 */
	@Test
	public void whenValidEmployee_thenEmployeeShouldBeUpdatedAndReturned() {
		// Data preparation
		Employee employee = employee1;
		final String NEW_NAME = "Mahmoud";
		employee.setFirstName(NEW_NAME);

		// Method call
		Employee savedEmployee = employeeService.edit(employee);

		// Verification
		assertThat(savedEmployee)
				.isNotNull()
				.extracting(Employee::getId).isNotNull();
		assertThat(savedEmployee.getEmail())
				.isEqualTo(employee.getEmail());
		assertThat(savedEmployee.getFirstName())
				.isEqualTo(NEW_NAME);

		Mockito.verify(employeeRepository, Mockito.times(1)).save(any(Employee.class));
		Mockito.verifyNoMoreInteractions(employeeRepository);
	}

	/**
	 * Validate delete employee with valid employee Id
	 */
	@Test
	public void whenValidEmployee_thenEmployeeShouldBeRemoved() {
		// Data preparation
		Employee employee = employee1;

		// Method call
		employeeService.delete(employee);

		// Verification
		Mockito.verify(employeeRepository, Mockito.times(1)).delete(employee);
		Mockito.verifyNoMoreInteractions(employeeRepository);
	}

	@Test
	void changeStateTest() {
		Employee employee = employee1;

		Employee savedEmployee = employeeService.add(employee);
		assertThat(savedEmployee.getState())
				.isEqualTo(EmployeeState.ADDED);

		StateMachine<EmployeeState, EmployeeEvent> checkedSM = employeeService.changeState(savedEmployee.getId(),
				EmployeeEvent.CHECK);
		assertThat(checkedSM.getState().getId())
				.isEqualTo(EmployeeState.IN_CHECK);

		Employee checkedEmployee = employeeRepository.getOne(savedEmployee.getId());
		assertThat(checkedEmployee.getState())
				.isEqualTo(EmployeeState.IN_CHECK);

		StateMachine<EmployeeState, EmployeeEvent> approvedSM = employeeService.changeState(savedEmployee.getId(),
				EmployeeEvent.APPROVE);
		assertThat(approvedSM.getState().getId())
				.isEqualTo(EmployeeState.APPROVED);
	}

	/**
	 * Verify FindById is called once
	 */
	private void verifyFindByIdIsCalledOnce() {
		Mockito.verify(employeeRepository, VerificationModeFactory.times(1)).findById(Mockito.anyLong());
		Mockito.verifyNoMoreInteractions(employeeRepository);
		Mockito.reset(employeeRepository);
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
		employee.setPassword(crypt.encode(password));
		employee.setCreatedAt(Instant.now());
		employee.setUpdatedAt(Instant.now());
		return employee;
	}
}
