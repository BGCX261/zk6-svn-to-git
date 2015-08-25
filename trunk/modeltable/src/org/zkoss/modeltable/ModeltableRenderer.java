package org.zkoss.modeltable;

import org.zkoss.zul.ListModel;


public interface ModeltableRenderer {
	
	public void renderHeader(RowRenderer row, ListModel model);
	
	public void renderItem(RowRenderer row, Object data, int index);
	
}
