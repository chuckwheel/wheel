package io.hsf.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcServer2 {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/spring/wheel-context.xml","classpath:serverContext2.xml");
	}
	
}
