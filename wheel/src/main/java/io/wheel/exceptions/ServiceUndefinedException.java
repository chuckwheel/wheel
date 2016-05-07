package io.wheel.exceptions;

import io.wheel.ErrorCode;
import io.wheel.ErrorCodeException;

public class ServiceUndefinedException extends ErrorCodeException {

	private static final long serialVersionUID = -6281361788126996801L;

	public ServiceUndefinedException(String serviceCode) {
		super(ErrorCode.UNDEFINED_SERVICE, new Object[] { serviceCode });
	}

}
