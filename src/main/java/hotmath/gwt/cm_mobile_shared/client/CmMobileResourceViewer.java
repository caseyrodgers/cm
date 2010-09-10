package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.user.client.ui.Widget;

public interface CmMobileResourceViewer {
    Widget getViewer(InmhItemData item);
}
