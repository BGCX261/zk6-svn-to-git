/* SelectiveIterable.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jun 18, 2011 8:44:42 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.selector.ext;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.zkoss.lang.Strings;
import org.zkoss.selector.impl.Parser;
import org.zkoss.selector.impl.model.Selector;
import org.zkoss.selector.impl.model.Selector.Combinator;

/**
 *
 * @author simonpai
 */
public class SelectiveIterable<T> implements Iterable<T> {
	
	private final T _first;
	private final String _selector;
	private final SelectorEngine<T> _engine;
	
	public SelectiveIterable(T item, String selector, SelectorEngine<T> engine) {
		if(item == null || Strings.isBlank(selector))
			throw new IllegalArgumentException();
		_first = item;
		_selector = selector;
		_engine = engine;
	}
	
	public Iterator<T> iterator() {
		return new SelectiveIterator();
	}
	
	public class SelectiveIterator implements Iterator<T> {
		
		private MatchContext<T> _currCtx;
		private final List<Selector> _selectors;
		private boolean _ready = false;
		private T _next;
		private int _index = -1;
		
		public SelectiveIterator() {
			_selectors = new Parser().parse(_selector);
		}
		
		/**
		 * Return true if it has next component.
		 */
		public boolean hasNext() {
			loadNext();
			return _next != null;
		}
		
		/**
		 * Return the next matched component. A NoSuchElementException will be 
		 * throw if next component is not available.
		 */
		public T next() {
			if(!hasNext()) 
				throw new NoSuchElementException();
			_ready = false;
			return _next;
		}
		
		/**
		 * Throws UnsupportedOperationException.
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Return the next matched component, but the iteration is not proceeded.
		 */
		public T peek() {
			if(!hasNext()) 
				throw new NoSuchElementException();
			return _next;
		}
		
		/**
		 * Return the index of the next component.
		 */
		public int nextIndex() {
			return _ready ? _index : _index + 1;
		}
		
		
		
		// helper //
		private void loadNext(){
			if(_ready) 
				return;
			_next = seekNext();
			_ready = true;
		}
		
		private T seekNext() {
			_currCtx = _index < 0 ? buildRootCtx() : buildNextCtx();
			
			while(_currCtx != null && !_currCtx.isMatched()) 
				_currCtx = buildNextCtx();
			if(_currCtx != null) {
				_index++;
				return _currCtx.getItem();
			}
			return null; 
		}
		
		private MatchContext<T> buildRootCtx(){
			T rt = _first;
			MatchContext<T> ctx = new MatchContext<T>(rt, _engine, _selectors);
			matchLevel0(ctx);
			return ctx;
		}
		
		private MatchContext<T> buildNextCtx(){
			
			if(_engine.getFirstChild(_currCtx.getItem()) != null) 
				return buildFirstChildCtx(_currCtx);
			
			while(_engine.getFirstChild(_currCtx.getItem()) == null) {
				_currCtx = _currCtx.getParent();
				if(_currCtx == null) 
					return null; // reached root
			}
			
			return buildNextSiblingCtx(_currCtx);
		}
		
		private MatchContext<T> buildFirstChildCtx(MatchContext<T> parent){
			
			MatchContext<T> ctx = new MatchContext<T>(
					_engine.getFirstChild(parent.getItem()), _engine, parent);
			matchLevel0(ctx);
			
			for(Selector selector : _selectors) {
				int i = selector.getSelectorIndex();
				
				for(int j = 0; j < selector.size()-1; j++){
					switch(selector.getCombinator(j)){
					case DESCENDANT:
						if(parent.isQualified(i, j)) ctx.setQualified(i, j);
						// no break
					case CHILD:
						if(parent.isQualified(i, j) && match(selector, ctx, j+1)) 
							ctx.setQualified(i, j+1);
						break;
					}
				}
			}
			return ctx;
		}
		
		private MatchContext<T> buildNextSiblingCtx(MatchContext<T> ctx){
			
			ctx.moveToNextSibling();
			
			for(Selector selector : _selectors) {
				int i = selector.getSelectorIndex();
				ctx.setQualified(i, selector.size()-1, 
						match(selector, ctx, selector.size()-1));
				
				for(int j = selector.size() - 2; j > -1; j--){
					Combinator cb = selector.getCombinator(j);
					MatchContext<T> parent = ctx.getParent();
					
					switch(cb){
					case DESCENDANT:
					case CHILD:
						if(parent != null && parent.isQualified(i, j) && 
								match(selector, ctx, j+1))
							ctx.setQualified(i, j+1);
						break;
					case GENERAL_SIBLING:
						if(ctx.isQualified(i, j)) 
							ctx.setQualified(i, j+1, 
									match(selector, ctx, j+1));
						break;
					case ADJACENT_SIBLING:
						ctx.setQualified(i, j+1, ctx.isQualified(i, j) && 
								match(selector, ctx, j+1));
						ctx.setQualified(i, j, false);
					}
				}
			}
			
			matchLevel0(ctx);
			return ctx;
		}
		
		private void matchLevel0(MatchContext<T> ctx) {
			for(Selector selector : _selectors)
				if(match(selector, ctx, 0)) 
					ctx.setQualified(selector.getSelectorIndex(), 0);
		}
		
		private boolean match(Selector selector, MatchContext<T> ctx, int index) {
			return ctx.match(selector.get(index), null); // TODO: pseudo class
		}
		
		// TODO: remove after testing
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("ComponentIterator: \n* index: ").append(_index);
			for(MatchContext<T> c = _currCtx; c != null; c = c.getParent())
				sb.append("\n").append(c);
			return sb.append("\n\n").toString();
		}
		
	}
	
}
