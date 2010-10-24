package se.knifhen.javarebel.plugin;

import net.sourceforge.stripes.controller.UrlBindingFactoryProvider;

import org.zeroturnaround.bundled.javassist.CannotCompileException;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.CtNewMethod;
import org.zeroturnaround.bundled.javassist.NotFoundException;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/**
 * This processor alters the way the <code>NameBasedActionResolver</code> loads new and existing ActionBeans.
 * The original would permanently bind an <code>ActionBean</code> to a specific urlBinding. And any changes regarding EventHandlers
 * would go unnoticed. This is fixed in the overriding methods.
 * 
 * @author Andreas Knifh
 */
public class NameBasedActionResolverBytecodeProcessor extends JavassistClassBytecodeProcessor {

	/**
	 * The class that all changes are made to.
	 */
	private CtClass nameBasedActionResolver;

	/**
	 * Applies changes to <code>NameBasedActionResolver</code> to allow Stripes to reload ActionBeans dynamically. 
	 */
	public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
		this.nameBasedActionResolver = ctClass;
		fields(cp);
		init();
		makeNewActionBean();
		getActionBean();
		getActionBeanType();
		implementUrlBindingFactoryProvider(cp, ctClass);
	}

	/**
	 * Adds the <code>NameBasedActionResolverHelper</code> to the <code>NameBasedActionResolver</code> 
	 */
	private void fields(ClassPool cp) throws NotFoundException, CannotCompileException {
		CtClass helperClass = cp.get("net.sourceforge.stripes.controller.NameBasedActionResolverHelper");
		CtField helper = new CtField(helperClass, "helper", nameBasedActionResolver);
		nameBasedActionResolver.addField(helper);
	}
	
	/**
	 * Append initialization of the <code>NameBasedActionResolverHelper</code>
	 */
	private void init() throws NotFoundException, CannotCompileException {
		CtMethod init = nameBasedActionResolver.getDeclaredMethod("init");
		init.insertAfter(
			"helper = new net.sourceforge.stripes.controller.NameBasedActionResolverHelper($0, log);"
		);
	}

	/**
	 * Overrides the <code>AnnotatedClassActionResolver.makeNewActionBean()</code>
	 */
	private void makeNewActionBean() throws NotFoundException, CannotCompileException {
		CtMethod makeNewActionBean = nameBasedActionResolver.getSuperclass().getDeclaredMethod("makeNewActionBean");
		makeNewActionBean = new CtMethod(makeNewActionBean, nameBasedActionResolver, null);
		makeNewActionBean.setBody(
			"{" +
			"	return helper.makeNewActionBean($1, $2);" +
			"}"
		);
		
		nameBasedActionResolver.addMethod(makeNewActionBean);
	}

	/**
	 * Overrides the <code>NameBasedActionResolver.getActionBean()</code>
	 */
	private void getActionBean() throws NotFoundException, CannotCompileException {
		CtMethod getActionBean = nameBasedActionResolver.getDeclaredMethod("getActionBean");
		getActionBean.setBody(
			"{" + 
			"	return helper.getActionBean($1, $2);" +
			"}"
		);
	}

	/**
	 * Overrides the <code>AnnotatedClassActionResolver.getActionBeanType()</code> 
	 */
	private void getActionBeanType() throws NotFoundException, CannotCompileException {
		CtMethod getActionBeanType = nameBasedActionResolver.getSuperclass().getDeclaredMethod("getActionBeanType");
		getActionBeanType = new CtMethod(getActionBeanType, nameBasedActionResolver, null);
		getActionBeanType.setBody(
			"{" +
			"	return helper.getActionBeanType($1);" + 
			"}"
		);
		nameBasedActionResolver.addMethod(getActionBeanType);
	}

	/**
	 * Add method to get access to UrlBindingFactory used by this action resolver
	 */
	private void implementUrlBindingFactoryProvider(ClassPool cp, CtClass ctClass) throws CannotCompileException, NotFoundException {
	  cp.importPackage("net.sourceforge.stripes.controller");

	  try {
	    // exists since stripes 1.5.4
	    ctClass.getMethod("getUrlBindingFactory", "()Lnet/sourceforge/stripes/controller/UrlBindingFactory;");
	  }
	  catch (NotFoundException e) {
	    ctClass.addMethod(
	      CtNewMethod.make(
	          "public UrlBindingFactory getUrlBindingFactory() {" +
	          "  return UrlBindingFactory.getInstance();" +
	          "}", ctClass)
	    );
	  }

	  ctClass.addInterface(cp.get(UrlBindingFactoryProvider.class.getName()));
  }
	
}
