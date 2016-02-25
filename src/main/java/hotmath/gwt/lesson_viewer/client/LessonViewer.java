package hotmath.gwt.lesson_viewer.client;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;


/** View lesson text as shown in CM
 * 
 *   process view MathMlTransform
 * 
 *     
 * @author caseyrodgers
 *
 */
   
  public class LessonViewer implements EntryPoint {

    public void onModuleLoad() {
        
        UserInfo.setInstance(new UserInfo());
        
        String lesson = CmCore.getQueryParameter("file");
        if(lesson == null) {
        	Window.alert("'file' must be specified");
        }
     
        RootPanel.get("review-div").add(new LessonViewerPanelSimple(lesson));

       
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
          });
    }
    
    
    static CmServiceAsync _serviceInstance;
    static public CmServiceAsync getCmService() {
        return _serviceInstance;
    }

    static private void setupServices() {

        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";
        
        point = "/";
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        _serviceInstance = cmService;
    }
    static {
        setupServices();
    }
}
