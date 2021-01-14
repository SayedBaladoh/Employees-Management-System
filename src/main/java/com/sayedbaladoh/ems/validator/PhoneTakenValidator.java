package com.sayedbaladoh.ems.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.sayedbaladoh.ems.service.EmployeeService;

public class PhoneTakenValidator implements ConstraintValidator<PhoneTaken, String> {

	@Autowired
	EmployeeService employeeService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		if (employeeService != null)
			return !employeeService.existsByPhoneNumber(value);

		return true;
	}

}
