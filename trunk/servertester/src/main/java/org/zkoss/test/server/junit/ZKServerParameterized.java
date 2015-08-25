/**
 * 
 */
package org.zkoss.test.server.junit;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 *
 * @author simonpai
 */
public class ZKServerParameterized extends Suite {
	
	protected ArrayList<Runner> _runners = new ArrayList<Runner>();
	
	@SuppressWarnings("unchecked")
	public ZKServerParameterized(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
		
		List<Object[]> parametersList = 
			(List<Object[]>) getParametersMethod().invokeExplosively(null);
		
		for (int i= 0; i < parametersList.size(); i++)
			getChildren().add(new ZKServerRunnerForParameters(
					getTestClass().getJavaClass(), parametersList, i));
	}
	
	@Override
	protected List<Runner> getChildren() {
		return _runners;
	}
	
	protected FrameworkMethod getParametersMethod() throws Exception {
		List<FrameworkMethod> methods = 
			getTestClass().getAnnotatedMethods(Parameters.class);
		
		for (FrameworkMethod each : methods) {
			int modifiers = each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}
		
		throw new Exception("No public static parameters method on class "
				+ getTestClass().getName());
	}
	
	
	
	protected class ZKServerRunnerForParameters extends ZKServerRunner {
		
		private final int _paramSetIndex;
		private final List<Object[]> _paramList;
		private String _paramSummary;
		
		public ZKServerRunnerForParameters(Class<?> klass,
				List<Object[]> parameterList, int i) throws InitializationError {
			
			super(klass);
			_paramList = parameterList;
			_paramSetIndex = i;
			
			try {
				Object[] params = getParams();
				if(params.length > 0) {
					String param0 = params[0].toString();
					_paramSummary = param0.length() > 20 ? 
							param0.substring(0, 17) + "..." : param0;
				}
			} catch (Exception e) {}
		}
		
		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(getParams());
		}
		
		protected Object[] getParams() throws Exception {
			try {
				return _paramList.get(_paramSetIndex);
			} catch (ClassCastException e) {
				throw new Exception(String.format(
						"%s.%s() must return a Collection of arrays.",
						getTestClass().getName(), getParametersMethod().getName()));
			}
		}
		
		@Override
		protected String getName() {
			if(_paramSummary == null)
				return String.format("[%s]", _paramSetIndex);
			return String.format("[%s] %s", _paramSetIndex, _paramSummary);
		}
		
		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s]", method.getName(), _paramSetIndex);
		}
		
		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}
		
		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
		
	}
	
}
