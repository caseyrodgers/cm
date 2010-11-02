package hotmath.gwt.cm_mobile2.client;

import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import junit.framework.TestCase;

public class TokenParser_Test extends TestCase {
    public TokenParser_Test(String name) {
        super(name);
    }
    
    public void testIt() throws Exception {
        String test1 = "lesson:type:2";
        TokenParser tp = new TokenParser(test1);
        assertTrue(tp.getOrdinal() == 2);
    }

}
