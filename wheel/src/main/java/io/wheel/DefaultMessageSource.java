package io.wheel;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * MessageSourceHelper
 * 
 * @author chuck
 * @since 2013-10-10
 * @version 1.0
 */
public class DefaultMessageSource implements MessageSourceAware {

	private static volatile MessageSource messageSource;

	@Override
	public void setMessageSource(MessageSource messageSource) {
		DefaultMessageSource.messageSource = messageSource;
	}

	public static String getMessage(String code) {
		return getMessage(code, null, code);
	}

	public static String getMessage(String code, Object[] args) {
		return getMessage(code, args, code);
	}

	public static String getMessage(String code, String defaultMessage) {
		return getMessage(code, null, defaultMessage);
	}

	public static String getMessage(String code, Object[] args, String defaultMessage) {
		return getMessage(code, args, defaultMessage, Locale.getDefault());
	}

	public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		if (messageSource == null) {
			return defaultMessage;
		}
		return messageSource.getMessage(code, args, defaultMessage, locale);
	}

}
