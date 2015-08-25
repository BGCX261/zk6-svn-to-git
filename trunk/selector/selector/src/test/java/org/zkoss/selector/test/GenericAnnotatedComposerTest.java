/**
 * 
 */
package org.zkoss.selector.test;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jetty.testing.ServletTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.zkoss.selector.test.util.ServletTesterInits;
import org.zkoss.test.server.junit.TesterInit;
import org.zkoss.test.server.junit.ZKServerParameterized;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;

/**
 *
 * @author simonpai
 */
@RunWith(ZKServerParameterized.class)
public class GenericAnnotatedComposerTest {
	
	private final String _zulname;
	
	public GenericAnnotatedComposerTest(String name){
		_zulname = name;
	}
	
	@TesterInit
	public void init(ServletTester tester) throws Exception {
		ServletTesterInits.setResourceBase(
				tester, "src/test/resources/web/junit/composer");
	}
	
	@Test
	public void composer(Page page){
		Executions.createComponents(_zulname+".zul", null, null);
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				{"groupbox"},
				{"deepspace"},
				{"implicit"},
				
				{"index"}
		});
	}
	
}
