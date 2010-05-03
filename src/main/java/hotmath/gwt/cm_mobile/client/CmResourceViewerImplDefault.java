package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class CmResourceViewerImplDefault implements CmMobileResourceViewer {
    @Override
    public Widget getViewer(InmhItemData item) {
        return new Label("No viewer has been configured for: " + item);
    }
}
