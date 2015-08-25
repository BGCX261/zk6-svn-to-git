/**
 * 
 */
package demo.selector.composer;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.selector.GenericAnnotatedComposer;
import org.zkoss.selector.annotation.Wire;
import org.zkoss.selector.annotation.Listen;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.*;

/**
 *
 * @author simonpai
 */
public class GroupboxController extends GenericAnnotatedComposer {
	
	private static final long serialVersionUID = 7125141965429889640L;
	
	@Wire("groupbox")
	List<Groupbox> groupboxList = new ArrayList<Groupbox>();
	
	@Wire("groupbox")
	Groupbox[] groupboxArray;
	
	@Wire("groupbox")
	Groupbox groupbox;
	
	@Listen("onClick = label")
	public void onAction(MouseEvent event){
		alert(((Label) event.getTarget()).getValue());
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		System.out.println(groupboxList);
		System.out.println(groupboxArray);
		System.out.println(groupbox);
		
	}
	
}
