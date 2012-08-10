package hotmath.gwt.shared.client.util;

public class CmNotifyManager {
    
    private static CmNotifyManager __instance;
    public static CmNotifyManager getInstance() {
        if(__instance == null) {
            __instance = new CmNotifyManager();
        }
        return __instance;
    }
    
    
    public void notify(String msg) {
        
    }
    

}
