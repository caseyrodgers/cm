package hotmath.gwt.hm_mobile.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class ShowHomeViewEvent extends GwtEvent<ShowHomeViewEventHandler> {

    public static Type<ShowHomeViewEventHandler> TYPE = new Type<ShowHomeViewEventHandler>();

	public ShowHomeViewEvent() {
	}

	@Override
	public Type<ShowHomeViewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowHomeViewEventHandler handler) {
		handler.showHome();
	}
	
}
