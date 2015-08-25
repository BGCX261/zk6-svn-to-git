/**
 * 
 */
package org.zkoss.selector.test.parse;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.zkoss.selector.impl.Parser;
import org.zkoss.selector.impl.Token;
import org.zkoss.selector.impl.Tokenizer;
import org.zkoss.selector.impl.model.Selector;
import org.zkoss.selector.test.util.Logs;
import org.zkoss.selector.test.util.Texts;

/**
 *
 * @author simonpai
 */
@RunWith(Parameterized.class)
public class MultipleSelectorParseTest {
	
	private final String _selector;
	private final String _expected;
	private final int _expectedSize;
	
	public MultipleSelectorParseTest(String selector, String expected, int size){
		_selector = selector;
		_expected = expected;
		_expectedSize = size;
	}
	
	@Test
	public void parseSingleSelectors(){
		
		Logs.log("[Parse Test] Selector:", _selector, _expected);
		
		List<Token> tokens = new Tokenizer().tokenize(_selector);
		Logs.log("[Parse Test] Tokens:", tokens);
		
		List<Selector> selectors = new Parser().parse(tokens, _selector);
		Assert.assertNotNull("selectors", selectors);
		Assert.assertEquals(_expectedSize, selectors.size());
		
		Assert.assertEquals("selector#toString()", _expected, 
				Texts.join(selectors, ", "));
		
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				
				{"div,label", "div, label", 2},
				{"div , label", "div, label", 2},
				{"window ,window,window", "window, window, window", 3},
				{"div.class, label#id", "div.class, label#id", 2},
				{"*, *, *, *, *", "*, *, *, *, *", 5},
				
				{"div, div", "div, div", 2}
		});
	}
}
