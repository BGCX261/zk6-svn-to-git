/**
 * 
 */
package org.zkoss.test.server;

import org.zkoss.test.server.junit.TestMethodFunctor;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;

/**
 *
 * @author simonpai
 */
public class ServerTestRichlet extends GenericRichlet {
	
	public void service(Page page) throws Exception {
		((TestMethodFunctor) Executions.getCurrent()
			.getAttribute("TestMethodFunctor")).invoke(page);
	}
	
}
