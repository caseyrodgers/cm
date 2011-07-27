package hotmath.gwt.cm_mobile_shared.client.event;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.event.shared.GwtEvent;


/** The back button was pressed and a page is current loaded in page stack
 * 
 * @author casey
 *
 */
public class BackPageLoadedEvent extends GwtEvent<BackPageLoadedEventHandler> {
	public static Type<BackPageLoadedEventHandler> TYPE = new Type<BackPageLoadedEventHandler>();

	IPage page;
	public BackPageLoadedEvent(IPage page) {
		this.page = page;
		
	}

	@Override
	public Type<BackPageLoadedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BackPageLoadedEventHandler handler) {
		handler.movedBack(page);
	}

	public IPage getPage() {
		return page;
	}

	public void setPage(IPage page) {
		this.page = page;
	}
}
