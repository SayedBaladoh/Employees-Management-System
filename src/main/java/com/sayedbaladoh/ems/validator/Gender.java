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
		validatedBy = GenderValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Gender {

	String message() default "{gender}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}