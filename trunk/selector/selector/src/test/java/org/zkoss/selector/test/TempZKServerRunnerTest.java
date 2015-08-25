/**
 * 
 */
package org.zkoss.selector.test;

import org.eclipse.jetty.testing.ServletTester;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.zkoss.selector.test.util.Logs;
import org.zkoss.test.server.junit.TesterInit;
import org.zkoss.test.server.junit.ZKServerRunner;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;

/**
 *
 * @author simonpai
 */
@Ignore
@RunWith(ZKServerRunner.class)
public class TempZKServerRunnerTest {
	
	@TesterInit
	public void init(ServletTester tester) throws Exception {
		tester.getContext().setBaseResource(
				Resource.newResource("src/test/resources/web/junit"));
	}
	
	@Test
	public void method(Page page){
		Executions.createComponents("SiblingMatchTest.zul", null, null);
		Logs.log("roots", page.getRoots().toArray());
	}
	
}
