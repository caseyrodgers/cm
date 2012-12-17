package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.event.MathJaxRenderNeededEvent;
import hotmath.gwt.cm_rpc.client.event.MathJaxRenderNeededHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;


public class CmRpc implements EntryPoint {
    
    /** Define globally accessible event bus
     * 
     */
    public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);
    
    public void onModuleLoad() {
        /** silent */
    }
    
    static {
        /** Setup a couple of global event handlers 
         * 
         */
        EVENT_BUS.addHandler(MathJaxRenderNeededEvent.TYPE, new MathJaxRenderNeededHandler() {
            @Override
            public void renderMathJax() {
                Log.info("Rendering MathJAX");
                jsni_processMathJax();
            }
        });
    }
    
    
    static native private void jsni_processMathJax() /*-{
        $wnd.processMathJax();
    }-*/;
}