package io.wheel.exceptions;

import io.wheel.ErrorCode;
import io.wheel.SysException;

public class NoProtocolException extends SysException {

	private static final long serialVersionUID = -6281361788126996801L;

	public NoProtocolException(String protocol) {
		super(ErrorCode.NO_PROTOCOL, new Object[] { protocol });
	}

}
