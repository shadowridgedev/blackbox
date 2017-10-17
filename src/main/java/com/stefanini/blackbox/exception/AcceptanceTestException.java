package com.stefanini.blackbox.exception;

public class AcceptanceTestException extends RuntimeException {

	private static final long serialVersionUID = 261702672695497105L;

	public AcceptanceTestException(Exception e) {
		super(e);
	}
	
}
