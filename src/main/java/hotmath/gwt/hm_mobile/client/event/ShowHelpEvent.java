package hotmath.gwt.hm_mobile.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class ShowHelpEvent extends GwtEvent<ShowHelpEventHandler> {
    
	public static Type<ShowHelpEventHandler> TYPE = new Type<ShowHelpEventHandler>();

	public ShowHelpEvent() {
	}

	@Override
	public Type<ShowHelpEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowHelpEventHandler handler) {
		handler.showHelp();
	}

	
}
