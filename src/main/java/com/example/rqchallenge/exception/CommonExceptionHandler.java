package com.example.rqchallenge.exception;

import com.example.rqchallenge.modal.ErrorResponse;
import com.example.rqchallenge.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class CommonExceptionHandler {

	@Autowired
	private ObjectMapper objectMapper;

	@ExceptionHandler(HttpClientErrorException.BadRequest.class)
	public ResponseEntity<ErrorResponse> handleBadRequestException(HttpClientErrorException.BadRequest ex) {
		ErrorResponse errorResponse = processErrorResponse(ex.getResponseBodyAsString(), Constants.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(HttpClientErrorException.Forbidden.class)
	public ResponseEntity<ErrorResponse> handleForbiddenException(HttpClientErrorException.Forbidden ex) {
		ErrorResponse errorResponse = processErrorResponse(ex.getResponseBodyAsString(), Constants.FORBIDDEN);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	@ExceptionHandler(HttpClientErrorException.NotFound.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(HttpClientErrorException.NotFound ex) {
		ErrorResponse errorResponse = processErrorResponse(ex.getResponseBodyAsString(), Constants.NOT_FOUND);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(value = { HttpServerErrorException.ServiceUnavailable.class, ResourceAccessException.class })
	public ResponseEntity<ErrorResponse> handleServiceUnavailableException(
			HttpServerErrorException.ServiceUnavailable ex) {
		ErrorResponse errorResponse = processErrorResponse(ex.getResponseBodyAsString(), Constants.SERVICE_UNAVAILABLE);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
		ErrorResponse errorResponse = processErrorResponse(ex.getResponseBodyAsString(), ex.getStatusText());
		return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
	}

	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException ex) {
		ErrorResponse errorResponse = processErrorResponse(ex.getResponseBodyAsString(), ex.getStatusText());
		return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
	}

	@ExceptionHandler(value = { HttpServerErrorException.InternalServerError.class, RestClientException.class,
			Exception.class })
	public ResponseEntity<ErrorResponse> handleInternalServerErrorException(
			HttpServerErrorException.InternalServerError ex) {
		ErrorResponse errorResponse = processErrorResponse(ex.getResponseBodyAsString(),
				Constants.INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	private ErrorResponse processErrorResponse(String responseBodyAsString, String status) {
		ErrorResponse errorResponse;
		try {
			errorResponse = objectMapper.readValue(responseBodyAsString, ErrorResponse.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		if (errorResponse.getStatus() == null) {
			errorResponse.setStatus(status);
		}
		return errorResponse;
	}
}