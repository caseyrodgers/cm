package hotmath.gwt.assignment_print.client;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;



  public class AssignmentPrint implements EntryPoint {

    public AssignmentPrint() {
    }
    
    SimplePanel _mainPanel;
    
    public void onModuleLoad() {
        
        UserInfo.setInstance(new UserInfo());
        
        int aid=0;
        try {
        	aid=Integer.parseInt(CmGwtUtils.getQueryParameter("aid"));
        }
        catch(Exception e) {
        	Window.alert("You need to specify the assignment id (aid=X)");
        	return;
        }
        		
        _mainPanel = new SimplePanel();
        _mainPanel.setStyleName("assignment_report");
        
        final AssignmentReport assRep = new AssignmentReport(aid);
        RootPanel p = RootPanel.get("gwt_hook");
        p.getElement().setInnerHTML("");
        p.add(_mainPanel);
        assRep.buildReport(new CallbackOnComplete() {
			public void isComplete() {
				System.out.println("Report created");
			}
		}, _mainPanel);
    }
    
    
    static CmServiceAsync _serviceInstance;
    static public CmServiceAsync getCmService() {
        return _serviceInstance;
    }

    static private void setupServices() {
    	// GWT.getModuleBaseURL();
    	// use cm_admin server 
    	// (TODO: why not a single access point?
    	//
        String point = "/cm_admin";               
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
