package hotmath.gwt.hm_mobile.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GoBackEvent extends GwtEvent<GoBackEventHandler> {
	public static Type<GoBackEventHandler> TYPE = new Type<GoBackEventHandler>();

	public GoBackEvent() {
	}

	@Override
	public Type<GoBackEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GoBackEventHandler handler) {
		handler.goBack();
	}
}
