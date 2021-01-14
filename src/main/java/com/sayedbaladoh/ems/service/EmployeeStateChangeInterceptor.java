package com.sayedbaladoh.ems.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.EmployeeEvent;
import com.sayedbaladoh.ems.model.EmployeeState;
import com.sayedbaladoh.ems.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * 
 */
@Log
@RequiredArgsConstructor
@Component
public class EmployeeStateChangeInterceptor extends StateMachineInterceptorAdapter<EmployeeState, EmployeeEvent> {

	private final EmployeeRepository employeeRepository;
	private final KafkaProducerService kafkaSender;

	@Value("${kafka.topic.name}")
	private String topicName;

	@Override
	public void preStateChange(State<EmployeeState, EmployeeEvent> state, Message<EmployeeEvent> message,
			Transition<EmployeeState, EmployeeEvent> transition,
			StateMachine<EmployeeState, EmployeeEvent> stateMachine) {

		Optional.ofNullable(message).ifPresent(msg -> {
			Optional.ofNullable(
					Long.class.cast(msg.getHeaders().getOrDefault(EmployeeServiceImp.EMPLOYEE_ID_HEADER, -1L)))
					.ifPresent(employeeId -> {
						log.info(String.format("State will changed for Employee Id: %s, to State: -> %s.", employeeId,
								state.getId()));
						Employee employee = employeeRepository.getOne(employeeId);
						employee.setState(state.getId());
						employeeRepository.save(employee);
						kafkaSender.sendMessageWithCallback(employee, topicName);
					});
		});
	}
}
