/**
 * 
 */
package org.zkoss.selector.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.zkoss.selector.Selectors;
import org.zkoss.selector.test.util.Logs;
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
public class BasicMatchTest {
	
	private final String _selector;
	private final String _zul;
	private final int _expected;
	
	public BasicMatchTest(String selector, String zul, int expected){
		_selector = selector;
		_zul = zul;
		_expected = expected;
	}
	
	@Test
	public void matchComp(Page page) {
		Window win = new Window();
		win.setPage(page);
		Component comp = Executions.getCurrent()
			.createComponentsDirectly(_zul, "zul", win, null);
		assertList(Selectors.find(comp, _selector));
	}
	
	@Test
	public void matchPage(Page page) {
		Executions.getCurrent().createComponentsDirectly(_zul, "zul", null, null);
		assertList(Selectors.find(page, _selector));
	}
	
	private void assertList(List<Component> list){
		Logs.log(_selector, list.toArray());
		Assert.assertNotNull(list);
		Assert.assertEquals(_expected, list.size());
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				
				// simple selector
				{"div", "<div />", 1},
				{"#id", "<div id=\"id\" />", 1},
				{".class", "<div sclass=\"class\" />", 1},
				{"[value=\"text\"]", "<label value=\"text\" />", 1},
				{":empty", "<div />", 1},
				
				// class
				{".a.b", "<div sclass=\"a b c\" />", 1},
				{".a.b.d", "<div sclass=\"a b c\" />", 0},
				{".z.b", "<div zclass=\"z\" sclass=\"a b c\" />", 1},
				
				// attribute
				{"[value^=\"te\"]", "<label value=\"text\" />", 1},
				{"[value$=\"xt\"]", "<label value=\"text\" />", 1},
				{"[value*=\"ex\"]", "<label value=\"text\" />", 1},
				{"[multiline=true]", "<textbox multiline=\"true\" />", 1},
				
				// combinator
				{"div label", "<div><window><label /></window></div>", 1},
				{"window > div", "<window><div><div /></div></window>", 1},
				{"label + label", "<div><label /><textbox /><label /><label /></div>", 1},
				{"label ~ label", "<div><label /><textbox /><label /></div>", 1},
				
				// pseudo class
				//{":root", "<div><div><label /></div><label /></div>"},
				{":empty", "<div />", 1},
				{":only-child", "<div />", 1},
				{":first-child", "<div />", 1},
				{":last-child", "<div />", 1},
				{"textbox:first-child", "<div><label /><label /><div>"+
					"<textbox /><label /></div></div>", 1},
				
				{"*", "<div />", 1}
		});
	}
	
}
