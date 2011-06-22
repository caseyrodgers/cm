package hotmath.gwt.hm_mobile.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowCategoryListEvent extends GwtEvent<ShowCategoryListEventHandler> {
	public static Type<ShowCategoryListEventHandler> TYPE = new Type<ShowCategoryListEventHandler>();

	public ShowCategoryListEvent() {
	}

	@Override
	public Type<ShowCategoryListEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowCategoryListEventHandler handler) {
		handler.showCategoryList();
	}
}
