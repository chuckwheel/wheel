package io.wheel.exceptions;

import io.wheel.ErrorCode;
import io.wheel.ErrorCodeException;

public class NoProviderException extends ErrorCodeException {

	private static final long serialVersionUID = -6281361788126996801L;

	public NoProviderException(String serviceCode) {
		super(ErrorCode.NO_PROVIDER, new Object[] { serviceCode });
	}

}
