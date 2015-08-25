/**
 * 
 */
package demo.selector.composer;

import java.util.List;

import org.zkoss.selector.GenericAnnotatedComposer;
import org.zkoss.selector.annotation.Listen;
import org.zkoss.selector.annotation.Wire;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.impl.InputElement;

/**
 *
 * @author simonpai
 */
public class FormController extends GenericAnnotatedComposer {
	
	private static final long serialVersionUID = 8317726417294692545L;
	
	@Wire("textbox, intbox, decimalbox, datebox, spinner")
	List<InputElement> inputs;
	
	@Listen("onClick = button[label='Clear']")
	public void onClear(MouseEvent event) {
		for(InputElement i : inputs) i.setText("");
	}
	
	@Listen("onClick = button[label^='Set']")
	public void onSetWidth(MouseEvent event) {
		for(InputElement i : inputs) i.setWidth("200px");
	}
	
}
