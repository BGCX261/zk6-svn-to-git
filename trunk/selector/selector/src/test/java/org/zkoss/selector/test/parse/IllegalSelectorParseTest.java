/**
 * 
 */
package org.zkoss.selector.test.parse;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.zkoss.selector.impl.ParseException;
import org.zkoss.selector.impl.Parser;
import org.zkoss.selector.impl.Token;
import org.zkoss.selector.impl.Tokenizer;
import org.zkoss.selector.test.util.Logs;

/**
 *
 * @author simonpai
 */
@Ignore
@RunWith(Parameterized.class)
public class IllegalSelectorParseTest {
	
	private final String _selector;
	
	public IllegalSelectorParseTest(String selector){
		_selector = selector;
	}
	
	@Test(expected = ParseException.class)
	public void parseSingleSelectors(){
		
		Logs.log("[Parse Test] Selector:", _selector);
		
		List<Token> tokens = new Tokenizer().tokenize(_selector);
		Logs.log("[Parse Test] Tokens:", tokens);
		
		new Parser().parse(tokens, _selector);
		
	}
	
	@Parameters
	public static Collection<Object[]> selectors() {
		return Arrays.asList(new Object[][]{
				
				// illegal char
				{"div"},
				{"#myid"},
				{".myclass"},
				{"[value=99]"},
				{"[value=\"mystr\"]"},
				{":mypseudoclass"},
				
				{"!!!"}
		});
	}
	
}
