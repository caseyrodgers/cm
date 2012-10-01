package hotmath.gwt.cm_tutor.client;



import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tutor.client.view.TutorWithWhiteboard2;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

public class CmTutor implements EntryPoint {

    public CmTutor() {
    }
    
    public void onModuleLoad() {
        if(RootPanel.get("cm_whiteboard-main") != null) {
            UserInfo.setInstance(new UserInfo());
            TutorWithWhiteboard2 tutorWhiteboard = new TutorWithWhiteboard2();
            RootPanel.get("cm_whiteboard-main").add(tutorWhiteboard);
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
        
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        _serviceInstance = cmService;
        
    }
    
    static {
        setupServices();
    }
}
