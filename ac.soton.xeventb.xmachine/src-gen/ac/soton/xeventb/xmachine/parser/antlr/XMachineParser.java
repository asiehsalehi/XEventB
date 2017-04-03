/*
 * generated by Xtext
 */
package ac.soton.xeventb.xmachine.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import ac.soton.xeventb.xmachine.services.XMachineGrammarAccess;

public class XMachineParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private XMachineGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT");
	}
	
	@Override
	protected ac.soton.xeventb.xmachine.parser.antlr.internal.InternalXMachineParser createParser(XtextTokenStream stream) {
		return new ac.soton.xeventb.xmachine.parser.antlr.internal.InternalXMachineParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "Machine";
	}
	
	public XMachineGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(XMachineGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}