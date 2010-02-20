package hotmath.gwt.cm_tools.client.model;


/** Defines class that needs to be told to update
 * 
 * This generates a recurrent call to allow it refresh itself
 * 
 * @author casey
 *
 */
public interface CmAdminDataRefresher {
    void refreshData();
}
