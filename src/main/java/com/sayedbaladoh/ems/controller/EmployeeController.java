package com.sayedbaladoh.ems.controller;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sayedbaladoh.ems.errorhandler.ResourceNotFoundException;
import com.sayedbaladoh.ems.model.Employee;
import com.sayedbaladoh.ems.model.IdentityAvailability;
import com.sayedbaladoh.ems.model.StateEvent;
import com.sayedbaladoh.ems.service.EmployeeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Employee REST Controller provides REST APIs for <code>Employee<code> CRUD
 * operations
 * 
 * @author Sayed Baladoh
 *
 */
@Api(
		value = "Employee Controller",
		description = "REST APIs for Employee's Operations")
@RequestMapping("/api/employees")
@RestController
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;

	/**
	 * Add a new employee.
	 * 
	 * @param employee
	 *            A valid employee details.
	 * @return Employee details with Id.
	 */
	@ApiOperation(
			value = "Add a new employee.",
			response = Employee.class)
	@PostMapping
	public ResponseEntity<?> addEmployee(@Valid @RequestBody Employee employee) {
		Employee savedEmployee = employeeService.add(employee);
		return ResponseEntity.status(201).body(savedEmployee);
	}

	/**
	 * Update an existing employee.
	 * 
	 * @param employeeId
	 *            The employee Id.
	 * @param employeeRequest
	 *            A valid employee details.
	 * @return Employee with updated details.
	 */
	@ApiOperation(
			value = "Update an existing employee.",
			response = Employee.class)
	// @ApiImplicitParams({
	// @ApiImplicitParam(
	// name = "Authorization",
	// value = "Authorization token",
	// required = true,
	// dataType = "string",
	// paramType = "header") })
	@PutMapping("/{employeeId}")
	public Employee editEmployee(@PathVariable Long employeeId, @RequestBody Employee employeeRequest) {
		return employeeService.get(employeeId).map(employee -> {
			employeeRequest.setId(employeeId);
			return employeeService.edit(employeeRequest);
		}).orElseThrow(() -> new ResourceNotFoundException("Employee", "Id", employeeId));
	}

	/**
	 * Change the state of a given employee.
	 * 
	 * @param employeeId
	 *            The employee Id.
	 * @param stateEvent
	 *            The event to change employee state.
	 * @return The updated employee.
	 */
	@ApiOperation(
			value = "Change the state of a given employee.",
			response = Employee.class)
	@PatchMapping("/{employeeId}/state")
	public Employee changeEmployeeState(@PathVariable Long employeeId, @RequestBody StateEvent stateEvent) {
		return employeeService.get(employeeId).map(employee -> {
			employeeService.changeState(employeeId, stateEvent.getEvent());
			return employeeService.get(employeeId).get();
		}).orElseThrow(() -> new ResourceNotFoundException("Employee", "Id", employeeId));
	}

	/**
	 * Delete an existing employee.
	 * 
	 * @param employeeId
	 *            The employee Id.
	 * @return
	 */
	@ApiOperation(
			value = "Delete an existing employee by Id.")
	// @ApiImplicitParams({
	// @ApiImplicitParam(
	// name = "Authorization",
	// value = "Authorization token",
	// required = true,
	// dataType = "string",
	// paramType = "header") })
	@DeleteMapping("/{employeeId}")
	public ResponseEntity<?> deleteEmployee(@PathVariable Long employeeId) {
		return employeeService.get(employeeId).map(employee -> {
			employeeService.delete(employee);
			return ResponseEntity.ok().build();
		}).orElseThrow(() -> new ResourceNotFoundException("Employee", "Id", employeeId));
	}

	/**
	 * Get a page contains a list of available employees.
	 * 
	 * @param pageable
	 *            Query page and sort options.
	 * @return A page contains list of available employees.
	 */
	@ApiOperation(
			value = "View a page contains a list of available employees.",
			response = Page.class)
	// @ApiImplicitParams({
	// @ApiImplicitParam(
	// name = "Authorization",
	// value = "Authorization token",
	// required = true,
	// dataType = "string",
	// paramType = "header") })
	@GetMapping()
	public Page<Employee> getEmployees(Pageable pageable) {
		return employeeService.getAll(pageable);
	}

	/**
	 * Get the employee profile by Id.
	 * 
	 * @param id
	 *            The employee Id.
	 * @return The employee profile details.
	 */
	@ApiOperation(
			value = "Get the employee profile by Id.",
			response = Employee.class)
	@GetMapping("/{id}")
	public Employee getEmployeeProfile(@PathVariable(
			value = "id") Long id) {
		return employeeService.get(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee", "Id",
						id));
	}

	/**
	 * Check if a phone number is available for a new employee.
	 * 
	 * @param phoneNumber
	 *            The employee phone number.
	 * @return
	 */
	@ApiOperation(
			value = "Check if a phone number is available for a new employee.",
			response = IdentityAvailability.class)
	@GetMapping("/availabile/phone/{phoneNumber}")
	public IdentityAvailability checkPhoneNumberAvailability(@PathParam(
			value = "phoneNumber") String phoneNumber) {
		Boolean isAvailable = !employeeService.exists(phoneNumber);
		return new IdentityAvailability(isAvailable);
	}

	/**
	 * Check if an email is available for a new employee.
	 * 
	 * @param email
	 *            The employee email.
	 * @return
	 */
	@ApiOperation(
			value = "Check if an email is available for a new employee.",
			response = IdentityAvailability.class)
	@GetMapping("/availabile/email/{email}")
	public IdentityAvailability checkEmailAvailability(@PathParam(
			value = "email") String email) {
		Boolean isAvailable = !employeeService.existsByEmail(email);
		return new IdentityAvailability(isAvailable);
	}

}
