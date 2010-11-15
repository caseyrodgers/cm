package hotmath.gwt.cm_activity.client;

import hotmath.gwt.cm_mobile_shared.client.CmMobileResourceViewer;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class CmResourceViewerImplActivity extends Composite implements CmMobileResourceViewer {
   
    public CmResourceViewerImplActivity() {
        try {
            initWidget(new WordProblemsPanel());
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Widget getViewer(final InmhItemData item) {
        return this;
    }
}
