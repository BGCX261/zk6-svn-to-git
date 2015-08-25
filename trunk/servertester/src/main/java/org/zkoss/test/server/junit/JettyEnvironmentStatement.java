/**
 * 
 */
package org.zkoss.test.server.junit;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.testing.ServletTester;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StdErrLog;
import org.junit.runners.model.Statement;
import org.zkoss.test.server.RichletLayoutServlet;
import org.zkoss.zk.ui.http.HttpSessionListener;

/**
 *
 * @author simonpai
 */
public class JettyEnvironmentStatement extends Statement {
	
	private final Statement _next;
	private final MethodInvocationContext _context;
	
	public JettyEnvironmentStatement(Statement next, 
			MethodInvocationContext context){
		_next = next;
		_context = context;
	}
	
	@Override
	public void evaluate() throws Throwable {
		ServletTester tester = _context.getServletTester();
		initServletTester(tester);
		tester.start();
		_next.evaluate();
		tester.stop();
	}
	
	private void initServletTester(ServletTester tester){
		
		// TODO: nullify logging
		
		// ZK web.xml configuration
		ServletHolder holder = new ServletHolder(RichletLayoutServlet.class){
			public void handle(Request baseRequest, ServletRequest request, 
					ServletResponse response) throws ServletException, 
					UnavailableException, IOException {
				
				// create a context for passing the test methods into Jetty
				request.setAttribute("TestMethodFunctor", 
						_context.getTestMethodFunctor());
				super.handle(baseRequest, request, response);
            }
		};
		holder.setInitParameter("update-uri", "/zkau");
		holder.setInitParameter("log-level", "OFF"); // No ZK log
		
		// for richlet
		tester.getContext().addServlet(holder, "/test/*");
		
		tester.addEventListener(new HttpSessionListener());
		tester.setContextPath("/");
		
		// TODO: port
		
		// nullify logging
		tester.getContext().setLogger(new Logger(){
			public void debug(String msg, Throwable th) {}
			public void debug(String msg, Object arg0, Object arg1) {}
			public Logger getLogger(String name) { return this; }
			public void info(String msg, Object arg0, Object arg1) {}
			public boolean isDebugEnabled() { return false; }
			public void setDebugEnabled(boolean enabled) {}
			public void warn(String msg, Throwable th) {}
			public void warn(String msg, Object arg0, Object arg1) {}
		});
	}
	
}
