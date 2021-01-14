package com.sayedbaladoh.ems.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * The Error information for providing custom error message.
 * 
 * @author Sayed Baladoh
 * 
 */
@ApiModel(
		description = "All details about the Error for providing custom error message.")
/**
 * The Job entity. All details about employee's work experience.
 * 
 * Extends <code>DateAudit</code> to automatically populate createdAt and
 * updatedAt values when we persist an <code>Job</code> entity.
 * 
 * @author Sayed Baladoh
 *
 */
@Getter
@Setter
public class ApiError {
	/**
	 * The time stamp for the error
	 */
	@JsonFormat(
			shape = JsonFormat.Shape.STRING,
			pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;

	/**
	 * The HTTP status code.
	 */
	private HttpStatus status;

	/**
	 * The error message associated with the exception.
	 */
	@JsonIgnore
	private String message;

	/**
	 * List of constructed error messages.
	 */
	private List<String> errors;

	/**
	 * Initialize <code>ApiError</code> with default values.
	 */
	public ApiError() {
		timestamp = LocalDateTime.now();
	}

	/**
	 * Initialize <code>ApiError</code> with specified parameters.
	 * 
	 * @param status
	 *            The HTTP status code.
	 * @param message
	 *            The error message associated with exception.
	 * @param error
	 *            List of constructed error messages.
	 */
	public ApiError(HttpStatus status, String message, List<String> errors) {
		this();
		this.status = status;
		this.message = message;
		this.errors = errors;
	}

	/**
	 * Initialize <code>ApiError</code> with specified parameters.
	 * 
	 * @param status
	 *            The HTTP status code.
	 * @param message
	 *            The error message associated with exception.
	 * @param error
	 *            The constructed error message.
	 */
	public ApiError(HttpStatus status, String message, String error) {
		this();
		this.status = status;
		this.message = message;
		errors = Arrays.asList(error);
	}
}
