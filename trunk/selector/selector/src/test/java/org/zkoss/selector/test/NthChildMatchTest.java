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
import org.zkoss.selector.test.util.Logs;
import org.zkoss.selector.test.util.ServletTesterInits;
import org.zkoss.test.server.junit.TesterInit;
import org.zkoss.test.server.junit.ZKServerParameterized;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Window;

/**
 *
 * @author simonpai
 */
@RunWith(ZKServerParameterized.class)
public class NthChildMatchTest {
	
	private final String _selector;
	private final String _expected;
	
	public NthChildMatchTest(String selector, String expected){
		_selector = selector;
		_expected = expected;
	}
	
	@TesterInit
	public void init(ServletTester tester) throws Exception {
		ServletTesterInits.setResourceBase(
				tester, "src/test/resources/web/junit");
	}
	
	@Test
	public void matchPage(Page page){
		Executions.createComponents("NthChildMatchTest.zul", null, null);
		assertList(Selectors.find(page, _selector));
	}
	
	@Test
	public void matchComp(Page page){
		Window win = new Window();
		win.setPage(page);
		Executions.createComponents("NthChildMatchTest.zul", win, null);
		assertList(Selectors.find(win, _selector), win);
	}
	
	private void assertList(List<Component> list, Component root){
		list.remove(root);
		assertList(list);
	}
	
	private void assertList(List<Component> list){
		Logs.log(_selector, list.toArray());
		Assert.assertNotNull(list);
		String[] exps = _expected.split(",");
		int size = exps.length;
		if(_expected.isEmpty()) {
			Assert.assertTrue(list.isEmpty());
			return;
		}
		Assert.assertEquals(size, list.size());
		for(int i=0; i<size; i++)
			Assert.assertEquals(exps[i].trim(), list.get(i).getId());
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				
				// use case
				{":nth-child(1)", "z1"},
				{":nth-child(2)", "z2"},
				{":nth-child(+3)", "z3"},
				{":nth-child(-3)", ""},
				{":nth-child(odd)", "z1,z3,z5,z7,z9,z11,z13,z15"},
				{":nth-child(even)", "z2,z4,z6,z8,z10,z12,z14"},
				{":nth-child(2n+1)", "z1,z3,z5,z7,z9,z11,z13,z15"},
				{":nth-child(2n)", "z2,z4,z6,z8,z10,z12,z14"},
				{":nth-child(3n)", "z3,z6,z9,z12,z15"},
				{":nth-child(3n+2)", "z2,z5,z8,z11,z14"},
				{":nth-child(+3n+2)", "z2,z5,z8,z11,z14"},
				{":nth-child(3n-1)", "z2,z5,z8,z11,z14"},
				{":nth-child(-n+5)", "z1,z2,z3,z4,z5"},
				{":nth-child(-2n+5)", "z1,z3,z5"},
				{":nth-child(-2n-5)", ""},
				{":nth-child(n+11)", "z11,z12,z13,z14,z15"},
				{":nth-child(+n+11)", "z11,z12,z13,z14,z15"},
				
				{":nth-last-child(1)", "z15"},
				{":nth-last-child(2)", "z14"},
				{":nth-last-child(+3)", "z13"},
				{":nth-last-child(odd)", "z1,z3,z5,z7,z9,z11,z13,z15"},
				
				// illegal syntax
				
				{"#x", ""}
				
		});
	}
	
}
