package com.sayedbaladoh.ems.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(
		validatedBy = CountryValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Country {

	String message() default "{country}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}