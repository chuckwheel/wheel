package io.wheel.exceptions;

import io.wheel.ErrorCode;
import io.wheel.ErrorCodeException;

public class NoServiceException extends ErrorCodeException {

	private static final long serialVersionUID = -6281361788126996801L;

	public NoServiceException(String serviceCode) {
		super(ErrorCode.NO_SERVICE, new Object[] { serviceCode });
	}

}
