package se.knifhen.javarebel.plugin;


import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.IntegrationFactory;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.Plugin;

/**
 * Javarebel plugin that makes any changes to Stripes ActionBeans load without restarting the application server. 
 * @author Andreas Knifh
 */
public class StripesPlugin implements Plugin {
	
	/**
	 * Adds the IntegrationProcessors.
	 */
	public void preinit() {
//		LoggerFactory.getInstance().echo("---------------------------------------------------------------------------------------");
//		LoggerFactory.getInstance().echo("JavaRebel: The Stripes Plugin is now enabled.");
//		LoggerFactory.getInstance().echo("---------------------------------------------------------------------------------------");

		IntegrationFactory.getInstance().addIntegrationProcessor("net.sourceforge.stripes.controller.NameBasedActionResolver", new NameBasedActionResolverBytecodeProcessor());
		IntegrationFactory.getInstance().addIntegrationProcessor("net.sourceforge.stripes.controller.UrlBindingFactory", new UrlBindingFactoryBytecodeProcessor());
		IntegrationFactory.getInstance().addIntegrationProcessor("net.sourceforge.stripes.integration.spring.SpringHelper", new SpringHelperBytecodeProcessor());
	}

	/**
	 * This method is not used by the Stripes plugin
	 */
	public boolean checkDependencies(ClassLoader cl, ClassResourceSource crs) {
		LoggerFactory.getInstance().echo("StripesPlugin.checkDependencies");
		return false;
	}

	@Override
	public String getDescription() {
		return "Adds reloading of Stripes ActionBeans.";
	}

	@Override
	public String getId() {
		return "stripes_plugin";
	}

	@Override
	public String getName() {
		return "Stripes plugin 1.0.4";
	}

}
