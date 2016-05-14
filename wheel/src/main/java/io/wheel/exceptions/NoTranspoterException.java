package io.wheel.exceptions;

import io.wheel.ErrorCode;
import io.wheel.ErrorCodeException;

public class NoTranspoterException extends ErrorCodeException {

	private static final long serialVersionUID = -6281361788126996801L;

	public NoTranspoterException(String transporter) {
		super(ErrorCode.NO_TRANSPORTER, new Object[] { transporter });
	}

}
