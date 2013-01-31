package hotmath.gwt.tutor_viewer.client;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContextPanel;
import hotmath.gwt.tutor_viewer.client.ui.ShowTutorContextPanel;
import hotmath.gwt.tutor_viewer.client.ui.TutorViewerPanelSimple;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;



/** Standarized Tutor viewer 
 * 
 *  Loads JS directly, not using compressed JS to allow debugging.
 *  
 *  Provides three features, controlled via a parameter:
 *  
 *     1. view (no param):   load a single pid
 *     2. generate_context:   generate the solution context for a single pid
 *     3. generate_context_all: generate solution context for ALL pids match wild.
 *  
 */     
   
  public class TutorViewer implements EntryPoint {

    public TutorViewer() {
    }
    
    public void onModuleLoad() {
        UserInfo.setInstance(new UserInfo());
        
        if(CmGwtUtils.getQueryParameter("generate_context_all") != null) {
            String pid = CmGwtUtils.getQueryParameter("pid");  // can contain wild
            if(pid==null) {
                Window.alert("'pid' parameter must be specified");
            }
            else {
                RootPanel.get().add(new GenerateTutorContextsAll().createContexts(pid));
            }
        }
        else {
            String pid = CmGwtUtils.getQueryParameter("pid");
            if(pid == null) {
                Window.alert("pid parameter must be specified on the URL");
            }
            
            if(CmGwtUtils.getQueryParameter("show_context") != null) {
                RootPanel.get().add(new ShowTutorContextPanel().showAllContexts(pid));
            }
            else if(CmGwtUtils.getQueryParameter("generate_context") != null) {
                RootPanel.get().add(new GenerateTutorContextPanel().createContexts(pid));
            }
            else {
                RootPanel.get().add(new TutorViewerPanelSimple(pid));
            }
            
            
        }
        
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                Log.debug("Window has been resized");
                CmRpc.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
          });
    }
    
    
    static CmServiceAsync _serviceInstance;
    static public CmServiceAsync getCmService() {
        return _serviceInstance;
    }

    static private void setupServices() {
//        String point = GWT.getModuleBaseURL();
//        if (!point.endsWith("/"))
//            point += "/";
        
        String point = "/";
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        _serviceInstance = cmService;
        
    }
    
    static {
        setupServices();
    }
}
