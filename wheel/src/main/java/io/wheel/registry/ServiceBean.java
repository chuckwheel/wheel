package io.wheel.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ServiceBean {
	
	// 服务组
	String value() default "";
	// 超时
	int timeout() default 5;
	// 注册中心
	String registry() default "*";
	// 发布协议
	String protocol() default "*";
}
