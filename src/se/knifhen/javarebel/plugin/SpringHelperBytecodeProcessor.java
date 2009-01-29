package se.knifhen.javarebel.plugin;

import org.zeroturnaround.bundled.javassist.CannotCompileException;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.NotFoundException;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/**
 * This processor alters the way the <code>SpringHelper</code> looks up methods annotaded with @SpringBean in each <code>ActionBean</code>.
 * The original would first look in a cache for previous result of a @SpringBean scan.This however
 * prevented ActionBeans from adding SpringBeans runtime.
 *    
 * @author Andreas Knifh
 */
public class SpringHelperBytecodeProcessor extends JavassistClassBytecodeProcessor {

	public void process(ClassPool cp, ClassLoader cl, CtClass springHelper) throws Exception {
		getMethods(cp, springHelper);
		getFields(cp, springHelper);
	}

	/**
	 * Overrides the implementation of <code>SpringHelper.getFields(Class)</code> to make sure that 
	 * a class is always scanned for fields with @SpringBean
	 */
	private void getFields(ClassPool cp, CtClass springHelper) throws CannotCompileException, NotFoundException {
		CtMethod getFields = springHelper.getDeclaredMethod("getFields");
		getFields.setBody("" +
			"{" +
			"	return net.sourceforge.stripes.integration.spring.SpringHelperWrapper.getFields($1);" +
			"}");
	}

	/**
	 * Overrides the implementation of <code>SpringHelper.getMethods(Class)</code> to make sure that 
	 * a class is always scanned for methods with @SpringBean
	 */
	private void getMethods(ClassPool cp, CtClass springHelper) throws NotFoundException, CannotCompileException {
		CtMethod getMethods = springHelper.getDeclaredMethod("getMethods");
		getMethods.setBody("" +
			"{" +
			"	return net.sourceforge.stripes.integration.spring.SpringHelperWrapper.getMethods($1);" +
			"}");
	}
}
