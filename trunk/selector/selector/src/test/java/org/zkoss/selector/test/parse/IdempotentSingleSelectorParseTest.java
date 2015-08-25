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
public class IdempotentSingleSelectorParseTest {
	
	private final String _selector;
	
	public IdempotentSingleSelectorParseTest(String selector){
		_selector = selector;
	}
	
	@Test
	public void parseSingleSelectors(){
		
		Logs.log("[Parse Test] Selector:", _selector);
		
		List<Token> tokens = new Tokenizer().tokenize(_selector);
		Logs.log("[Parse Test] Tokens:", tokens);
		
		List<Selector> selectors = new Parser().parse(tokens, _selector);
		Assert.assertNotNull("selectors", selectors);
		Assert.assertEquals(1, selectors.size());
		
		Selector s = selectors.get(0);
		Assert.assertEquals("selector#toString()", _selector, s.toString());
		
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				
				// simple selector
				{"*"},
				{"div"},
				{"#myid"},
				{".myclass"},
				{"[value=99]"},
				{"[value=\"mystr\"]"},
				{":mypseudoclass"},
				{":mypseudoclass(1)"},
				{":mypseudoclass(1,2,3)"},
				
				// pseudo class
				{":mypseudoclass(2n+1)"},
				{":mypseudoclass(2n-1)"},
				{":mypseudoclass(-2n+1)"},
				{":mypseudoclass(-2n-1)"},
				{":mypseudoclass(-2n)"},
				{":mypseudoclass(2n)"},
				{":mypseudoclass(+1)"},
				{":mypseudoclass(-1)"},
				{":mypseudoclass(-2n+1,a,c$c)"},
				
				// simple selector sequence
				{"label#id"},
				{"label.class"},
				{"label[value=1]"},
				{"label:pseudoclass"},
				{"#id.class:pseudoclass[value=1]"},
				{"label#id.class:pseudoclass[value=1]"},
				{"label#id.class:pseudoclass(1,2)[value=\"1\"]"},
				
				// multiple selector sequence
				{"div div"},
				{"div > div"},
				{"div + div"},
				{"div ~ div"},
				{"window * > label + div ~ window"},
				
				// miscellaneous use
				
				
				{""}
		});
	}
	
}
