package hotmath.gwt.cm_mobile_shared.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class ShowWorkSubmittedEvent extends GwtEvent<ShowWorkSubmittedHandler> {
    
	public static Type<ShowWorkSubmittedHandler> TYPE = new Type<ShowWorkSubmittedHandler>();

	public ShowWorkSubmittedEvent() {}

	@Override
	public Type<ShowWorkSubmittedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowWorkSubmittedHandler handler) {
		handler.showWorkSubmitted();
	}
	
}
