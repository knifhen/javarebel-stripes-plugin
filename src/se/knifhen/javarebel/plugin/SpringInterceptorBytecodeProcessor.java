package se.knifhen.javarebel.plugin;

import org.zeroturnaround.bundled.javassist.CannotCompileException;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.NotFoundException;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class SpringInterceptorBytecodeProcessor extends JavassistClassBytecodeProcessor {

	/**
	 * Applies changes to <code>UrlBindingFactory</code> to make Stripes always extract the correct urlBinding from each <code>ActionBean</code>. 
	 */
	public void process(ClassPool cp, ClassLoader cl, CtClass springHelper) throws Exception {
		System.out.println("Found SpringInterceptor");
		intercept(cp, springHelper);
	}

	/**
	 * Overrides the implementation of <code>UrlBindingFactory.getBindingPrototype(Class)</code> to make sure that only the 
	 * correct urlBinding is used for each <code>ActionBean</code>. 
	 */
	private void intercept(ClassPool cp, CtClass springHelper) throws NotFoundException, CannotCompileException {
		
		CtMethod intercept = springHelper.getDeclaredMethod("intercept");
		intercept.setBody("" +
			"{" +
			"	return net.sourceforge.stripes.integration.spring.SpringHelperWrapper.intercept($1);" +
			"}");
	}
}
