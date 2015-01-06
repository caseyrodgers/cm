package hotmath.gwt.cm_tutor.client;




import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tutor.client.view.TutorWithWhiteboardViewer;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

public class CmTutor implements EntryPoint {

    public CmTutor() {
    }
    
    public void onModuleLoad() {
        if(RootPanel.get("cm_whiteboard-main") != null) {
            UserInfo.setInstance(new UserInfo());
            
            String pid = CmCore.getQueryParameter("pid");
            TutorWithWhiteboardViewer tutorWhiteboard = new TutorWithWhiteboardViewer(pid);
            RootPanel.get("cm_whiteboard-main").add(tutorWhiteboard);
            
            
            
            Window.addResizeHandler(new ResizeHandler() {
                @Override
                public void onResize(ResizeEvent event) {
                    Log.debug("Window has been resized");
                    CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
                }
              });
        }
    }
    
    
    static CmServiceAsync _serviceInstance;
    static public CmServiceAsync getCmService() {
        return _serviceInstance;
    }

    static private void setupServices() {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";
        
        point = "/cm/";
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        _serviceInstance = cmService;
        
    }
    
    static {
        setupServices();
    }
}
