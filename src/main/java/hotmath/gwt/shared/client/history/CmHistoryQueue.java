package hotmath.gwt.shared.client.history;

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
    
    Queue<CmLocation> locations = new LinkedList<CmLocation>();
    public void pushLocation(CmLocation location) {
        locations.add(location);
    }
    
    
    public CmLocation popLocation() {
        if(locations.size() > 0)
            return locations.remove();
        else 
            return null;
    }

}
