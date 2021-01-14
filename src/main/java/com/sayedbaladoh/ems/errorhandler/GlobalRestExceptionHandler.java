package com.sayedbaladoh.ems.errorhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sayedbaladoh.ems.model.ApiError;

import io.swagger.annotations.ApiModel;

/**
 * Exception Handler. Handle the most common client errors.
 * 
 * @author Sayed Baladoh
 *
 */
@ApiModel(
		description = "All details about the Error for providing custom error message.")
@ControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * MethodArgumentTypeMismatchException Handler handle method argument is not the
	 * expected type
	 * 
	 * @param ex
	 *            the target exception
	 * @param request
	 *            the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex,
			final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * ConstraintViolationException Handler handle constraint violation exception.
	 * 
	 * @param ex
	 *            the target exception
	 * @param request
	 *            the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex,
			final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final List<String> errors = new ArrayList<String>();
		for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
					+ violation.getMessage());
		}

		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * BadCredentialsException Handler provides handling Bad Credentials Exception.
	 * 
	 * @param ex
	 *            the target exception
	 * @param request
	 *            the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@ExceptionHandler({ BadCredentialsException.class })
	public ResponseEntity<Object> handleBadCredentials(final BadCredentialsException ex, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(),
				ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * ResourceNotFoundException Handler handle resource not found exception.
	 * 
	 * @param ex
	 *            the target exception
	 * @param request
	 *            the current request
	 * @return a {@code ResponseEntity} instance
	 */
	protected ResponseEntity<Object> handleResourceNotFoundException(final ResourceNotFoundException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(),
				ex.getMessage());

		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * UsernameNotFoundException Handler handle user not found exception.
	 * 
	 * @param ex
	 *            the target exception
	 * @param request
	 *            the current request
	 * @return a {@code ResponseEntity} instance
	 */
	protected ResponseEntity<Object> handleUsernameNotFoundExceptionException(final UsernameNotFoundException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(),
				ex.getMessage());

		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * MethodArgumentNotValidException Handler handle method argument not valid.
	 * 
	 * MethodArgumentNotValidException: This exception is thrown when argument
	 * annotated with @Valid failed validation.
	 * 
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final List<String> errors = new ArrayList<String>();
		for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}

		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
		return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
	}

	/**
	 * BindException Handler: This exception is thrown when fatal binding errors
	 * occur.
	 */
	@Override
	protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers,
			final HttpStatus status, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final List<String> errors = new ArrayList<String>();
		for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": "
					+ error.getDefaultMessage());
		}
		for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}
		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
		return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
	}

	/**
	 * TypeMismatchException Handler: This exception is thrown when try to set bean
	 * property with wrong type.
	 */
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers,
			final HttpStatus status, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final Map<String, List<Error>> errors = new HashMap<>();
		final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type "
				+ ex.getRequiredType();

		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * MissingServletRequestPartException Handler: This exception is thrown when
	 * when the part of a multipart request not found.
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());
		//
		final String error = ex.getRequestPartName() + " part is missing";
		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * MissingServletRequestParameterException Handler: This exception is thrown
	 * when request missing parameter.
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status,
			final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());
		//
		final String error = ex.getParameterName() + " parameter is missing";
		final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * NoHandlerFoundException Handler handle no handler found exception.
	 */
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

		final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * HttpRequestMethodNotSupportedException Handler handle request with an
	 * unsupported HTTP method.
	 */
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status,
			final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName());

		final StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request. Supported methods are ");
		ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

		final ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(),
				builder.toString());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}

	/**
	 * Default Handler handle other exceptions that don't have specific handlers
	 * 
	 * @param ex
	 *            the target exception
	 * @param request
	 *            the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
		logger.error("Error occurred. Class: " + ex.getClass().getName() + ", error: ", ex);

		final ApiError apiError;
		if (ex instanceof ResourceNotFoundException)
			apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(),
					ex.getMessage());
		else
			apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(),
					ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}
}
