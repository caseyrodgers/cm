package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.event.MathJaxRenderNeededEvent;
import hotmath.gwt.cm_rpc.client.event.MathJaxRenderNeededHandler;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;


public class CmRpc implements EntryPoint {
    public void onModuleLoad() {
        /** silent */
    }
    
    static native public void jsni_processMathJax() /*-{
        $wnd.processMathJax();
    }-*/;
    
    
    
    
    static {
        /** Setup a couple of global event handlers 
         * 
         */
        CmRpcCore.EVENT_BUS.addHandler(MathJaxRenderNeededEvent.TYPE, new MathJaxRenderNeededHandler() {
            @Override
            public void renderMathJax() {
                Log.info("Rendering MathJAX");
                jsni_processMathJax();
            }
        });
    }
    
    
    
    
}