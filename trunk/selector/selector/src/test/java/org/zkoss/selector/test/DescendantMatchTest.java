/**
 * 
 */
package org.zkoss.selector.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.jetty.testing.ServletTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.zkoss.selector.Selectors;
import org.zkoss.selector.test.util.ServletTesterInits;
import org.zkoss.test.server.junit.TesterInit;
import org.zkoss.test.server.junit.ZKServerParameterized;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;

/**
 *  
 * @author simonpai
 */
@RunWith(ZKServerParameterized.class)
public class DescendantMatchTest {
	
	private final String _selector;
	private final int _expectedCount;
	
	public DescendantMatchTest(String selector, int expectedCount){
		_selector = selector;
		_expectedCount = expectedCount;
	}
	
	@TesterInit
	public void init(ServletTester tester) throws Exception {
		ServletTesterInits.setResourceBase(
				tester, "src/test/resources/web/junit");
	}
	
	@Test
	public void match(Page page){
		
		Component comp = Executions.createComponents(
				"DescendantMatchTest.zul", null, null);
		
		List<Component> list = Selectors.find(comp, _selector);
		System.out.println(_selector);
		for(Component c : list) System.out.println(c);
		Assert.assertNotNull(list);
		Assert.assertEquals(_expectedCount, list.size());
		
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				{"div", 3},
				{"hlayout > hlayout", 1},
				{"hlayout hlayout", 2},
				{"hlayout hlayout hlayout", 1},
				{"grid > rows > row", 1},
				{"grid > row", 0},
				{"grid row", 1},
				
				{":first-child", 14},
				{"*", 14}
		});
	}
	
}
