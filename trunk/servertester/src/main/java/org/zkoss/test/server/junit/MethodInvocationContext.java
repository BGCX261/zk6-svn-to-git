/**
 * 
 */
package org.zkoss.test.server.junit;

import org.eclipse.jetty.testing.ServletTester;

/**
 *
 * @author simonpai
 */
public class MethodInvocationContext {
	
	private TestMethodFunctor _testMethodFunctor;
	private final ServletTester _servletTester;
	
	public MethodInvocationContext(ServletTester servletTester) {
		_servletTester = servletTester;
	}
	
	public ServletTester getServletTester() {
		return _servletTester;
	}
	
	public TestMethodFunctor getTestMethodFunctor() {
		return _testMethodFunctor;
	}
	
	public void setTestMethodFunctor(TestMethodFunctor testMethodFunctor) {
		_testMethodFunctor = testMethodFunctor;
	}

}
