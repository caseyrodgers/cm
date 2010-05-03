package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

public class CmMobileResourceViewerFactory {
    static public CmMobileResourceViewer createViewer(InmhItemData item) {
        if("review".equals(item.getType())) {
            return new CmResourceViewerImplLesson();
        }
        else if("practice".equals(item.getType())
                || "cmextra".equals(item.getType())) {
            return new CmResourceViewerImplSolution();
        }
        else 
            return new CmResourceViewerImplDefault();
    }
}
