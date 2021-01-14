package com.sayedbaladoh.ems.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.EmployeeState;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class EmployeeRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private EmployeeRepository employeeRepository;

	private PasswordEncoder cryptEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	/**
	 * Validate findAll
	 */
	// @Disabled
	@Test
	public void givenSetOfEmployees_whenFindAll_thenReturnPageofAllEmployees() {
		// Data preparation
		Employee employee1 = getEmployee();
		Employee employee2 = getEmployee("Ahmed", "Mohamed", "00201235478912", "test2@test.com", "12345789",
				"2005-01-01T00:00:00.000Z", "male", "EG", "Developer");
		Employee employee3 = getEmployee("Sara", "Ahmed", "00201235478921", "test3@test.com", "12345789",
				"2007-01-01T00:00:00.000Z", "female", "EG", "Tester");

		entityManager.persist(employee1);
		entityManager.persist(employee2);
		entityManager.persist(employee3);
		entityManager.flush();

		// Method call
		Page<Employee> allEmployees = employeeRepository.findAll(PageRequest.of(0, 3));

		// Verification
		assertThat(allEmployees.getContent())
				.hasSize(3)
				.extracting(Employee::getFirstName)
				.containsOnly(employee1.getFirstName(), employee2.getFirstName(), employee3.getFirstName());

	}

	/**
	 * Validate findById with valid Id
	 */
	// @Disabled
	@Test
	public void givenEmployeeInDB_WhenFindById_ThenReturnOptionalWithEmployee() {

		// Data preparation
		Employee employee = getEmployee();
		entityManager.persistAndFlush(employee);

		// Method call
		Optional<Employee> found = employeeRepository.findById(employee.getId());

		// Verification
		assertThat(found.isPresent())
				.isEqualTo(true);
		assertThat(found.get().getEmail())
				.isEqualTo(employee.getEmail());
		assertThat(found.get().getFirstName())
				.isEqualTo(employee.getFirstName());
	}

	/**
	 * Validate findById with invalid Id
	 */
	// @Disabled
	@Test
	public void givenEmptyDB_WhenFindById_ThenReturnEmptyOptional() {
		// Method call
		Optional<Employee> foundEmployee = employeeRepository.findById(-1L);

		// Verification
		assertThat(foundEmployee.isPresent()).isEqualTo(false);
	}

	/**
	 * Validate findByEmail with valid email
	 */
	@Test
	public void givenEmployeeInDB_WhenFindByEmail_ThenReturnOptionalWithEmployee() {
		// Data preparation
		Employee employee = getEmployee();
		entityManager.persistAndFlush(employee);

		// Method call
		Optional<Employee> found = employeeRepository.findByEmail(employee.getEmail());

		// Verification
		assertThat(found.isPresent())
				.isEqualTo(true);
		assertThat(found.get().getEmail())
				.isEqualTo(employee.getEmail());
		assertThat(found.get().getFirstName())
				.isEqualTo(employee.getFirstName());
	}

	/**
	 * Validate findByEmail with invalid email
	 */
	@Test
	public void givenEmptyDB_WhenFindByEmail_ThenReturnEmptyOptional() {
		// Method call
		Optional<Employee> found = employeeRepository.findByEmail("doesNotExist");

		// Verification
		assertThat(found.isPresent())
				.isEqualTo(false);
		assertThat(found.isEmpty());
	}

	/**
	 * Validate save employee with valid employee
	 */
	// @Disabled
	@Test
	public void whenValidEmployee_thenEmployeeShouldBeSavedAndReturned() {
		// Data preparation
		Employee employee = getEmployee();

		// Method call
		Employee savedEmployee = employeeRepository.save(employee);

		// Verification
		assertThat(savedEmployee)
				.isNotNull()
				.extracting(Employee::getId)
				.isNotNull()
				.isNotEqualTo("");
		assertThat(savedEmployee.getEmail())
				.isEqualTo(employee.getEmail());
		assertThat(savedEmployee.getFirstName())
				.isEqualTo(employee.getFirstName());
	}

	/**
	 * Validate save employee with invalid employee
	 */
	// @Disabled
	@Test
	public void whenInvalidEmployee_thenEmployeeShouldNotBeSaved() {

		Exception exception = Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			Employee savedEmployee = employeeRepository.save(null);

			assertThat(savedEmployee)
					.isNull();
		});

		String expectedMessage = "Entity must not be null.";
		String actualMessage = exception.getMessage();

		assertThat(actualMessage.contains(expectedMessage));
	}

	/**
	 * Validate edit employee with valid employee
	 */
	// @Disabled
	@Test
	public void whenValidEmployee_thenEmployeeShouldBeUpdatedAndReturned() {
		// Data preparation
		Employee employee = getEmployee();
		entityManager.persistAndFlush(employee);

		// Method call
		Employee savedEmployee = employeeRepository.save(employee);

		// Update Object
		String newName = "AHMED";
		savedEmployee.setFirstName(newName);

		// Method call
		Employee updatedEmployee = employeeRepository.save(savedEmployee);

		// Verification
		assertThat(updatedEmployee)
				.isNotNull()
				.extracting(Employee::getId).isNotNull();
		assertThat(updatedEmployee.getEmail())
				.isEqualTo(savedEmployee.getEmail());
		assertThat(updatedEmployee.getEmail())
				.isEqualTo(savedEmployee.getEmail());
		assertThat(updatedEmployee.getFirstName())
				.isEqualTo(newName);

	}

	/**
	 * Validate delete employee with valid employee Id
	 */
	@Test
	public void whenValidEmployeeId_thenEmployeeShouldBeRemoved() {
		// Data preparation
		Employee employee = getEmployee();
		employeeRepository.save(employee);

		// Method call
		employeeRepository.deleteById(employee.getId());
	}

	/**
	 * Validate delete employee with invalid employee Id
	 */
	@Test
	public void whenInvalidEmployeeId_thenEmployeeShouldNotBeRemoved() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			employeeRepository.deleteById(-1l);
		});
	}

	@AfterEach
	public void cleanUp() {
		employeeRepository.deleteAll();
	}

	private Employee getEmployee() {

		Employee employee = new Employee();
		employee.setFirstName("Ahmed");
		employee.setLastName("Muhammad Mahmoud");
		employee.setEmail("ahmed@test.com");
		employee.setPhoneNumber("00201234567901");
		employee.setGender("male");
		employee.setCountryCode("EG");
		employee.setPosition("Technical Lead");
		employee.setBirthDate(Date.from(Instant.parse("2001-01-01T00:00:00.000Z")));
		employee.setPassword(cryptEncoder.encode("12345678"));
		employee.setState(EmployeeState.ADDED);
		employee.setCreatedAt(Instant.now());
		employee.setUpdatedAt(Instant.now());
		return employee;
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
		employee.setPassword(cryptEncoder.encode(password));
		employee.setState(EmployeeState.ADDED);
		employee.setCreatedAt(Instant.now());
		employee.setUpdatedAt(Instant.now());
		return employee;
	}
}
