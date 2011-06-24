package hotmath.gwt.hm_mobile.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SystemIsBusyEvent extends GwtEvent<SystemIsBusyEventHandler> {
	
	boolean trueFalse;
	
	public SystemIsBusyEvent(boolean trueFalse) {
		this.trueFalse = trueFalse;
	}
	
	public static Type<SystemIsBusyEventHandler> TYPE = new Type<SystemIsBusyEventHandler>();

	@Override
	protected void dispatch(SystemIsBusyEventHandler h) {
		h.showIsBusy(trueFalse);
	}

	@Override
	public Type<SystemIsBusyEventHandler> getAssociatedType() {
		return TYPE;
	}
}
