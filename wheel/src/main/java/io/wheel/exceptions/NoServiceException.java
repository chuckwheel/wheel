package io.wheel.exceptions;

import io.wheel.ErrorCode;
import io.wheel.SysException;

public class NoServiceException extends SysException {

	private static final long serialVersionUID = -6281361788126996801L;

	public NoServiceException(String serviceCode) {
		super(ErrorCode.NO_SERVICE, new Object[] { serviceCode });
	}

}
