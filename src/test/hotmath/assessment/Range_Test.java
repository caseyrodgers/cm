package hotmath.assessment;

import junit.framework.TestCase;

public class Range_Test extends TestCase{
    
    public Range_Test(String name) {
        super(name);
    }

    public void testRangeNull() throws Exception {
        Range range = new Range(null);
        assertTrue(range.getRange() == null);
    }

    public void testRange1() throws Exception {
        Range range = new Range("TEST:1");
        assertTrue(range.getGradeLevels().get(0).equals(new Integer(1)));
    }
    
    public void testRange2() throws Exception {
        Range range = new Range("TEST:1-2");
        assertTrue(range.getGradeLevels().size() == 2);
    }

}
