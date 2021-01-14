package com.sayedbaladoh.ems.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.statemachine.StateMachine;

import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.EmployeeEvent;
import com.sayedbaladoh.ems.model.EmployeeState;

public interface EmployeeService {

	Employee add(Employee employee);

	Employee edit(Employee employee);

	StateMachine<EmployeeState, EmployeeEvent> changeState(Long employeeId, EmployeeEvent event);

	Page<Employee> getAll(Pageable pageable);

	Optional<Employee> get(Long id);

	boolean exists(Long id);

	boolean exists(String employeeName);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	void delete(Employee employee);

}
