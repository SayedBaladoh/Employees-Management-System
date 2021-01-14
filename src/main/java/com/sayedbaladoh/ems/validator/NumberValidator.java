package com.sayedbaladoh.ems.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validate value is Number
 * @author Sayed Baladoh
 *
 */
public class NumberValidator implements ConstraintValidator<Number, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		// validate is Number
		if(value !=null)
			return (value.matches("\\+?\\d+"));
		
		return true;
	}

}
