package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentQuickSearcher_Test extends CmDbTestCase {
    Set<Integer> studentUids=null;
    StudentQuickSearcher searcher=null;
    public StudentQuickSearcher_Test(String name) {
        super(name);
        
    }
    public void testSearch1() throws Exception {
        List<Integer> matches = searcher.doQuickSearch("A");
        assertTrue(matches.size() > 0);
    }
    
    public void testSearchZeroUids() throws Exception {
        StudentQuickSearcher searcher2 = new StudentQuickSearcher(conn, new HashSet<Integer>());
        List<Integer> matches = searcher2.doQuickSearch("A");
        assertTrue(matches.size() == 0);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        studentUids = new HashSet<Integer>();
        studentUids.addAll(Arrays.asList(23472,23253,23254,23475,23470,23255,23476,23256,23257,23258,23259,23260,23502,23487,23261,23474,23485,23493,23482,23490,23469,23486,23468,23491,23503,23501,23495,23481,23483,23519,23471,23520,23521,23522,23523,23488,23494,23524,23525,23478,23526,23527,23528,23529,23530,23484,23531,23532,23533,23534,23473,23498,23479,23496,23477,23492,23499,23540,23541,23543,23544,23275,23281,23282,23684,23283,23480,23500,23489,23497,23546));
        searcher = new StudentQuickSearcher(conn, studentUids);
    }
}
