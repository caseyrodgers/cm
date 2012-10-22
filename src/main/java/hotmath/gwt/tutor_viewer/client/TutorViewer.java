package hotmath.gwt.tutor_viewer.client;




import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.tutor_viewer.client.ui.TutorViewerPanelSimple;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

public class TutorViewer implements EntryPoint {

    public TutorViewer() {
    }
    
    public void onModuleLoad() {
        UserInfo.setInstance(new UserInfo());
        
        String pid = CmGwtUtils.getQueryParameter("pid");
        if(pid == null) {
            Window.alert("pid parameter must be specified on the URL");
        }
        RootPanel.get().add(new TutorViewerPanelSimple(pid));
        
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