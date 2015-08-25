/**
 * 
 */
package org.zkoss.selector.test.util;

/**
 *
 * @author simonpai
 */
public class Logs {
	
	public static void log(String title, Object ... values){
		System.out.println((title == null || title.isEmpty()?
				"" : title + " ") + join(values));
	}
	
	private static String join(Object ... values){
		if(values.length == 0) return "";
		StringBuffer sb = new StringBuffer(values[0] == null? "null" : values[0].toString());
		for(int i=1; i < values.length; i++) sb.append(", ").append(values[i]);
		return sb.toString();
	}
	
}
