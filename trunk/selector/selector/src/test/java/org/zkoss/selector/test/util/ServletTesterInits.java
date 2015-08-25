/**
 * 
 */
package org.zkoss.selector.test.util;

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.jetty.testing.ServletTester;
import org.eclipse.jetty.util.resource.Resource;

/**
 *
 * @author simonpai
 */
public class ServletTesterInits {
	
	public static void setResourceBase(ServletTester tester, 
			String resourceBase) throws MalformedURLException, IOException {
		tester.getContext().setBaseResource(
				Resource.newResource(resourceBase));
	}
	
}
