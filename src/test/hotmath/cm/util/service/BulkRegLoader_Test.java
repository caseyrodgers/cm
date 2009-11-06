package hotmath.cm.util.service;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

public class BulkRegLoader_Test extends TestCase {
    
    
    public BulkRegLoader_Test(String name) {
        super(name);
    }
    
    public void testParse1() throws Exception {
        
        String input = "# comment\n" +
                       "NON_ENTRY_ONE_TOKEN\n" +
                       "name1\tvalue1\n" +
                       "\"name2_last, name2_first\"\tvalue2\n" +
                       "name3\tvalue3\n" +
                       "\n";
        ByteArrayInputStream bs = new ByteArrayInputStream(input.getBytes());
        BulkRegLoader brl = new BulkRegLoader();
        brl.readStream(bs);
        
        assertTrue(brl.getEntries().size() == 3);
        assertTrue(brl.getErrorCount() == 0);
        assertTrue(brl.getEntries().get(1).getName().equals("name2_last, name2_first"));
    }

}
