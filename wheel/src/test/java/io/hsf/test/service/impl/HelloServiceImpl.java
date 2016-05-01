package io.hsf.test.service.impl;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.hsf.test.service.HelloService;
import io.wheel.engine.ServiceContext;
import io.wheel.registry.ServiceBean;
import io.wheel.registry.ServiceImporter;
import io.wheel.registry.ServiceMethod;

@ServiceBean
public class HelloServiceImpl implements HelloService {
	private static Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
	@Override
	@ServiceMethod(timeout = 30)
	public String hello(String name) {
		logger.info(Thread.currentThread().getName() + "-Hello "+name + "!");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Object> attributes = ServiceContext.get().getRequest().getAttributes();
		logger.info(""+attributes);
		ServiceContext.get().getResponse().setAttribute("rspCode", "9999999999");
		return "Hello " + name;
	}

	@Override
	public long print(long a, String b) {
		// System.out.println("a:"+a+",b:"+b);
		return a;
	}
}
