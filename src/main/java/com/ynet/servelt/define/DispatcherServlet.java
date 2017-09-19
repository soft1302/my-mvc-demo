package com.ynet.servelt.define;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ynet.annotation.Autowire;
import com.ynet.annotation.Controller;
import com.ynet.annotation.RequestMapping;
import com.ynet.annotation.Service;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String scanPackage = "";
	private List<String> packageNames = new ArrayList<String>();
	private List<BeanFactory> beanFactories = new ArrayList<>();
	private List<HandlerMapping> handlerMappings = new ArrayList<>();

	/**
	 * 负责转发地址请求，并执行
	 */
	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		Enumeration em = arg0.getParameterNames();
		Map<String, Object> contextMap = new HashMap<>();
		while (em.hasMoreElements()) {
			String key = (String) em.nextElement();
			String value = arg0.getParameter(key);
			contextMap.put(key, value);
		}
		String url = arg0.getRequestURI();
		String context = arg0.getContextPath();
		String path = url.replace(context, "");
		url = path.substring(1);
		Object clazz = null;
		Method method = null;
		// 映射容器中获取映射方法
		for (HandlerMapping bean : handlerMappings) {
			if (url.equals(bean.getKey())) {
				method = (Method) bean.getBean();
				break;
			}
		}
		// 获取bean
		for (BeanFactory bean : beanFactories) {
			if (url.split("/")[0].equals(bean.getKey())) {
				clazz = bean.getBean();
			}
		}

		// 通过反射机制执行方法
		try {
			method.invoke(clazz, new Object[] { arg0, arg1, contextMap });
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化参数，bean容器生成，实现ioc注入
	 * 
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		// 获取所有类
		scanClass(scanPackage);
		/*******************利用反射机制实例化类开始**************************/
		try {
			filterAndInstanceClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 打印实例化类
		for (BeanFactory bean : beanFactories) {
			System.out.println(bean.toString());
		}
		/*******************利用反射机制实例化类结束**************************/
		
		/*******************利用反射机制实例化映射地址开始**************************/
		mapping();
		
		for (HandlerMapping mapping : handlerMappings) {
			System.out.println(mapping.toString());
		}
		/*******************利用反射机制实例化映射地址结束**************************/
		
		/*******************IOC容器注入开始**************************/
		iocInject();
		/*******************IOC容器注入结束**************************/
	}

	/**
	 * IOC注入
	 */
	private void iocInject() {
		if (beanFactories.isEmpty()) {
			return;
		}
		for (BeanFactory bean : beanFactories) {
			// 拿到里面的所有属性
			Field fields[] = bean.getBean().getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);// 可访问私有属性
				// 按照类型自动注入
				if (field.isAnnotationPresent(Autowire.class)) {
					// 类型查找
					for (BeanFactory types : beanFactories) {
						Class<?> c = types.getBean().getClass();
						Class<?>[] interfaceDefines = c.getInterfaces();
						for (Class<?> type : interfaceDefines) {
							// 类型相似匹配
							if (type.getName().equals(
									field.getGenericType().getTypeName())) {
								field.setAccessible(true);
								try {
									field.set(bean.getBean(), types.getBean());
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							}
						}

					}
				}
			}
		}
	}

	private void mapping() {
		if (beanFactories.isEmpty()) {
			return;
		}
		for (BeanFactory bean : beanFactories) {
			if (bean.getBean().getClass().isAnnotationPresent(Controller.class)) {
				Method[] methods = bean.getBean().getClass().getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(RequestMapping.class)) {
						RequestMapping rm = (RequestMapping) method
								.getAnnotation(RequestMapping.class);
						String urlMapping = rm.value();
						handlerMappings.add(new HandlerMapping(bean.getKey()
								+ "/" + urlMapping, method));
					}
				}
			}
		}
	}

	private void filterAndInstanceClass() throws Exception {
		if (packageNames.isEmpty()) {
			return;
		}
		for (String className : packageNames) {
			Class<?> cObject = Class.forName(className.replace(".class", "")
					.trim());
			if (cObject.isAnnotationPresent(Controller.class)) {
				Object object = cObject.newInstance();
				Controller controller = cObject.getAnnotation(Controller.class);
				String key = controller.value();
				beanFactories.add(new BeanFactory(key, object));
			}

			if (cObject.isAnnotationPresent(Service.class)) {
				Object object = cObject.newInstance();
				Service service = cObject.getAnnotation(Service.class);
				String key = service.value();
				beanFactories.add(new BeanFactory(key, object));
			}
		}
	}

	private void scanClass(String scanPackage) {

		URL url = this.getClass().getClassLoader().getResource(scanPackage);
		String filePath = url.getFile();
		File file = new File(filePath);
		String[] fileList = file.list();
		for (String strFile : fileList) {
			File child = new File(filePath + "/" + strFile);
			if (child.isDirectory()) {
				scanClass(scanPackage + "/" + child.getName());
			} else if (child.isFile()) {
				packageNames.add(validate(scanPackage) + "." + child.getName());
			}
		}
	}

	private String validate(String scanPackage) {
		if (scanPackage.indexOf("/") == 0) {
			scanPackage = scanPackage.substring(1);
		}
		return scanPackage.replaceAll("/", ".");
	}

}
