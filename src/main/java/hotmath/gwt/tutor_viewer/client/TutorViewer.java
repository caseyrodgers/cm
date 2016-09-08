package hotmath.gwt.tutor_viewer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.tutor_viewer.client.ui.GenerateTutorContextPanel;
import hotmath.gwt.tutor_viewer.client.ui.ShowTutorContextPanel;
import hotmath.gwt.tutor_viewer.client.ui.TutorViewerPanelSimple;
import hotmath.gwt.tutor_viewer.client.ui.ValidateTutorContextPanel;



/** Standardized Tutor viewer 
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
        
        if(CmCore.getQueryParameter("generate_context_all") != null) {
            String hm = CmCore.getQueryParameter("how_many");
            if(hm == null) {
                hm = "100";
            }
            int howMany = Integer.parseInt(hm);
            
            RootPanel.get().add(new GenerateTutorContextsAll(howMany).createContexts(null));
        }
        else {
            String pid = CmCore.getQueryParameter("pid");
            if(pid == null) {
                Window.alert("pid parameter must be specified on the URL");
            }
            
            if(CmCore.getQueryParameter("show_context") != null) {
                RootPanel.get().add(new ShowTutorContextPanel().showAllContexts(pid, false));
            }
            else if(CmCore.getQueryParameter("show_context_details") != null) {
                RootPanel.get().add(new ShowTutorContextPanel().showAllContexts(pid, true));
            }
            else if(CmCore.getQueryParameter("generate_context") != null) {
                RootPanel.get().add(new GenerateTutorContextPanel().createContexts(pid));
            }
            else if(CmCore.getQueryParameter("validate_context") != null) {
                RootPanel.get().add(new ValidateTutorContextPanel().validateContexts(pid));
            }
            else {
                RootPanel.get().add(new TutorViewerPanelSimple(pid));
            }
        }
        
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
        
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        _serviceInstance = cmService;
    }
    static {
        setupServices();
    }
}
