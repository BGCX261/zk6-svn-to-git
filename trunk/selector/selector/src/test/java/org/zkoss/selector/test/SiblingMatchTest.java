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
 * @author TonyQ
 */
@RunWith(ZKServerParameterized.class)
public class SiblingMatchTest {
	
	private final String _selector;
	private final int _expectedSize;
	
	public SiblingMatchTest(String selector, int size){
		_selector = selector;		
		_expectedSize = size;
	}
	
	@TesterInit
	public void init(ServletTester tester) throws Exception {
		ServletTesterInits.setResourceBase(
				tester, "src/test/resources/web/junit");
	}
	
	@Test
	public void match(Page page){
		
		Component comp = Executions.createComponents(
				"SiblingMatchTest.zul", null, null);
		
		List<Component> list = Selectors.find(comp, _selector);
		System.out.println(_selector);
		for(Component c : list) System.out.println(c);
		Assert.assertNotNull(list);
		Assert.assertEquals(_expectedSize, list.size());
		
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				{"div", 2},
				{"vbox", 1},
				{"hbox", 3},
				{"div, hbox", 5},
				{"#myroot .container textbox", 1},
				{"#myroot *", 8},
				{"#myroot textbox", 1},
				{"#myroot label[value=hello]", 1},
				{"#myroot textbox[id=txt]", 1},
				{"#myroot .container textbox[id=txt]", 1},
				{".container hbox", 3},
				{".container hbox[vflex=1]", 1},
				{".container hbox[hflex=1]", 2},
				{".container hbox[hflex=1][vflex=1]", 1},
				{".container hbox[style=\"width:500px\"]", 2},
		});
	}
	
}
