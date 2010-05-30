package hotmath.assessment;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.inmh.INeedMoreHelpItem;

import java.util.ArrayList;
import java.util.List;

public class InmhItemData_Test extends CmDbTestCase {
    
    public InmhItemData_Test(String name) {
        super(name);
    }
    
    public void testCreateWithDupRpp() throws Exception {
        /** create with known duplicate
         * TODO: generalize
         * 
         */
        INeedMoreHelpItem item = new INeedMoreHelpItem("practice", "topics/functions.html", "Test");  
        InmhItemData itemData = new InmhItemData(item);
        List<RppWidget> rpps = itemData.getWookBookSolutionPool(conn);
        assertTrue(!hasDuplicates(rpps));
    }

    private boolean hasDuplicates(List<RppWidget> rpps) {
        List<String> list = new ArrayList<String>();
        for(RppWidget r: rpps) {
            String pid = r.getPid().getGUID();
            if(list.contains(pid))
                return true;
            list.add(pid);
        }
        return false;
    }
}
