/* GenericSelectorEngine.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jun 18, 2011 8:18:05 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.selector.ext;

import org.zkoss.selector.impl.model.Attribute;

/**
 *
 * @author simonpai
 */
public interface SelectorEngine<T> {
	
	// tree //
	public T getFirstChild(T item);
	
	public T getNextSibling(T item);
	
	
	
	// matchers //
	public boolean matchType(T item, String name);
	
	public boolean matchClass(T item, String name);
	
	public boolean matchAttribute(T item, Attribute attribute);
	
	public boolean matchPseudoClass(T item, String name);
	
	// TODO: merge pseudo class def?
	
}
