package hotmath.gwt.cm_mobile_shared.client.event;

import hotmath.gwt.hm_mobile.client.model.CategoryModel;

import com.google.gwt.event.shared.GwtEvent;


public class BackDiscoveryEvent extends GwtEvent<BackDiscoveryEventHandler> {
    CategoryModel category;
    
	public static Type<BackDiscoveryEventHandler> TYPE = new Type<BackDiscoveryEventHandler>();

	public BackDiscoveryEvent() {
	}

	@Override
	public Type<BackDiscoveryEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BackDiscoveryEventHandler handler) {
		handler.discoverBack();
	}
}
