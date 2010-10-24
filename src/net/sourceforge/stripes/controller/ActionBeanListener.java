package net.sourceforge.stripes.controller;

import net.sourceforge.stripes.action.ActionBean;

import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.Logger;
import org.zeroturnaround.javarebel.LoggerFactory;

/**
 * An example ClassEventListener
 */
public class ActionBeanListener implements ClassEventListener {
    Logger logger = LoggerFactory.getInstance();

    /**
     * Adds any class that is an <code>ActionBean</code> to the <code>AnnotatedClassActionResolver</code>.
     */
    @SuppressWarnings("unchecked")
    public void onClassEvent(final int eventType, final Class klass) {

        if (ClassEventListener.EVENT_LOADED == eventType) {
            /* nothing to do */
            return;
        }

        logger.echo("Action bean listener reloading "
                + klass.getCanonicalName());

        try {
            boolean isAssignable = ActionBean.class.isAssignableFrom(klass);

            if (isAssignable) {
                ActionResolver actionResolver = StripesFilter
                        .getConfiguration().getActionResolver();
                logger.echo(klass.getSimpleName() + " is an Action Bean.");
                AnnotatedClassActionResolver resolver = (AnnotatedClassActionResolver) actionResolver;
                logger.echo("Action resolver is " + actionResolver);

                logger.echo("Adding action bean");
                resolver.addActionBean(klass);
            }

        } catch (Throwable e) {
            logger.echo("Error reloading action bean: " + e.getMessage());
        }

    }

	public int priority() {
		// TODO Auto-generated method stub
		return 0;
	}
}