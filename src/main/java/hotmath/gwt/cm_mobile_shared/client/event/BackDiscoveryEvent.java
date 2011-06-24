package hotmath.gwt.cm_mobile_shared.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class BackDiscoveryEvent extends GwtEvent<BackDiscoveryEventHandler> {
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
