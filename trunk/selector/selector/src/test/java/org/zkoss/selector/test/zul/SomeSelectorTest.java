/**
 * 
 */
package org.zkoss.selector.test.zul;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.zkoss.selector.Selectors;
import org.zkoss.test.server.junit.ZKServerParameterized;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Window;

/**
 *
 * @author TonyQ
 */
@RunWith(ZKServerParameterized.class)
public class SomeSelectorTest {
	
	private final String _zul;
	private final String _selector;
	private final int _expectedSize;
	
	public SomeSelectorTest(String selector, String zul, int size){
		_zul = zul;
		_selector = selector;		
		_expectedSize = size;
	}
	
	@Test
	public void parseSingleSelectors(Page page){
		
		Window win = new Window();
		win.setPage(page);
		Component comp = Executions.getCurrent()
			.createComponentsDirectly(_zul, "zul", win, null);
		
		List<Component> list = Selectors.find(comp, _selector);
		System.out.println(_selector);
		for(Component c : list) System.out.println(c);
		Assert.assertNotNull(list);
		Assert.assertEquals(_expectedSize, list.size());
		
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		String zul = "<div id='myroot'><div class='container'>"+
			"<textbox id='txt' /> <label value='hello' />"+
			"<listbox />"+
			"<hbox style='width:500px' />"+			
			"<hbox hflex='1' />"+		
			"<hbox hflex='1' vflex='1' style='width:500px' />"+
			"<vbox />"+
			"</div></div>";
		
		return Arrays.asList(new Object[][]{
				{"div", zul, 2},
				{"vbox", zul, 1},
				{"hbox", zul, 3},
				{"div, hbox", zul, 5},
				{"#myroot .container textbox", zul, 1},
				{"#myroot *", zul, 8},
				{"#myroot textbox", zul, 1},
				{"#myroot label[value=hello]", zul, 1},
				{"#myroot textbox[id=txt]", zul, 1},
				{"#myroot .container textbox[id=txt]", zul, 1},
				{".container hbox", zul, 3},
				{".container hbox[vflex=1]", zul, 1},
				{".container hbox[hflex=1]", zul, 2},
				{".container hbox[hflex=1][vflex=1]", zul, 1},
				{".container hbox[style=\"width:500px\"]", zul, 2},
		});
	}
	
}
