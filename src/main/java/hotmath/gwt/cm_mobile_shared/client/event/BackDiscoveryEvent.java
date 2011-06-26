package hotmath.gwt.cm_mobile_shared.client.event;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.event.shared.GwtEvent;


public class BackDiscoveryEvent extends GwtEvent<BackDiscoveryEventHandler> {
	public static Type<BackDiscoveryEventHandler> TYPE = new Type<BackDiscoveryEventHandler>();

	IPage page;
	public BackDiscoveryEvent(IPage page) {
		this.page = page;
		
	}

	@Override
	public Type<BackDiscoveryEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BackDiscoveryEventHandler handler) {
		handler.discoverBack(page);
	}

	public IPage getPage() {
		return page;
	}

	public void setPage(IPage page) {
		this.page = page;
	}
}
