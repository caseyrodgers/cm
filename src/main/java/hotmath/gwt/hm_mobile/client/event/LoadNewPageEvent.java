package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.event.shared.GwtEvent;

public class LoadNewPageEvent extends GwtEvent<LoadNewPageEventHandler> {
	public static Type<LoadNewPageEventHandler> TYPE = new Type<LoadNewPageEventHandler>();

	IPage page;
	public LoadNewPageEvent(IPage page) {
	    this.page = page;
	}

	@Override
	public Type<LoadNewPageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadNewPageEventHandler handler) {
		handler.loadPage(page);
	}

    public IPage getPage() {
        return page;
    }

    public void setPage(IPage page) {
        this.page = page;
    }
}
