package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.cm_mobile_shared.client.event.EnableDisplayZoomEventHandler;

import com.google.gwt.event.shared.GwtEvent;

public class EnableDisplayZoomEvent extends GwtEvent<EnableDisplayZoomEventHandler> {
	
	boolean enable;
	
	public static Type<EnableDisplayZoomEventHandler> TYPE = new Type<EnableDisplayZoomEventHandler>();

	public EnableDisplayZoomEvent(boolean enable) {
		this.enable = enable;
	}

	@Override
	public Type<EnableDisplayZoomEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EnableDisplayZoomEventHandler handler) {
		handler.enableZoom(enable);
	}
}
