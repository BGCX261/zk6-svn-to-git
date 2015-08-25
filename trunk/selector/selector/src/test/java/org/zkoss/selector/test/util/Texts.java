/**
 * 
 */
package org.zkoss.selector.test.util;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author simonpai
 */
public class Texts {
	
	public static String join(List<? extends Object> objs, String joiner){
		if(objs == null || objs.isEmpty()) return "";
		Iterator<? extends Object> iter = objs.iterator();
		StringBuffer sb = new StringBuffer(iter.next().toString());
		while(iter.hasNext()) sb.append(joiner).append(iter.next());
		return sb.toString();
	}
	
}
