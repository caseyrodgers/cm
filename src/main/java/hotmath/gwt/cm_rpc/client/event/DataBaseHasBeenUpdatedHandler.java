package hotmath.gwt.cm_rpc.client.event;

import com.google.gwt.event.shared.EventHandler;

/** Allows listeners to be informed when base database
 * tables have been updated.  This allows for UI tables/grids
 * to refresh their view of the db.
 * 
 * @author casey
 *
 */
public interface DataBaseHasBeenUpdatedHandler extends EventHandler {
    
    public enum TypeOfUpdate{ANNOTATION,FULL, ASSIGNMENTS, SOLUTION};
    
    /** 'some' table has been updated.
     * 
     * TODO: What is 'some' table .. an enumeration?
     * 
     */
    void databaseUpdated(TypeOfUpdate type);
}
