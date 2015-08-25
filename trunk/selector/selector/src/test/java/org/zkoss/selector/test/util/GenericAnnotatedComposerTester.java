/**
 * 
 */
package org.zkoss.selector.test.util;

import org.zkoss.selector.GenericAnnotatedComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

/**
 *
 * @author simonpai
 */
public abstract class GenericAnnotatedComposerTester 
		extends GenericAnnotatedComposer {
	
	private static final long serialVersionUID = 5948094785460162194L;
	
	protected abstract void doTest(Component comp);
	public abstract void onAssert(Event event);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		doTest(comp);
		Events.postEvent(-9999, "onAssert", comp, null);
	}
	
	protected void log(String title, Object ... values){
		Logs.log(title, values);
	}
	
	protected final void success(){
		log("Successful:", getClass().getSimpleName());
	}
	
}
