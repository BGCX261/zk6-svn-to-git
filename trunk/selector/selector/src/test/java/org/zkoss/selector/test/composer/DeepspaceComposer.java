/**
 * 
 */
package org.zkoss.selector.test.composer;

import java.util.List;

import junit.framework.Assert;

import org.zkoss.selector.annotation.Listen;
import org.zkoss.selector.annotation.Wire;
import org.zkoss.selector.test.util.GenericAnnotatedComposerTester;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 *
 * @author simonpai
 */
public class DeepspaceComposer extends GenericAnnotatedComposerTester {
	
	private static final long serialVersionUID = -2026018224470295322L;
	
	// event flag //
	private int _labelActionCount = 0;
	
	@Wire("#lb")
	List<Label> lbs;
	
	@Wire("#tb")
	Textbox tb;
	
	@Listen("onClick = #lb")
	public void onLabelActions(Event event) {
		_labelActionCount++;
	}
	
	@Override
	protected void doTest(Component comp) {
		
		Assert.assertNotNull(lbs);
		Assert.assertEquals(4, lbs.size());
		
		Assert.assertNotNull(tb);
		
		Events.postEvent("onClick", tb, null);
		for(Label lb : lbs) Events.postEvent("onClick", lb, null);
	}
	
	@Override
	public void onAssert(Event event) {
		Assert.assertEquals(4, _labelActionCount);
		success();
	}
	
}
