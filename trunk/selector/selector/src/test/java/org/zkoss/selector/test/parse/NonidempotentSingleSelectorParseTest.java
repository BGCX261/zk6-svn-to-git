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

/**
 *
 * @author simonpai
 */
@RunWith(Parameterized.class)
public class NonidempotentSingleSelectorParseTest {
	
	private final String _selector;
	private final String _expected;
	
	public NonidempotentSingleSelectorParseTest(String selector, String expected){
		_selector = selector;
		_expected = expected;
	}
	
	@Test
	public void parseSingleSelectors(){
		
		Logs.log("[Parse Test] Selector:", _selector, _expected);
		
		List<Token> tokens = new Tokenizer().tokenize(_selector);
		Logs.log("[Parse Test] Tokens:", tokens);
		
		List<Selector> selectors = new Parser().parse(tokens, _selector);
		Assert.assertNotNull("selectors", selectors);
		Assert.assertEquals(1, selectors.size());
		
		Selector s = selectors.get(0);
		Assert.assertEquals("selector#toString()", _expected, s.toString());
		
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				
				// simple selector with universal char
				{"*#myid", "#myid"},
				{"*.myclass", ".myclass"},
				{"*[value=99]", "[value=99]"},
				{"*[value=\"mystr\"]", "[value=\"mystr\"]"},
				{"*:mypseudoclass", ":mypseudoclass"},
				
				// simple selector sequence with universal char
				
				// other
				{":mypseudoclass(-2n+1, a, c$c)", ":mypseudoclass(-2n+1,a,c$c)"},
				
				{"", ""}
		});
	}
	
}
