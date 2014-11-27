package hotmath.gwt.cm.client;

import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplActivity;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview;
import hotmath.gwt.shared.client.CmShared;


/** Run test identified by test URL param 
 * 
 * @return
 */
public class CatchupMathTests {
    public static boolean runTest() {
        String test = CmShared.getQueryParameterValue("test");
        if(test.equals("ResourceViewerImplReview")) {
            ResourceViewerImplReview.startTest();
        }
        else if(test.equals("ResourceViewerImplActivity")) {
            ResourceViewerImplActivity.startTest();
        }
        else {
            return false;
        }
        return true;
    }

}

