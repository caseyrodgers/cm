package hotmath.gwt.hm_mobile.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class ShowBookSearchEvent extends GwtEvent<ShowBookSearchEventHandler> {
    
	public static Type<ShowBookSearchEventHandler> TYPE = new Type<ShowBookSearchEventHandler>();

	public ShowBookSearchEvent() {
	}

	@Override
	public Type<ShowBookSearchEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowBookSearchEventHandler handler) {
		handler.showBookSearch();
	}

	
}
