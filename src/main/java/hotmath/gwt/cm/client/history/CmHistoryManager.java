package hotmath.gwt.cm.client.history;

import hotmath.gwt.cm_rpc.client.UserInfo;

import com.google.gwt.user.client.History;



/** Manages the interface between the GUI and the GWT History api
 * 
 * @author casey
 *
 */
public class CmHistoryManager {
    
    static private CmHistoryManager __instance;
    static public CmHistoryManager getInstance() {
        if(__instance == null)
            __instance = new CmHistoryManager();
        return __instance;
    }
    
    
    private CmHistoryManager(){}
    
    
    /** Cause a new history change to occur, this will
     *  invoke the global changeListener.
     *  
     *  Adds a unique key to URL to force a refresh
     *  
     * @param location
     */
    public void addHistoryLocation(CmLocation location) {
        History.newItem(location.toString() + "" + ":" + (System.currentTimeMillis()));
    }
    
    
    
    
    
    /** Make a GWT History request to load resource which will
     * call our central HistoryListener (CmHistoryListener).  
     * 
     * The history listener will analyze the CmLocation and 
     * load the appropriate forms/data.
     * 
     * resourceNumber can be an ordinal position or the item path
     * 
     * Provide central location to create CmLocation object identifying
     * resource to load.
     * 
     * @param rm
     */
    static public void loadResourceIntoHistory(String type, String resourceId) {
        
        String locationStr = "p:" + UserInfo.getInstance().getSessionNumber() + ":" +  type + ":" +  resourceId;
        CmLocation location = new CmLocation(locationStr);
        CmHistoryManager.getInstance().addHistoryLocation(location);
    }    
    
}
