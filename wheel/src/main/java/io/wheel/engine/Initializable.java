package io.wheel.engine;

/**
 * 自定义初始化接口
 * 
 * @author chuck
 * @since 2013-10-10
 * @version 1.0
 */
public interface Initializable {

	/**
	 * 初始化顺序
	 * 
	 * @return
	 */
	int index();

	/**
	 * 初始化方法
	 * 
	 * @throws Exception
	 */
	void init() throws Exception;

	/**
	 * 
	 * @throws Exception
	 */
	// void destroy() throws Exception;

}
