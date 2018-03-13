package de.spring.example.rest.exception;

import org.resthub.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import de.spring.example.rest.filter.UsernameFilter;

@RestControllerAdvice
public class SpringExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(UsernameFilter.class);
	
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOGGER.error("handleMethodArgumentNotValid: ", ex);
		
		return new ResponseEntity<Object>( ex.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler({ NotFoundException.class })
	protected ResponseEntity<Object> handleResourceNotFound(final NotFoundException ex, final WebRequest request) {
		LOGGER.info("handleResourceNotFound: ", ex);
		
		return new ResponseEntity<Object>( ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler({ Exception.class })
	protected ResponseEntity<Object> handleApplicationException(final Exception ex, final WebRequest request) {
		LOGGER.error("handleException: ", ex);
	
		return new ResponseEntity<Object>( ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
