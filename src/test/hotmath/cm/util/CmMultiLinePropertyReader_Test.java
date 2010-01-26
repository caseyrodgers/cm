package hotmath.cm.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class CmMultiLinePropertyReader_Test extends TestCase {
    
    public CmMultiLinePropertyReader_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        CmMultiLinePropertyReader.getInstance().put("PROP_1", "PROPERTY_1");
        CmMultiLinePropertyReader.getInstance().put("PROP_2", "PROPERTY_1 WITH REPLACMENT: $$THIS$$");
    }
    
    public void testSingleProperty() throws Exception {
        String p = CmMultiLinePropertyReader.getInstance().getProperty("PROP_1").trim();
        assertNotNull(p);
    }
    
    
    public void testSinglePropertyReplacement() throws Exception {
        Map<String,String> replace = new HashMap<String,String>();
        replace.put("THIS", "WITHTHAT");
        String p = CmMultiLinePropertyReader.getInstance().getProperty("PROP_2", replace).trim();
        assertTrue(p.indexOf("WITHTHAT") > -1);
    }
    
    public void testSinglePropertyNotExist() throws Exception {
        Map<String,String> replace = new HashMap<String,String>();
        replace.put("THIS", "WITHTHAT");
        String p = CmMultiLinePropertyReader.getInstance().getProperty("NOT_EXIST");
        assertTrue(p == null);
    }

}
