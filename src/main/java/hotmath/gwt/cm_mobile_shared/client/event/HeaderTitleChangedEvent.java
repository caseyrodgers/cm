package hotmath.gwt.cm_mobile_shared.client.event;

import com.google.gwt.event.shared.GwtEvent;


/** Called when the header title should be updated due
 *  to a loaded data item, such as a new solution.
 *  
 * @author casey
 *
 */
public class HeaderTitleChangedEvent extends GwtEvent<HeaderTitleChangedHandler> {

    public static Type<HeaderTitleChangedHandler> TYPE = new Type<HeaderTitleChangedHandler>();
    private String headerTitle;
    
    public HeaderTitleChangedEvent(String headerTitle) {
        this.headerTitle = headerTitle;
    }
    
    @Override
    public Type<HeaderTitleChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HeaderTitleChangedHandler handler) {
        handler.headerTitleChanged(headerTitle);
    }


}
