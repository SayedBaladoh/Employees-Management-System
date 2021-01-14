package com.sayedbaladoh.ems.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator of country code.
 * 
 * Check if a string is ISO country of ISO language in Java
 * 
 * @author Sayed Baladoh
 *
 */
public class CountryValidator implements ConstraintValidator<Country, String> {

	private final Set<String> ISO_LANGUAGES = new HashSet<String>(Arrays.asList(Locale.getISOLanguages()));
	private final Set<String> ISO_COUNTRIES = new HashSet<String>(Arrays.asList(Locale.getISOCountries()));

	public boolean isValidISOLanguage(String s) {
		return ISO_LANGUAGES.contains(s);
	}

	public boolean isValidISOCountry(String s) {
		return ISO_COUNTRIES.contains(s);
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null)
			return false;

		return isValidISOCountry(value);
	}

}
