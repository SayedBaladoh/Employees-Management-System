package com.sayedbaladoh.ems.service;

import java.time.Instant;
import java.util.Date;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.EmployeeEvent;
import com.sayedbaladoh.ems.model.EmployeeState;
import com.sayedbaladoh.ems.repository.EmployeeRepository;

import lombok.extern.java.Log;

/**
 * Employee ServiceImp Test
 * 
 * @Ignor No need. It is for test with logging
 * 
 * @author Sayed Baladoh
 *
 */
@Disabled
@Log
@SpringBootTest
class EmployeeServiceImpTest1 {

	@Autowired
	EmployeeService employeeService;

	@Autowired
	EmployeeRepository employeeRepository;

	// private MockProducer<String, Employee> mockProducer;

	Employee employee;

	// private void buildMockProducer(boolean autoComplete) {
	// this.mockProducer = new MockProducer<>(autoComplete, new StringSerializer(),
	// new JsonSerializer());
	// }

	@BeforeEach
	void setUp() {
		// buildMockProducer(true);
		// MockitoAnnotations.initMocks(this);
		// when(producer.send(any(ProducerRecord.class),any(Callback.class))).thenReturn(null);
		// doNothing().when(producer).flush();
		// RecordMetadata dummyRecord = new RecordMetadata(null, 0L, 0L, 0L, 0L, 0, 0);
		// when(producer.send(Mockito.any())).thenReturn(future);
		// when(future.get()).thenReturn(dummyRecord);

		employee = new Employee();
		employee.setFirstName("Ahmed");
		employee.setLastName("Muhammad Mahmoud");
		employee.setEmail("ahmed@test.com");
		employee.setPhoneNumber("00201234567901");
		employee.setGender("male");
		employee.setCountryCode("EG");
		employee.setPosition("Technical Lead");
		employee.setBirthDate(Date.from(Instant.parse("2001-01-01T00:00:00.000Z")));
		employee.setPassword("12345");
	}

	@Disabled
	@Transactional
	@Test
	void changeStateTest() {
		log.info("Employee: " + employee);
		Employee savedEmployee = employeeService.add(employee);
		log.info("Should be ADDED, State: " + savedEmployee.getState());

		StateMachine<EmployeeState, EmployeeEvent> sm = employeeService.changeState(savedEmployee.getId(),
				EmployeeEvent.CHECK);
		// log.info("State after Check Event: " + sm.getState().toString());
		log.info("State Id after CHECKED Event: " + sm.getState().getId());

		Employee checkedEmployee = employeeRepository.getOne(savedEmployee.getId());
		log.info("Should be CHECKED, State: " + savedEmployee.getState());
		log.info("Checked Employee: " + checkedEmployee);

		StateMachine<EmployeeState, EmployeeEvent> approvedSM = employeeService.changeState(savedEmployee.getId(),
				EmployeeEvent.APPROVE);
		log.info("State Id after APPROVE Event: " + approvedSM.getState().getId());
		log.info("Approved Employee: " + employeeRepository.getOne(savedEmployee.getId()));
	}

	// @Transactional
	// @RepeatedTest(10)
	// void testAuth() {
	// Payment savedPayment = paymentService.newPayment(payment);
	//
	// StateMachine<PaymentState, PaymentEvent> preAuthSM =
	// paymentService.preAuth(savedPayment.getId());
	//
	// if (preAuthSM.getState().getId() == PaymentState.PRE_AUTH) {
	// System.out.println("Payment is Pre Authorized");
	//
	// StateMachine<PaymentState, PaymentEvent> authSM =
	// paymentService.authorizePayment(savedPayment.getId());
	//
	// System.out.println("Result of Auth: " + authSM.getState().getId());
	// } else {
	// System.out.println("Payment failed pre-auth...");
	// }
	// }

	// @Test
	// void test() {
	// fail("Not yet implemented");
	// }
}
