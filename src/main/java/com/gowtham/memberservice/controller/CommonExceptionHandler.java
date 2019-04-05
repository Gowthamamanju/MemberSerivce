package com.gowtham.memberservice.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gowtham.memberservice.exception.MemberBaseException;
import com.gowtham.memberservice.exception.MemberNotFoundException;

public class CommonExceptionHandler {

	@ExceptionHandler(MemberBaseException.class)
	void handleBaseException(MemberBaseException e, HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(DefaultErrorAttributes.class.getName() + ":ERROR", e);
		sendHttpError(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MemberNotFoundException.class)
	void handleUnknownSubscriberException(MemberNotFoundException e, HttpServletRequest request,
			HttpServletResponse response) {
		request.setAttribute(DefaultErrorAttributes.class.getName() + ":ERROR", e);
		sendHttpError(response, HttpStatus.NOT_FOUND);
	}

	protected void sendHttpError(HttpServletResponse response, HttpStatus code) {
		response.setStatus(code.value());
	}

}
