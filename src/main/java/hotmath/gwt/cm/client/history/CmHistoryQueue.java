package hotmath.gwt.cm.client.history;


import java.util.LinkedList;
import java.util.Queue;

/** Manages a queue of CmLocation objects
 *  that represent pending 'history changes'.
 *  
 *  This allows the system to be initialized asynchronously. 
 *  
 *  The synchronized queue can be checked for pending changes.  
 *  
 * @author casey
 *
 */
public class CmHistoryQueue {

    static private CmHistoryQueue __instance;
    static public CmHistoryQueue getInstance() {
        if(__instance == null)
            __instance = new CmHistoryQueue();
        return __instance;
    }
    
    private CmHistoryQueue() {}
    
    Integer countLocations=0;
    CmLocation lastLocation;
    
    Queue<CmLocation> locations = new LinkedList<CmLocation>();
    public void pushLocation(CmLocation location) {
        locations.clear(); // remove any previous
        locations.add(location);
        
        countLocations++;
        lastLocation = location;
    }
    
    
    public CmLocation popLocation() {
        if(locations.size() > 0)
            return locations.remove();
        else 
            return null;
    }
    

    /** Return true if initializing to a non-standard
     *  CmLocation, such as a bookmarked resource.  This.
     *  will be used to determine if initialzing messages 
     *  should be shown to user.  If has been initialized
     *  from bookmarked viewer, we do not want to have Z-Order
     *  issue with Flash resources.
     *  
     *   
     * @return
     */
    public Boolean isInitializingToNonStandard() {
        return countLocations < 2 && lastLocation.getLocationType() == CmLocation.LocationType.PRESCRIPTION;
    }

}
