package com.sts.fincub.usermanagement.exception;

import com.sts.fincub.usermanagement.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Shahzad Hussain
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private static final String ERROR_MSG = "Something went Wrong, Please try Later";

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public ResponseEntity<Object> handleException(Exception ex) {
		logger.info("Exception  occurs => {}", ex);
		return new ResponseEntity<>(new Response<>(ERROR_MSG, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = BadRequestException.class)
	@ResponseBody
	public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
		logger.info("BadRequestException occurs => {}", ex.getMessage());
		return new ResponseEntity<>(new Response(ex.getMessage(), ex.getHttpStatus()), HttpStatus.OK);
	}

	@ExceptionHandler(value = ObjectNotFoundException.class)
	@ResponseBody
	public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException ex) {
		logger.info("ObjectNotFoundException occurs => {}", ex.getMessage());
		return new ResponseEntity<>(new Response(ex.getMessage(), ex.getHttpStatus()), HttpStatus.OK);
	}

	@ExceptionHandler(value = InternalServerErrorException.class)
	@ResponseBody
	public ResponseEntity<Object> handleInternalServerErrorException(InternalServerErrorException ex) {
		logger.info("InternalServerException occurs => {}", ex.getMessage());
		return new ResponseEntity<>(new Response(ex.getMessage(), ex.getHttpStatus()), HttpStatus.OK);
	}
	@ExceptionHandler(value = MissingPathVariableException.class)
	@ResponseBody
	public ResponseEntity<Object> handleMissingPathVariableException(MissingPathVariableException ex) {
		logger.info("HttpStatus.OK occurs => {}", ex.getMessage());
		return new ResponseEntity<>(new Response(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.OK);
	}

	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException ex) {
		logger.info("HttpRequestMethodNotSupportedException occurs => {}", ex);
		return new ResponseEntity<>(new Response(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	@ResponseBody
	public ResponseEntity<Object> handleException(MissingServletRequestParameterException ex) {
		logger.info("MissingServletRequestParameterException occurs => {}", ex);
		return new ResponseEntity<>(new Response(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

}