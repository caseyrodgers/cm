package hotmath.gwt.cm_mobile3.client.event;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.event.shared.GwtEvent;

public class ShowPrescriptionResourceEvent extends GwtEvent<ShowPrescriptionResourceHandler>{

    public static Type<ShowPrescriptionResourceHandler> TYPE = new Type<ShowPrescriptionResourceHandler>();

    InmhItemData resourceItem;
    
    public ShowPrescriptionResourceEvent(InmhItemData resourceItem) {
        this.resourceItem = resourceItem;
    }
    
    @Override
    public Type<ShowPrescriptionResourceHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowPrescriptionResourceHandler handler) {
        handler.showResource(resourceItem);
    }
}
