package se.knifhen.javarebel.plugin;

import org.zeroturnaround.bundled.javassist.CannotCompileException;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.NotFoundException;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class SpringHelperBytecodeProcessor extends JavassistClassBytecodeProcessor {

	public void process(ClassPool cp, ClassLoader cl, CtClass springHelper) throws Exception {
		getMethods(cp, springHelper);
		getFields(cp, springHelper);
	}

	private void getFields(ClassPool cp, CtClass springHelper) throws CannotCompileException, NotFoundException {
		CtMethod getFields = springHelper.getDeclaredMethod("getFields");
		getFields.setBody("" +
				"{" +
				"	return net.sourceforge.stripes.integration.spring.SpringHelperWrapper.getFields($1);" +
				"}");
	}

	private void getMethods(ClassPool cp, CtClass springHelper) throws NotFoundException, CannotCompileException {
		CtMethod getMethods = springHelper.getDeclaredMethod("getMethods");
		getMethods.setBody("" +
				"{" +
				"	return net.sourceforge.stripes.integration.spring.SpringHelperWrapper.getMethods($1);" +
				"}");
	}
}
