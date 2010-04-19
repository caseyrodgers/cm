package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/** What is the minimum size of CM 
 * 
 * @author casey
 *
 */
public class CatchupMathMobile implements EntryPoint {
    Label _name = new Label();
    Panel panel = new SimplePanel();
    
    public void onModuleLoad() {
    	setupServices();
        panel.add(_name);
        
        GetCmMobileLoginAction action = new GetCmMobileLoginAction();
        getCmService().execute(action, new AsyncCallback<CmMobileUser>() {
        	@Override
        	public void onSuccess(CmMobileUser result) {
        		_name.setText("Login successful: " + result);
        	}
        	@Override
        	public void onFailure(Throwable caught) {
        		_name.setText("Error logging in: " + caught);
        	}
		});
        RootPanel.get().add(panel);
    }
    
    
    static CmServiceAsync getCmService() {
    	return _cmService;
    }
    static CmServiceAsync _cmService;
    static private void setupServices() {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";
        
        _cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) _cmService).setServiceEntryPoint(point + "services/cmService");
    }
}
