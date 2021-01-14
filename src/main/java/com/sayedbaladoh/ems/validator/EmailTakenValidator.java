package com.sayedbaladoh.ems.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.sayedbaladoh.ems.service.EmployeeService;

public class EmailTakenValidator implements ConstraintValidator<EmailTaken, String> {

	@Autowired
	EmployeeService employeeService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		if (value != null && employeeService != null)
			return !employeeService.existsByEmail(value);

		return true;
	}

}
