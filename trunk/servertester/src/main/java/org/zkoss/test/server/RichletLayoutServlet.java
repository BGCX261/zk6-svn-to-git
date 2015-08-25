/**
 * 
 */
package org.zkoss.test.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.util.Configuration;

/**
 *
 * @author simonpai
 */
public class RichletLayoutServlet extends DHtmlLayoutServlet {
	
	private static final long serialVersionUID = 768805275769914220L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		Configuration zkconfig = WebManager.getWebManager(
				config.getServletContext()).getWebApp().getConfiguration();
		
		zkconfig.addRichlet("ServerTest", ServerTestRichlet.class, null);
		zkconfig.addRichletMapping("ServerTest", "/*");
	}
	
}
