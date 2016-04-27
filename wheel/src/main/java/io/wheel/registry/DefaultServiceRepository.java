package io.wheel.registry;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import io.wheel.engine.Initable;
import io.wheel.utils.ClassHelper;

/**
 * The default ServiceRepository implement
 * 
 * @author chuck
 * @since 2014-2-21
 * @version 1.0
 */
public class DefaultServiceRepository implements ServiceRepository, Initable, ApplicationContextAware {

	private static Logger logger = LoggerFactory.getLogger(DefaultServiceRepository.class);

	private ApplicationContext applicationContext;

	private Map<String, ServiceExp> serviceExps = new ConcurrentHashMap<String, ServiceExp>();

	private Map<String, ServiceImp> serviceImps = new ConcurrentHashMap<String, ServiceImp>();

	@Override
	public int index() {
		return 0;
	}

	@Override
	public void init() throws Exception {
		this.serviceExps.clear();
		this.registerAnnotationService();
		this.registerExportService();
		this.registerImportService();
	}

	private void registerAnnotationService() throws Exception {
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ServiceBean.class);
		if (CollectionUtils.isEmpty(beans)) {
			logger.warn("Annotation ServiceExport is empty!");
			return;
		}
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Object bean = entry.getValue();
			Class<?> beanClass = ClassHelper.getTargetClass(bean);
			if (beanClass == null) {
				logger.warn("ServiceExport target class is null,beanName={}", entry.getKey());
				continue;
			}
			Class<?>[] interfaces = beanClass.getInterfaces();
			if (ArrayUtils.isEmpty(interfaces)) {
				logger.warn("Service bean must implement an interface,beanName={}", entry.getKey());
				continue;
			}
			Method[] interfaceMethods = interfaces[0].getMethods();
			if (ArrayUtils.isEmpty(interfaceMethods)) {
				logger.warn("Service interface method is empty,interface={}", interfaces[0]);
				continue;
			}
			Map<String, Method> interfaceMethodMap = new HashMap<String, Method>();
			for (Method interfaceMethod : interfaceMethods) {
				interfaceMethodMap.put(interfaceMethod.getName(), interfaceMethod);
			}
			Method[] classMethods = beanClass.getMethods();
			String interfaceName = interfaces[0].getName();
			for (Method cMethod : classMethods) {
				Method iMethod = interfaceMethodMap.get(cMethod.getName());
				if (iMethod == null) {
					continue;
				}
				ServiceExp serviceExp = new ServiceExp();
				String serviceCode = ClassHelper.getMethodFullName(iMethod);
				serviceExp.setServiceCode(serviceCode);
				serviceExp.setServiceGroup(interfaceName);
				ServiceBean serviceExport = beanClass.getAnnotation(ServiceBean.class);
				if (serviceExport != null) {
					serviceExp.setTimeout(serviceExport.timeout());
					serviceExp.setRegistry(serviceExport.registry());
					serviceExp.setProtocol(serviceExport.protocol());
				}
				ServiceMethod serviceMethod = cMethod.getAnnotation(ServiceMethod.class);
				if (serviceMethod != null) {
					serviceExp.setTimeout(serviceMethod.timeout());
					// serviceExp.setRegistry(serviceMethod.registry());
					// serviceExp.setProtocol(serviceMethod.protocol());
				}
				serviceExp.setTargetObject(bean);
				serviceExp.setTargetMethod(cMethod);
				this.serviceExps.put(serviceCode, serviceExp);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void registerExportService() throws Exception {
		Map<String, ServiceExporter> beans = applicationContext.getBeansOfType(ServiceExporter.class);
		if (CollectionUtils.isEmpty(beans)) {
			return;
		}
		for (Map.Entry<String, ServiceExporter> entry : beans.entrySet()) {
			ServiceExporter<?> exp = entry.getValue();
			Object bean = exp.getObject();
			Class<?> beanClass = ClassHelper.getTargetClass(bean);
			Class<?> interfaceClass = exp.getObjectType();
			if (interfaceClass == null) {
				Class<?>[] interfaces = beanClass.getInterfaces();
				if (ArrayUtils.isEmpty(interfaces)) {
					logger.warn("Service bean must implement an interface,beanName={}", entry.getKey());
					continue;
				}
				interfaceClass = interfaces[0];
			}

			Map<String, Method> interfaceMethodMap = new HashMap<String, Method>();
			Method[] interfaceMethods = interfaceClass.getMethods();
			if (ArrayUtils.isEmpty(interfaceMethods)) {
				logger.warn("Service interface method is empty,interface={}", interfaceClass);
				continue;
			}
			for (Method interfaceMethod : interfaceMethods) {
				interfaceMethodMap.put(interfaceMethod.getName(), interfaceMethod);
			}
			String interfaceName = interfaceClass.getName();
			Method[] classMethods = beanClass.getMethods();
			for (Method cMethod : classMethods) {
				Method iMethod = interfaceMethodMap.get(cMethod.getName());
				if (iMethod == null) {
					continue;
				}
				ServiceExp serviceExp = new ServiceExp();
				String serviceCode = ClassHelper.getMethodFullName(iMethod);
				serviceExp.setServiceCode(serviceCode);
				serviceExp.setServiceGroup(interfaceName);
				ServiceBean serviceExport = beanClass.getAnnotation(ServiceBean.class);
				if (serviceExport != null) {
					serviceExp.setTimeout(serviceExport.timeout());
					serviceExp.setRegistry(serviceExport.registry());
					serviceExp.setProtocol(serviceExport.protocol());
				}
				ServiceMethod serviceMethod = cMethod.getAnnotation(ServiceMethod.class);
				if (serviceMethod != null) {
					serviceExp.setTimeout(serviceMethod.timeout());
					// serviceExp.setRegistry(serviceMethod.registry());
					// serviceExp.setProtocol(serviceMethod.protocol());
				}
				{
					if (exp.getTimeout() != 0)
						serviceExp.setTimeout(exp.getTimeout());
					if (exp.getRegistry() != null)
						serviceExp.setRegistry(exp.getRegistry());
					if (exp.getProtocol() != null)
						serviceExp.setProtocol(exp.getProtocol());
				}
				MethodConfig exportMethod = exp.getMethod(cMethod.getName());
				if (exportMethod != null) {
					serviceExp.setTimeout(exportMethod.getTimeout());
					// serviceExp.setRegistry(exportMethod.getRegistry());
					// serviceExp.setProtocol(exportMethod.getProtocol());
				}
				serviceExp.setTargetObject(bean);
				serviceExp.setTargetMethod(cMethod);
				this.serviceExps.put(serviceCode, serviceExp);
			}
		}
	}

	private void registerImportService() throws Exception {
		Map<String, ServiceImporter> beans = applicationContext.getBeansOfType(ServiceImporter.class);
		if (CollectionUtils.isEmpty(beans)) {
			return;
		}
		for (ServiceImporter imp : beans.values()) {
			Class<?> interfaceClass = imp.getObjectType();
			Method[] interfaceMethods = interfaceClass.getMethods();
			if (ArrayUtils.isEmpty(interfaceMethods)) {
				logger.warn("Service interface method is empty,interface={}", interfaceClass);
				continue;
			}
			String interfaceName = interfaceClass.getName();
			for (Method iMethod : interfaceMethods) {
				ServiceImp serviceImp = new ServiceImp();
				String serviceCode = ClassHelper.getMethodFullName(iMethod);
				serviceImp.setServiceCode(serviceCode);
				serviceImp.setServiceGroup(interfaceName);
				{
					serviceImp.setTimeout(imp.getTimeout());
					serviceImp.setRegistry(imp.getRegistry());
					serviceImp.setProtocol(imp.getProtocol());
				}
				MethodConfig impMethod = imp.getMethod(iMethod.getName());
				if (impMethod != null) {
					serviceImp.setTimeout(impMethod.getTimeout());
					// serviceImp.setRegistry(impMethod.getRegistry());
					// serviceImp.setProtocol(impMethod.getProtocol());
				}
				this.serviceImps.put(serviceCode, serviceImp);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public ServiceExp getServiceExp(String serviceCode) {
		return serviceExps.get(serviceCode);
	}

	@Override
	public Collection<ServiceExp> getAllServiceExps() {
		return serviceExps.values();
	}

	@Override
	public ServiceImp getServiceImp(String serviceCode) {
		return serviceImps.get(serviceCode);
	}

	@Override
	public Collection<ServiceImp> getAllServiceImps() {
		return serviceImps.values();
	}
}
