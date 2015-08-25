/**
 * 
 */
package org.zkoss.test.server.junit;

import org.eclipse.jetty.testing.HttpTester;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.zkoss.zk.ui.Page;

/**
 *
 * @author simonpai
 */
public class DHtmlLayoutStatement extends Statement {
	
	private final MethodInvocationContext _context;
	private final FrameworkMethod _testMethod;
	private final Object _target;
	
	public DHtmlLayoutStatement(FrameworkMethod method, Object target, 
			MethodInvocationContext context){
		_context = context;
		_testMethod = method;
		_target = target;
	}
	
	@Override
	public void evaluate() throws Throwable {
		
		final Ref<Throwable> errorRef = new Ref<Throwable>();
		_context.setTestMethodFunctor(new TestMethodFunctor(){
			public void invoke(Page page) {
				try {
					_testMethod.invokeExplosively(_target, page);
				} catch (Throwable e) {
					errorRef.set(e);
				}
			}
		});
		
		// send request
		HttpTester request = createRequest();
		HttpTester response = new HttpTester();
		response.parse(_context.getServletTester()
				.getResponses(request.generate()));
		
		if(response.getStatus() != 200) throw new Exception(
				"Invalid server response: " + response.getStatus());
		if(errorRef.get() != null) throw errorRef.get();
	}
	
	// helper //
	private HttpTester createRequest(){
	    HttpTester request = new HttpTester();
	    request.setMethod("GET");
	    request.setHeader("Host","127.0.0.1");
	    request.setURI("http://localhost:8080/test/"); // TODO: port
	    request.setVersion("HTTP/1.0");
	    return request;
	}
	
	private class Ref<T> {
		private T _obj;
		public T get(){ return _obj; }
		public void set(T obj){ _obj = obj; }
	}
	
}
