package org.zkoss.modeltable;

import org.zkoss.zk.ui.Component;

public interface RowRenderer {

	public Modeltable getTable();
	
	public void appendHtml(String html);
	
	public void appendComponent(Component comp);
	
}
