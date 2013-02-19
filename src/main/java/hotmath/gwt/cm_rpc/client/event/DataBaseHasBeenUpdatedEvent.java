package hotmath.gwt.cm_rpc.client.event;

import com.google.gwt.event.shared.GwtEvent;

/** Event fired when the database has been updated.
 * 
 * 
 * @author casey
 *
 */
public class DataBaseHasBeenUpdatedEvent extends GwtEvent<DataBaseHasBeenUpdatedHandler> {

        public static Type<DataBaseHasBeenUpdatedHandler> TYPE = new Type<DataBaseHasBeenUpdatedHandler>();

          @Override
        public Type<DataBaseHasBeenUpdatedHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(DataBaseHasBeenUpdatedHandler handler) {
            handler.databaseUpdated();
        }
}
