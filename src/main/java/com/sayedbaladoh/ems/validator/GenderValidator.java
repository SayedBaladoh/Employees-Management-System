package com.sayedbaladoh.ems.validator;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<Gender, String> {

	private final List<String> genderList = Arrays.asList("male", "female");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null)
			return false;

		return genderList.contains(value);
	}

}
