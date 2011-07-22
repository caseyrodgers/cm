package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.cm_mobile_shared.client.event.ResetDisplayEventHandler;

import com.google.gwt.event.shared.GwtEvent;

public class ResetDisplayEvent extends GwtEvent<ResetDisplayEventHandler> {
	public static Type<ResetDisplayEventHandler> TYPE = new Type<ResetDisplayEventHandler>();

	public ResetDisplayEvent() {
	}

	@Override
	public Type<ResetDisplayEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ResetDisplayEventHandler handler) {
		handler.resetView();
	}
}
