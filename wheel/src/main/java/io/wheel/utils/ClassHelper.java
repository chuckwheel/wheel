package io.wheel.utils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;

/**
 * ClassHelper
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class ClassHelper {
	public static String[] getSimplePropertyNames(Class<?> clazz) {

		PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
		List<String> result = new ArrayList<String>();
		if (propertyDescriptors != null) {
			for (PropertyDescriptor descriptor : propertyDescriptors) {
				if (descriptor == null)
					continue;
				if (!(descriptor instanceof IndexedPropertyDescriptor)) {
					result.add(descriptor.getName());
				}
			}
		}
		return result.toArray(new String[] {});
	}

	public static String[] getIndexedPropertyNames(Class<?> clazz) {
		PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
		List<String> result = new ArrayList<String>();
		if (propertyDescriptors != null) {
			for (PropertyDescriptor descriptor : propertyDescriptors) {
				if (descriptor == null)
					continue;
				if (descriptor instanceof IndexedPropertyDescriptor) {
					result.add(descriptor.getName());
				}
			}
		}
		return result.toArray(new String[] {});
	}

	public static String[] getPropertyNames(Class<?> clazz) {
		PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
		List<String> result = new ArrayList<String>();
		if (propertyDescriptors != null) {
			for (PropertyDescriptor descriptor : propertyDescriptors) {
				if (descriptor == null)
					continue;
				result.add(descriptor.getName());
			}
		}
		return result.toArray(new String[] {});
	}

	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
		return BeanUtils.getPropertyDescriptors(clazz);
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {

		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (Exception e) {
			return null;
		}
	}

	public static Method getMethodByName(Class<?> clazz, String methodName) {
		if (clazz == null)
			throw new NullPointerException("Parameter clazz cann't be null!");
		Method[] methods = clazz.getMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (method.getName().equals(methodName))
					return method;
			}
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public static <T> T readStaticFieldValue(Class<?> clazz, String fieldName) {
		try {
			return (T) FieldUtils.readDeclaredStaticField(clazz, fieldName, true);
		} catch (Throwable t) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T readStaticFieldValue(String className, String fieldName) {
		try {
			return (T) FieldUtils.readDeclaredStaticField(Class.forName(className), fieldName, true);
		} catch (Throwable t) {
			return null;
		}
	}

	public static <T> T readStaticFieldValue(String expression) {
		if (expression == null)
			return null;
		int index = expression.lastIndexOf(".");
		if (index == -1)
			return null;
		String className = expression.substring(0, index);
		String fieldName = expression.substring(index + 1);
		if (StringUtils.isEmpty(className) || StringUtils.isEmpty(fieldName))
			return null;
		return readStaticFieldValue(className, fieldName);
	}

	/*
	 * 获得代理对象类型
	 */
	public static Class<?> getTargetClass(Object proxy) throws Exception {
		try {
			if (AopUtils.isCglibProxy(proxy)) {
				Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
				h.setAccessible(true);
				Object interceptor = h.get(proxy);
				Field a = interceptor.getClass().getDeclaredField("advised");
				a.setAccessible(true);
				return ((AdvisedSupport) a.get(interceptor)).getTargetSource().getTargetClass();
			} else if (AopUtils.isJdkDynamicProxy(proxy)) {
				Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
				h.setAccessible(true);
				AopProxy aopProxy = (AopProxy) h.get(proxy);
				Field a = aopProxy.getClass().getDeclaredField("advised");
				a.setAccessible(true);
				return ((AdvisedSupport) a.get(aopProxy)).getTargetSource().getTargetClass();
			} else {
				return proxy.getClass();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getMethodFullName(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getDeclaringClass().getName() + "#");
		sb.append(method.getName());
		sb.append("(");
		Class<?>[] pts = method.getParameterTypes();
		if (pts != null && pts.length > 0) {
			for (Class<?> t : pts) {
				sb.append(t.getSimpleName());
				sb.append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
		}
		sb.append(")");
		return sb.toString();
	}
}
