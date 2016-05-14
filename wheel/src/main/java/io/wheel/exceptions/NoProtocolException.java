package io.wheel.exceptions;

import io.wheel.ErrorCode;
import io.wheel.ErrorCodeException;

public class NoProtocolException extends ErrorCodeException {

	private static final long serialVersionUID = -6281361788126996801L;

	public NoProtocolException(String protocol) {
		super(ErrorCode.NO_PROTOCOL, new Object[] { protocol });
	}

}
