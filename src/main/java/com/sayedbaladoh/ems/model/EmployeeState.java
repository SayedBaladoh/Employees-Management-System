package com.sayedbaladoh.ems.model;

import io.swagger.annotations.ApiModel;

/**
 * Employee States
 * 
 * @author sayed
 *
 */
@ApiModel(description = "Employee states.")
public enum EmployeeState {
	ADDED, UPDATED, IN_CHECK, APPROVED, REJECTED, ACTIVE, INACTIVE
}
