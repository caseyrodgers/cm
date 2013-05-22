package hotmath.gwt.cm_mobile_shared.client.event;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.event.shared.GwtEvent;

/** Fired when a new page should be loaded into the 
 *  IPage GUI stack.
 *  
 * @author casey
 *
 */
public class NewPageLoadedEvent extends GwtEvent<NewPageLoadedHandler> {
	public static Type<NewPageLoadedHandler> TYPE = new Type<NewPageLoadedHandler>();

	IPage page;
	public NewPageLoadedEvent(IPage page) {
	    this.page = page;
	}

	@Override
	public Type<NewPageLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NewPageLoadedHandler handler) {
		handler.pageLoaded(page);
	}

    public IPage getPage() {
        return page;
    }

    public void setPage(IPage page) {
        this.page = page;
    }
}
