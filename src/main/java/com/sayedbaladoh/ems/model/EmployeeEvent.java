package com.sayedbaladoh.ems.model;

import io.swagger.annotations.ApiModel;

/**
 * Employee Events
 * 
 * @author sayed
 *
 */
@ApiModel(description = "Employee events.")
public enum EmployeeEvent {
	ADD, CHECK, APPROVE, ACTIVATE, REJECT, DEACTIVATE
}
