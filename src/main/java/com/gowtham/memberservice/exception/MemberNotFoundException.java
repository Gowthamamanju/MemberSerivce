package com.gowtham.memberservice.exception;

public class MemberNotFoundException extends MemberBaseException {

	private static final long serialVersionUID = -8640353979975624403L;

	public MemberNotFoundException(String message) {
		super(message);
	}

}
