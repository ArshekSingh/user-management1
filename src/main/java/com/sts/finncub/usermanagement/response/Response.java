package com.sts.finncub.usermanagement.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author Shahzad Hussain
 */
@Data
@NoArgsConstructor
public class Response<T> {

	private int code;
	private String message;
	private HttpStatus status;
	private T responseObject;

	public Response(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
		this.code = status.value();
	}

	public Response(String message, T responseObject, HttpStatus status) {
		this.message = message;
		this.status = status;
		this.code = status.value();
		this.responseObject = responseObject;
	}

}
