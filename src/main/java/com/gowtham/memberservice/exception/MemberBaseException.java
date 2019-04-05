package com.gowtham.memberservice.exception;

public abstract class MemberBaseException extends Exception {

	private static final long serialVersionUID = -7948253551762732937L;

	public MemberBaseException(String message) {
		super(message);
	}

	public MemberBaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public MemberBaseException(Throwable cause) {
		super(cause);
	}

}
