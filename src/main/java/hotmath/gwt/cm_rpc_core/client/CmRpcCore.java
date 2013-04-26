package hotmath.gwt.cm_rpc_core.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;


public class CmRpcCore implements EntryPoint {

    
    /** Define globally accessible single application event bus
     * 
     */
    public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
    

    public void onModuleLoad() {
        /** silent */
    }
}