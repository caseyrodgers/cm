package hotmath.gwt.cm_mobile3.client.event;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.event.shared.EventHandler;

public interface ShowPrescriptionResourceHandler extends EventHandler {
    void showResource(InmhItemData resourceItem);
}
