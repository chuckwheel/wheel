package io.hsf.test.service.impl;

import java.lang.reflect.Method;

import io.hsf.test.service.HelloService;
import io.wheel.registry.ServiceBean;
import io.wheel.registry.ServiceMethod;

@ServiceBean
public class HelloServiceImpl implements HelloService {

	@Override
	@ServiceMethod(timeout = 30)
	public String hello(String name) {
		// System.out.println("Hello "+name + "!");
		return "Hello " + name;
	}

	@Override
	public long print(long a, String b) {
		// System.out.println("a:"+a+",b:"+b);
		return a;
	}
}
