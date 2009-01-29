package net.sourceforge.stripes.integration.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.util.ReflectUtil;

/**
 * Replaces some methods in net.sourceforge.stripes.integration.spring.SpringHelper
 */
public class SpringHelperWrapper {

	/**
	 * Removed methods cache from original code.
	 */
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

	/**
	 * Removed fields cache from original code.
	 */
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
