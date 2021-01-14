package com.sayedbaladoh.ems.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.EmployeeEvent;
import com.sayedbaladoh.ems.model.EmployeeState;
import com.sayedbaladoh.ems.repository.EmployeeRepository;

/**
 * Employee Service implementation
 * 
 * @author Sayed Baladoh
 *
 */
@Service
public class EmployeeServiceImp implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private PasswordEncoder crypt;
	@Autowired
	private StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;
	@Autowired
	private EmployeeStateChangeInterceptor employeeStateChangeInterceptor;
	@Autowired
	private KafkaProducerService kafkaSender;

	public static final String EMPLOYEE_ID_HEADER = "employee_id";

	@Value("${kafka.topic.name}")
	private String topicName;

	@Override
	public Employee add(Employee employee) {

		employee.setState(EmployeeState.ADDED);
		if (employee.getPassword() != null && !employee.getPassword().isEmpty())
			employee.setPassword(crypt.encode(employee.getPassword()));

		Employee savedEmployee = employeeRepository.save(employee);
		kafkaSender.sendMessageWithCallback(savedEmployee, topicName);
		return savedEmployee;
	}

	@Override
	public Employee edit(Employee employee) {

		employee.setState(EmployeeState.UPDATED);
		if (employee.getPassword() != null && !employee.getPassword().isEmpty())
			employee.setPassword(crypt.encode(employee.getPassword()));

		Employee savedEmployee = employeeRepository.save(employee);
		kafkaSender.sendMessageWithCallback(savedEmployee, topicName);
		return savedEmployee;
	}

	@Override
	public StateMachine<EmployeeState, EmployeeEvent> changeState(Long employeeId, EmployeeEvent event) {

		StateMachine<EmployeeState, EmployeeEvent> sm = buildStateMachine(employeeId);
		sendEvent(employeeId, sm, event);
		return sm;
	}

	@Override
	public Page<Employee> getAll(Pageable pageable) {
		return employeeRepository.findAll(pageable);
	}

	@Override
	public Optional<Employee> get(Long id) {
		return employeeRepository.findById(id);
	}

	@Override
	public boolean exists(Long id) {
		return employeeRepository.existsById(id);
	}

	@Override
	public boolean exists(String phoneNumber) {
		return employeeRepository.existsByPhoneNumber(phoneNumber);
	}

	@Override
	public boolean existsByEmail(String email) {
		return employeeRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByPhoneNumber(String phoneNumber) {
		return employeeRepository.existsByPhoneNumber(phoneNumber);
	}

	@Override
	public void delete(Employee employee) {
		employeeRepository.delete(employee);
	}

	private StateMachine<EmployeeState, EmployeeEvent> buildStateMachine(Long employeeId) {

		Employee employee = employeeRepository.getOne(employeeId);
		StateMachine<EmployeeState, EmployeeEvent> sm = stateMachineFactory
				.getStateMachine(Long.toString(employee.getId()));
		sm.stop();
		sm.getStateMachineAccessor()
				.doWithAllRegions(sma -> {
					sma.addStateMachineInterceptor(employeeStateChangeInterceptor);
					sma.resetStateMachine(new DefaultStateMachineContext<>(employee.getState(), null, null, null));
				});
		sm.start();
		return sm;
	}

	private void sendEvent(Long employeeId, StateMachine<EmployeeState, EmployeeEvent> sm, EmployeeEvent event) {

		Message<EmployeeEvent> msg = MessageBuilder.withPayload(event)
				.setHeader(EMPLOYEE_ID_HEADER, employeeId)
				.build();
		sm.sendEvent(msg);
	}

}
