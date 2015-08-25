/**
 * 
 */
package demo.selector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletContext;

import org.zkoss.selector.GenericAnnotatedComposer;
import org.zkoss.selector.Selectors;
import org.zkoss.selector.annotation.Listen;
import org.zkoss.selector.annotation.Wire;
import org.zkoss.selector.impl.ComponentIterator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * 
 * @author simonpai
 */
public class SelectorDemoController extends GenericAnnotatedComposer {
	
	private static final long serialVersionUID = 6738045491398493198L;
	
	private static final String CLEAR = "clear.zul";
	
	@Wire
	private Desktop desktop;
	@Wire("#componentsWindow")
	private Window componentsWindow;
	@Wire("#zulBox")
	private Textbox zulSrcBox;
	@Wire("#selectorBox")
	private Textbox selectorBox;
	@Wire("#ctrlVL listbox")
	private Listbox zulListbox; 
	@Wire("#ctrlVL label[value^='Index'] + label")
	private Label indexLb;
	@Wire("#ctrlVL label[value^='Component'] + label")
	private Label compLb;
	@Wire("#ctrlVL label[value^='Parse'] + label")
	private Label parseLb;
	@Wire("#ctrlVL label[value^='Step'] + label")
	private Label timeLb;
	@Wire("#iterLb")
	private Label iterLb;
	
	private String _currSelector;
	private ComponentIterator _iterator;
	private Component _selected;
	
	@Listen("onClick = #ctrlVL button[label='Step']; onOK = #selectorBox")
	public void onStep(Event event) {
		step();
	}
	
	@Listen("onClick = #ctrlVL button[label='Reset']")
	public void onReset(Event event) {
		reset();
	}
	
	@Listen("onChange = #zulBox")
	public void onSourceChange(Event event) {
		loadZUL();
		reset();
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		final String curPath = 
			((ServletContext) desktop.getWebApp().getNativeContext())
				.getRealPath("zuls");
		File root = new File(curPath);
		File[] zuls = root.listFiles(new FileFilter(){
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".zul");
			}
		});
		Arrays.sort(zuls, new java.util.Comparator<File>() {
			public int compare(File a, File b) {
				String aname = a.getName();
				String bname = b.getName();
				if(CLEAR.equals(aname)) return -1;
				if(CLEAR.equals(bname)) return 1;
				return aname.compareTo(bname);
			}
		});
		zulListbox.addEventListener("onSelect", new EventListener(){
			public void onEvent(Event event) throws Exception {
				selectZUL((Listitem)((SelectEvent) event).getReference());
			}
		});
		zulListbox.addEventListener("onAfterRender", new EventListener(){
			public void onEvent(Event event) throws Exception {
				selectZUL(zulListbox.getItemAtIndex(1));
			}
		});
		zulListbox.setItemRenderer(new ListitemRenderer(){
			public void render(Listitem listitem, Object data) {
				listitem.setValue(data);
				final String fname = ((File) data).getName();
				listitem.setLabel(fname.substring(0, fname.length()-4));
			}
		});
		zulListbox.setModel(new ListModelList(zuls));
	}
	
	
	
	// helper //
	private ComponentIterator getComponentIterator(String selector){
		long pst = new Date().getTime();
		ComponentIterator result = 
			(ComponentIterator) Selectors.iterator(componentsWindow, selector);
		long ptime = new Date().getTime() - pst;
		parseLb.setValue("" + ptime + "ms");
		return result;
	}
	
	private void step(){
		String selector = selectorBox.getValue();
		if(!selector.equals(_currSelector)) {
			_currSelector = selector;
			_iterator = getComponentIterator(selector);
		}
		
		removeMark(_selected);
		long start = new Date().getTime();
		boolean hasNext = _iterator.hasNext();
		long timeCost = new Date().getTime() - start;
		addMark(_selected = hasNext? _iterator.next() : null);
		indexLb.setValue(hasNext? "" + (_iterator.nextIndex() - 1) : "-");
		compLb.setValue(_selected == null? "null" : _selected.toString());
		iterLb.setValue(_iterator.toString());
		timeLb.setValue("" + timeCost + "ms");
	}
	
	
	private void reset() {
		String selector = selectorBox.getValue();
		if(selector == null || selector.isEmpty()) return;
		_iterator = getComponentIterator(selector);
		removeMark(_selected);
		_selected = null;
		indexLb.setValue("-");
		compLb.setValue("-");
		timeLb.setValue("-");
		iterLb.setValue("");
	}
	
	private void selectZUL(Listitem listitem) throws Exception {
		File f = (File)(listitem.getValue());
		if(CLEAR.equals(f.getName())) {
			zulSrcBox.setValue("");
			Components.removeAllChildren(componentsWindow);
		} else {
			zulSrcBox.setValue(readFile(f));
			loadZUL();
		}
		reset();
	}
	
	private void loadZUL(){
		Components.removeAllChildren(componentsWindow);
		try {
			Executions.createComponentsDirectly(zulSrcBox.getValue(), "zul", 
					componentsWindow, null);
		} catch (RuntimeException e) {
			Executions.createComponentsDirectly(
					"<div class='code'>" + e.getMessage() + "</div>", "zul", 
					componentsWindow, null);
		}
	}
	
	private void removeMark(Component selected){
		if(selected == null || !(selected instanceof HtmlBasedComponent)) return;
		HtmlBasedComponent comp = (HtmlBasedComponent) selected;
		String sc = comp.getSclass();
		comp.setSclass(sc.substring(0, sc.lastIndexOf(" __selected__")));
	}
	
	private void addMark(Component selected){
		if(selected == null || !(selected instanceof HtmlBasedComponent)) return;
		HtmlBasedComponent comp = (HtmlBasedComponent) selected;
		comp.setSclass(comp.getSclass() + " __selected__");
	}
	
	private String readFile(File file) throws Exception {
		String newLine = System.getProperty("line.separator");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String next = "";
		StringBuffer sb = new StringBuffer();
		while((next = br.readLine()) != null)
			sb.append(next).append(newLine);
		return sb.toString();
	}
}

