/* MatchContext.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jun 19, 2011 3:47:25 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.selector.ext;

import java.util.List;
import java.util.Map;

import org.zkoss.selector.impl.PseudoClassDef;
import org.zkoss.selector.impl.model.Attribute;
import org.zkoss.selector.impl.model.Selector;
import org.zkoss.selector.impl.model.SimpleSelectorSequence;

/**
 *
 * @author simonpai
 */
public class MatchContext<T> {
	
	private final SelectorEngine<T> _engine;
	private MatchContext<T> _parent;
	private T _item;
	
	// qualified positions
	private boolean[][] _qualified;
	
	// TODO: Extension: basic pseudo class support
	// pseudo-class support
	//private int _compChildIndex = -1;
	
	/*package*/ MatchContext(T item, SelectorEngine<T> engine, List<Selector> selectors) {
		_engine = engine;
		_item = item;
		_qualified = new boolean[selectors.size()][];
		
		for(Selector s : selectors)
			_qualified[s.getSelectorIndex()] = 
				new boolean[s.size()];
		//_compChildIndex = getComponentIndex();
	}
	
	/*package*/ MatchContext(T item, SelectorEngine<T> engine, MatchContext<T> parent) {
		_engine = engine;
		_item = item;
		int selectorListSize = parent._qualified.length;
		_qualified = new boolean[selectorListSize][];
		for(int i=0; i < selectorListSize; i++)
			_qualified[i] = new boolean[parent._qualified[i].length];
		_parent = parent;
		// _compChildIndex = 0;
	}
	
	/*package*/ void moveToNextSibling() {
		_item = _engine.getNextSibling(_item);
		//_compChildIndex++;
	}
	
	/**
	 * 
	 * @return
	 */
	public MatchContext<T> getParent() {
		return _parent;
	}
	
	/**
	 * 
	 * @return
	 */
	public T getItem() {
		return _item;
	}
	
	// TODO: getChildIndex, getSiblingSize
	
	// match position //
	/**
	 * Return true if the component matched the given position of the given 
	 * selector.
	 * @param selector
	 * @param position
	 * @return
	 */
	public boolean isQualified(int selectorIndex, int position) {
		return _qualified[selectorIndex][position];
	}
	
	/*package*/ void setQualified(int selectorIndex, int position) {
		setQualified(selectorIndex, position, true);
	}
	
	/*package*/ void setQualified(int selectorIndex, int position, 
			boolean qualified) {
		_qualified[selectorIndex][position] = qualified;
	}
	
	/**
	 * Return true if the component matched the last position of any selectors
	 * in the list. (i.e. the one we are looking for)
	 * @return
	 */
	public boolean isMatched() {
		for(int i = 0; i< _qualified.length; i++) 
			if(isMatched(i)) 
				return true;
		return false;
	}
	
	/**
	 * Return true if the component matched the last position of the given
	 * selector.
	 * @param selectorIndex
	 * @return
	 */
	public boolean isMatched(int selectorIndex) {
		boolean[] quals = _qualified[selectorIndex];
		return quals[quals.length-1];
	}
	
	
	
	// match local property //
	/**
	 * Return true if the component qualifies the local properties of a given
	 * SimpleSelectorSequence.
	 * @param seq 
	 * @param defs 
	 * @return
	 */
	public boolean match(SimpleSelectorSequence seq, 
			Map<String, PseudoClassDef> defs) {
		return _engine.matchType(_item, seq.getType())
			&& matchClasses(seq)
			&& matchAttributes(seq); // TODO: pseudo class
	}
	
	private boolean matchClasses(SimpleSelectorSequence seq) {
		for(String c : seq.getClasses())
			if(!_engine.matchClass(_item, c))
				return false;
		return true;
	}
	
	private boolean matchAttributes(SimpleSelectorSequence seq) {
		for(Attribute attr : seq.getAttributes()) {
			if(!_engine.matchAttribute(_item, attr))
				return false;
		}
		return true;
	}
	
	// TODO: remove after testing
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		for(boolean[] bs : _qualified) { 
			sb.append("Q[");
			for(boolean b : bs) sb.append(b?'1':'0');
			sb.append("]");
		}
		return sb.append(", ").append(_item).toString();
	}
	
	
	
	// helper //
	/*
	private int getComponentIndex(){
		Component curr = _comp;
		int index = -1;
		while(curr != null) {
			curr = curr.getPreviousSibling();
			index++;
		}
		return index;
	}
	*/

	
	
}
