package com.sayedbaladoh.ems.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StateEvent {

	/**
	 * The event to change the employee state.
	 */
	@ApiModelProperty(
			notes = "The event to change the employee state.")
	private EmployeeEvent event;
}
