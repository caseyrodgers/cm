package hotmath.gwt.cm_mobile_shared.client.event;

import com.google.gwt.event.shared.GwtEvent;


/** The back button was pressed and a page is current loaded in page stack
 * 
 * @author casey
 *
 */
public class DoSearchEvent extends GwtEvent<DoSearchEventHandler> {
    
	public static Type<DoSearchEventHandler> TYPE = new Type<DoSearchEventHandler>();

	String searchFor;
	
	public DoSearchEvent(String searchFor) {
	    this.searchFor = searchFor;
	}

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DoSearchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DoSearchEventHandler handler) {
        handler.doSearch(searchFor);
    }

}
