/**
 * 
 */
package org.zkoss.test.server.junit;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 *
 * @author simonpai
 */
public class JettyTesterInitStatement extends Statement {
	
	private final Statement _next;
	private final List<FrameworkMethod> _initiators;
	private final Object _target;
	private final MethodInvocationContext _context;
	
	public JettyTesterInitStatement(Statement next, 
			List<FrameworkMethod> initiators, Object target,
			MethodInvocationContext context) {
		_next = next;
		_initiators = initiators;
		_target = target;
		_context = context;
	}
	
	@Override
	public void evaluate() throws Throwable {
		for(FrameworkMethod fm : _initiators)
			fm.invokeExplosively(_target, _context.getServletTester());
		_next.evaluate();
	}
	
}
