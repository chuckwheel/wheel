package io.hsf.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.hsf.test.service.HelloService;
import io.hsf.test.service.UserService;

public class RpcClient {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/spring/wheel-context.xml","classpath:clientContext.xml");
		
		testNetty(context);
//		testHttp(context);
		
	}
	
	private static void testNetty(ApplicationContext context){
		HelloService helloService = context.getBean(HelloService.class);
		long a = System.currentTimeMillis();
		for(int i=0;i<100000;i++){
			try{
				helloService.print(i, "-88888");
//				Thread.sleep(1000);
//				System.out.println("==============>" + i);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("------->" + i);
			}
		}
		long b = System.currentTimeMillis();
		System.out.println(b-a);
	}
	
	private static void testHttp(ApplicationContext context){
		UserService userService = context.getBean(UserService.class);
		long a = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			try{
				userService.register(i);
//				Thread.sleep(1000);
				
			}catch(Exception e){
				System.out.println("------->" + i);
			}
		}
		long b = System.currentTimeMillis();
		System.out.println(b-a);
	}
}
