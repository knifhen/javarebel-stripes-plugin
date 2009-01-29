package net.sourceforge.stripes.integration.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.util.ReflectUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringHelperWrapper {
	public static Resolution intercept(ExecutionContext context) throws Exception {
		Resolution resolution = context.proceed();
		System.out.println("OVERRIDE: Running Spring dependency injection for instance of " + context.getActionBean().getClass().getSimpleName());
		injectBeans(context.getActionBean(), context.getActionBeanContext());
		return resolution;
	}

	public static void injectBeans(Object bean, ActionBeanContext context) {
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(StripesFilter.getConfiguration().getServletContext());
		// First inject any values using annotated methods
		for (Method m : getMethods(bean.getClass())) {
			try {
				System.out.println("method " + m.getName() + " " + m.getAnnotation(SpringBean.class));
				SpringBean springBean = m.getAnnotation(SpringBean.class);
				boolean nameSupplied = !"".equals(springBean.value());
				String name = nameSupplied ? springBean.value() : SpringHelper.methodToPropertyName(m);
				Class<?> beanType = m.getParameterTypes()[0];
				Object managedBean = SpringHelper.findSpringBean(ctx, name, beanType, !nameSupplied);
				m.invoke(bean, managedBean);
			} catch (Exception e) {
				throw new StripesRuntimeException("Exception while trying to lookup and inject " + "a Spring bean into a bean of type " + bean.getClass().getSimpleName() + " using method "
						+ m.toString(), e);
			}
		}

		// And then inject any properties that are annotated
		for (Field f : getFields(bean.getClass())) {
			try {
				SpringBean springBean = f.getAnnotation(SpringBean.class);
				boolean nameSupplied = !"".equals(springBean.value());
				String name = nameSupplied ? springBean.value() : f.getName();
				Object managedBean = SpringHelper.findSpringBean(ctx, name, f.getType(), !nameSupplied);
				f.set(bean, managedBean);
			} catch (Exception e) {
				throw new StripesRuntimeException("Exception while trying to lookup and inject " + "a Spring bean into a bean of type " + bean.getClass().getSimpleName()
						+ " using field access on field " + f.toString(), e);
			}
		}
	}

	protected static Collection<Method> getMethods(Class<?> clazz) {
		Collection<Method> methods = ReflectUtil.getMethods(clazz);
		Iterator<Method> iterator = methods.iterator();

		while (iterator.hasNext()) {
			Method method = iterator.next();
			if (!method.isAnnotationPresent(SpringBean.class)) {
				iterator.remove();
			} else {
				// If the method isn't public, try to make it accessible
				if (!method.isAccessible()) {
					try {
						method.setAccessible(true);
					} catch (SecurityException se) {
						throw new StripesRuntimeException("Method " + clazz.getName() + "." + method.getName() + "is marked " + "with @SpringBean and is not public. An attempt to call "
								+ "setAccessible(true) resulted in a SecurityException. Please " + "either make the method public or modify your JVM security "
								+ "policy to allow Stripes to setAccessible(true).", se);
					}
				}

				// Ensure the method has only the one parameter
				if (method.getParameterTypes().length != 1) {
					throw new StripesRuntimeException("A method marked with @SpringBean must have exactly one parameter: " + "the bean to be injected. Method [" + method.toGenericString() + "] has "
							+ method.getParameterTypes().length + " parameters.");
				}
			}
		}

		return methods;
	}

	protected static Collection<Field> getFields(Class<?> clazz) {
		Collection<Field> fields = ReflectUtil.getFields(clazz);
		Iterator<Field> iterator = fields.iterator();

		while (iterator.hasNext()) {
			Field field = iterator.next();
			if (!field.isAnnotationPresent(SpringBean.class)) {
				iterator.remove();
			} else if (!field.isAccessible()) {
				// If the field isn't public, try to make it accessible
				try {
					field.setAccessible(true);
				} catch (SecurityException se) {
					throw new StripesRuntimeException("Field " + clazz.getName() + "." + field.getName() + "is marked " + "with @SpringBean and is not public. An attempt to call "
							+ "setAccessible(true) resulted in a SecurityException. Please " + "either make the field public, annotate a public setter instead "
							+ "or modify your JVM security policy to allow Stripes to " + "setAccessible(true).", se);
				}
			}
		}

		return fields;
	}
}
