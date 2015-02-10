package hotmath.assessment;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.inmh.INeedMoreHelpItem;

import java.util.ArrayList;
import java.util.List;

public class InmhItemData_Test extends CmDbTestCase {
    
    public InmhItemData_Test(String name) {
        super(name);
    }
    
    public void testRange1() throws Exception {
        INeedMoreHelpItem item = new INeedMoreHelpItem(CmResourceType.PRACTICE.label(), "", "Test");  
        InmhItemData itemData = new InmhItemData(item);
        String range = "[{PID_1, PID_2, PID_3}-2])";
        List<String> res = itemData.findSolutionsInRandomRange(conn, range);
        assertTrue(res.size() == 2);
    }
    public void testCreateMixedNumbers() throws Exception {
        String file = "topics/mean-median-mode.html";
        INeedMoreHelpItem item = new INeedMoreHelpItem(CmResourceType.PRACTICE.label(), file, "Test");  
        InmhItemData itemData = new InmhItemData(item);
        List<RppWidget> rpps = itemData.getWidgetPool(conn,"testing");
        assertTrue(rpps.size() > 0);
        assertTrue(rpps.get(0).getWidgetJsonArgs() != null);
    }
    
    
    public void testCreateWithDupRpp() throws Exception {
        /** create with known duplicate
         * TODO: generalize
         * 
         */
        INeedMoreHelpItem item = new INeedMoreHelpItem(CmResourceType.PRACTICE.label(), "topics/functions.html", "Test");  
        InmhItemData itemData = new InmhItemData(item);
        List<RppWidget> rpps = itemData.getWidgetPool(conn,"testing");
        assertTrue(!hasDuplicates(rpps));
    }
    
    
    public void testCreateNoFLash() throws Exception {
        /** create with known duplicate
         * TODO: generalize
         * 
         */
        INeedMoreHelpItem item = new INeedMoreHelpItem(CmResourceType.PRACTICE.label(), "topics/functions.html", "Test");  
        InmhItemData itemData = new InmhItemData(item);
        List<RppWidget> rpps = itemData.getWidgetPool(conn,"testing");
        assertTrue(rpps.get(0).getFile() != null);
    }

    private boolean hasDuplicates(List<RppWidget> rpps) {
        List<String> list = new ArrayList<String>();
        for(RppWidget r: rpps) {
            String pid = r.getFile();
            if(list.contains(pid))
                return true;
            list.add(pid);
        }
        return false;
    }
}
