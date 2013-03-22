package hotmath.gwt.cm_rpc.client.event;

import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when the database has been updated.
 * 
 * Used generically to indicate any static state has been updated and components
 * should reconfigure themselves.
 * 
 * 
 * @author casey
 * 
 */
public class DataBaseHasBeenUpdatedEvent extends GwtEvent<DataBaseHasBeenUpdatedHandler> {

    TypeOfUpdate typeOfUpdate;

    public static Type<DataBaseHasBeenUpdatedHandler> TYPE = new Type<DataBaseHasBeenUpdatedHandler>();
    
    public DataBaseHasBeenUpdatedEvent(TypeOfUpdate typeOfUpdate) {
        this.typeOfUpdate = typeOfUpdate;
    }

    @Override
    public Type<DataBaseHasBeenUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DataBaseHasBeenUpdatedHandler handler) {
        handler.databaseUpdated(typeOfUpdate);
    }
}
