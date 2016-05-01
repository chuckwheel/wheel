package io.hsf.test;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.hsf.test.service.HelloService;
import io.hsf.test.service.UserService;
import io.wheel.engine.DefaultServiceExecutor;
import io.wheel.engine.RpcContext;
import io.wheel.engine.RpcResponse;

public class RpcClient {

	private static Logger logger = LoggerFactory.getLogger(RpcClient.class);
	
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/spring/wheel-context.xml","classpath:clientContext.xml");
		
//		testNetty0(context);
		testNetty5(context);
		
	}
	
	private static void testNetty0(ApplicationContext context){
		final HelloService helloService = context.getBean(HelloService.class);
		
		new Thread(){
			@Override
			public void run() {
				RpcContext.get().setAttribute("name", "chuck");
				RpcContext.get().setAttribute("code", 01);
				RpcContext.get().async();
				helloService.hello("huangchuan");
				Future<RpcResponse> f1 = RpcContext.get().getFuture();
				try {
					f1.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		new Thread(){
			@Override
			public void run() {
				RpcContext.get().setAttribute("name2", "chuck2");
				RpcContext.get().setAttribute("code2", 01);
				RpcContext.get().async();
				helloService.hello("fuck");
				Future<RpcResponse> f2 = RpcContext.get().getFuture();
			}
		}.start();
		
		new Thread(){
			@Override
			public void run() {
				RpcContext.get().setAttribute("name3", "chuck3");
				RpcContext.get().setAttribute("code3", 01);
				RpcContext.get().async();
				helloService.hello("fuck");
				Future<RpcResponse> f3 = RpcContext.get().getFuture();
				
			}
		}.start();
//		try {
//			String sss1 = f1.get().getResult();
//			String sss2 = f2.get().getResult();
//			logger.info(sss1);
//			logger.info(sss2);
//			
//			logger.info(""+f1.get().getAttributes());
//			logger.info(""+f1.get().getAttributes());
//			
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	private static void testNetty5(ApplicationContext context){
		final HelloService helloService = context.getBean(HelloService.class);
		
		RpcContext.get().setAttribute("name", "chuck");
		RpcContext.get().setAttribute("code", 01);
		RpcContext.get().async();
		helloService.hello("huangchuan");
		Future<RpcResponse> f1 = RpcContext.get().getFuture();
		
		RpcContext.get().setAttribute("name2", "chuck");
		RpcContext.get().setAttribute("code3", 01);
		RpcContext.get().async();
		helloService.hello("fuck");
		Future<RpcResponse> f2 = RpcContext.get().getFuture();
		
		RpcContext.get().setAttribute("name2", "chuck");
		RpcContext.get().setAttribute("code3", 01);
		RpcContext.get().async();
		helloService.hello("fuck2");
		Future<RpcResponse> f3 = RpcContext.get().getFuture();
		
		try {
			logger.info("f1:" + f1.get());
			logger.info("f2:" + f2.get());
			logger.info("f3:" + f3.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void testNetty1(ApplicationContext context){
		HelloService helloService = context.getBean(HelloService.class);
		
		RpcContext.get().setAttribute("name", "chuck");
		RpcContext.get().setAttribute("code", 01);
		RpcContext.get().async();
		helloService.hello("huangchuan");
		Future<RpcResponse> f1 = RpcContext.get().getFuture();
		
		RpcContext.get().setAttribute("name2", "chuck");
		RpcContext.get().setAttribute("code3", 01);
		String v = helloService.hello("fuck");
		logger.info(v);
		logger.info(""+RpcContext.get().getResponse().getAttributes());
		
		try {
			String sss1 = f1.get().getResult();
			logger.info(sss1);
			logger.info(""+f1.get().getAttributes());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	private static void testNetty2(ApplicationContext context){
		final HelloService helloService = context.getBean(HelloService.class);
		final AtomicLong ids = new AtomicLong(0);
		System.out.println("*******************>" + new Date());
		for(int k=0;k<100;k++){
			final int m = k;
			new Thread(){
				public void run() {
					long a = System.currentTimeMillis();
					for(int i=0;i<20000;i++){
						long hh = helloService.print(i,"-88888");
						if(hh!=i){
							System.err.println("-----------------------============>"+i);
						}
						long id = ids.getAndIncrement();
						if(id%1000==0){
							double b = System.currentTimeMillis();
							double x = b-a;
							Double y = (id/x)*1000;
							System.out.println("--------->" + y.longValue() );
						}
					}
					long b = System.currentTimeMillis();
					System.out.println(m + "-->" + (b-a) + "===============>" + new Date());
				};
			}.start();
			
		}
	}
}
