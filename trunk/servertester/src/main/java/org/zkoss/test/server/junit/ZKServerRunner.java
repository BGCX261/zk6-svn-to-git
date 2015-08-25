/**
 * 
 */
package org.zkoss.test.server.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jetty.testing.ServletTester;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.zkoss.zk.ui.Page;

/**
 *
 * @author simonpai
 */
public class ZKServerRunner extends BlockJUnit4ClassRunner {
	
	protected MethodInvocationContext _context = 
		new MethodInvocationContext(new ServletTester());
	
	public ZKServerRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}
	
	
	
	// statement //
	@Override
	protected Statement methodBlock(FrameworkMethod method) {
		return new JettyEnvironmentStatement(
				super.methodBlock(method), _context);
	}
	
	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new DHtmlLayoutStatement(method, test, _context);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected Statement withBefores(FrameworkMethod method, Object target,
			Statement statement) {
		return new JettyTesterInitStatement(
				super.withBefores(method, target, statement), 
				getTestClass().getAnnotatedMethods(TesterInit.class), 
				target, _context);
	}
	
	
	
	// validation //
	@SuppressWarnings("deprecation")
	@Override
	protected void validateInstanceMethods(List<Throwable> errors) {
		validateMethodSignature(
				TesterInit.class, true, false, errors, ServletTester.class);
		super.validateInstanceMethods(errors);
	}
	
	@Override
	protected void validateTestMethods(List<Throwable> errors) {
		validateMethodSignature(Test.class, true, false, errors, Page.class);
	}
	
	protected void validateMethodSignature(
			Class<? extends Annotation> annotation, boolean isPublicVoid, 
			boolean isStatic, List<Throwable> errors, Class<?> ... signature) {
		
		List<FrameworkMethod> methods = 
			getTestClass().getAnnotatedMethods(annotation);
		
		for (FrameworkMethod fm : methods) {
			if(isPublicVoid) fm.validatePublicVoid(isStatic, errors);
			Method m = fm.getMethod();
			Class<?>[] msigs = m.getParameterTypes();
			
			if (msigs.length != signature.length)
				errors.add(new Exception("Method " + m.getName() + 
						" should have " + signature.length + " parameters"));
			
			else for(int i=0; i < signature.length; i++)
				if(!msigs[i].isAssignableFrom(signature[i]))
					errors.add(new Exception("Signature of method " + 
							m.getName() + " requires " + signature[i] + 
							" at position " + i + " but found " + msigs[i]));
		}
	}
	
}
