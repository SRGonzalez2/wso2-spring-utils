package com.microservicios.wso2_spring_utils.jwt.exceptions;

/**
 * Excepcion lanzada cuando un claim requerido no esta presente en el JWT.
 */
public class MissingClaimException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public MissingClaimException(String message) {
		super(message);
	}
	
	public MissingClaimException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
