/**
 * 
 */
package org.zkoss.selector.test.composer;

import java.util.Map;

import junit.framework.Assert;

import org.zkoss.selector.annotation.Wire;
import org.zkoss.selector.test.util.GenericAnnotatedComposerTester;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;

/**
 *
 * @author simonpai
 */
public class ImplicitComposer extends GenericAnnotatedComposerTester {
	
	private static final long serialVersionUID = -3569676579109458833L;
	
	@Wire
	private Log log;
	@Wire
	private Object self;
	@Wire
	private IdSpace spaceOwner;
	
	@Wire
	private Page page;
	@Wire
	private Desktop desktop;
	@Wire
	private Session session;
	@Wire
	private WebApp application;
	@Wire
	private Execution execution;
	
	@Wire
	private Map<Object, Object> componentScope;
	@Wire
	private Map<Object, Object> spaceScope;
	@Wire
	private Map<Object, Object> pageScope;
	@Wire
	private Map<Object, Object> desktopScope;
	@Wire
	private Map<Object, Object> sessionScope;
	@Wire
	private Map<Object, Object> applicationScope;
	@Wire
	private Map<Object, Object> requestScope;
	
	@Wire
	private Map<Object, Object> arg;
	@Wire
	private Map<Object, Object> param;
	
	@Override
	protected void doTest(Component comp) {
		
		Assert.assertNotNull(log);
		Assert.assertSame(comp, self);
		Assert.assertSame(comp.getSpaceOwner(), spaceOwner);
		
		Assert.assertSame(comp.getPage(), page);
		Assert.assertSame(comp.getDesktop(), desktop);
		Assert.assertSame(comp.getDesktop().getSession(), session);
		Assert.assertSame(comp.getDesktop().getWebApp(), application);
		//Assert.assertEquals(comp.getDesktop().getExecution(), execution);
		
		Assert.assertNotNull(componentScope);
		Assert.assertNotNull(spaceScope);
		Assert.assertNotNull(pageScope);
		Assert.assertNotNull(desktopScope);
		Assert.assertNotNull(sessionScope);
		Assert.assertNotNull(applicationScope);
		Assert.assertNotNull(requestScope);
		
		Assert.assertNotNull(arg);
		Assert.assertNotNull(param);
		
	}
	
	@Override
	public void onAssert(Event event) {
		success();
	}
	
}
