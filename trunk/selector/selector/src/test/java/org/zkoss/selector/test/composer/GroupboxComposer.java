/**
 * 
 */
package org.zkoss.selector.test.composer;

import java.util.List;

import junit.framework.Assert;

import org.zkoss.selector.annotation.Wire;
import org.zkoss.selector.annotation.Listen;
import org.zkoss.selector.test.util.GenericAnnotatedComposerTester;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;

/**
 *
 * @author simonpai
 */
public class GroupboxComposer extends GenericAnnotatedComposerTester {
	
	private static final long serialVersionUID = -29631043995107349L;
	
	// event flags //
	private boolean _labelAction;
	
	
	
	// selector //
	@Wire("groupbox")
	private Groupbox[] gArray;
	
	@Wire("groupbox")
	private List<Groupbox> gList;
	
	@Wire("groupbox")
	private Groupbox gb;
	
	@Wire
	private Label lb0;
	
	@Wire("#arrow")
	private Div div0;
	
	@Listen("onClick = #lb0")
	public void onLabelAction(Event event) {
		_labelAction = true;
	}
	
	
	
	// implicit variables //
	@Wire
	private Desktop desktop;
	
	
	
	// test //
	@Override
	protected void doTest(Component comp) {
		
		// verify auto wire
		Assert.assertNotNull(gArray);
		Assert.assertEquals(4, gArray.length);
		Groupbox gbExp = gArray[0];
		
		Assert.assertNotNull(gList);
		Assert.assertEquals(4, gList.size());
		Assert.assertSame(gbExp, gList.get(0));
		
		Assert.assertNotNull(gb);
		Assert.assertSame(gbExp, gb);
		
		Assert.assertNotNull(lb0);
		Assert.assertEquals("lb0", lb0.getId());
		
		Assert.assertNotNull(desktop);
		Assert.assertEquals(comp.getDesktop(), desktop);
		
		// verify forwarding
		Events.postEvent("onClick", lb0, null);
		Events.postEvent("onClick", div0, null);
		
	}
	
	@Override
	public void onAssert(Event event){
		Assert.assertTrue(_labelAction);
		success();
	}
	
}
