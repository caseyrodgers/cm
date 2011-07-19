package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.cm_mobile_shared.client.event.ResetListSelectionsEventHandler;

import com.google.gwt.event.shared.GwtEvent;

public class ResetListSelectionsEvent extends GwtEvent<ResetListSelectionsEventHandler> {
	public static Type<ResetListSelectionsEventHandler> TYPE = new Type<ResetListSelectionsEventHandler>();

	public ResetListSelectionsEvent() {
	}

	@Override
	public Type<ResetListSelectionsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ResetListSelectionsEventHandler handler) {
		handler.resetSelections();
	}
}
