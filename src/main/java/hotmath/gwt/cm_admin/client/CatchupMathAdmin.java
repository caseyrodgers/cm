package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_admin.client.service.RegistrationService;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_admin.client.ui.AccountInfoPanel;
import hotmath.gwt.cm_admin.client.ui.HeaderPanel;
import hotmath.gwt.cm_admin.client.ui.FooterPanel;
import hotmath.gwt.cm_admin.client.ui.StudentDetailsPanel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_admin.client.model.CmAdminModel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMathAdmin implements EntryPoint {

	Viewport        mainPort;
	LayoutContainer mainContainer;
	HeaderPanel     headerPanel;
	FooterPanel     footerPanel;
	StudentGridPanel sgp;
	int              userId;
	CmAdminModel     cmAdminMdl;
	
	static CatchupMathAdmin instance;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		instance = this;
		
        try {
            userId = CmShared.handleLoginProcess();
            UserInfoBase user = UserInfoBase.getInstance();
            if (user != null) {
            	if (! user.isAdmin()) {
            		//TODO: restore Admin check
                	//throw new Exception("Not an admin");
            	}
            }
            else {
            	throw new Exception("Login failed!");
            }
            cmAdminMdl = new CmAdminModel();
            cmAdminMdl.setId(userId);
        }
        catch(Exception e) {
            CatchupMathAdmin.showAlert(e.getMessage(), new CmAsyncRequest()  {
                public void requestComplete(String requestData) {
                    Window.Location.assign("/"); // goto home
                }
                public void requestFailed(int code, String text) {
                }
            });
            return;
        }
		
		mainPort = new Viewport();
		mainPort.setLayout(new BorderLayout());
		mainPort.setEnableScroll(true);
		
		// attempt to improve scrolling...
		mainPort.setStyleAttribute("overflow", "auto");
		mainPort.setStyleAttribute("overflow-x", "auto");
		mainPort.setStyleAttribute("overflow-y", "auto");
		
		registerRpcServices();

		BorderLayoutData bdata = new BorderLayoutData(LayoutRegion.NORTH,40);
		headerPanel = new HeaderPanel();
		mainPort.add(headerPanel, bdata);

		mainContainer = new LayoutContainer();
		mainContainer.setStylePrimaryName("main-container");
		
		sgp = new StudentGridPanel(cmAdminMdl);
		mainContainer.add(sgp);
		mainContainer.setHeight(630);
		mainContainer.setMonitorWindowResize(true);

		bdata = new BorderLayoutData(LayoutRegion.CENTER);
		bdata.setMinSize(600);
		mainPort.add(mainContainer, bdata);
		
		bdata = new BorderLayoutData(LayoutRegion.SOUTH,20);
		FooterPanel footer = new FooterPanel();
		mainPort.add(footer, bdata);
		
		RootPanel.get().add(mainPort);
	}
	
	/**
	 *  Register RPC services
	 */
	private void registerRpcServices() {

		final RegistrationServiceAsync regService = (RegistrationServiceAsync) GWT.create(RegistrationService.class);
		
		String point = GWT.getModuleBaseURL();
		if (!point.endsWith("/")) point += "/";
		point += "services/registrationService";
		
		((ServiceDefTarget) regService).setServiceEntryPoint(point);
		Registry.register("registrationService", regService);
	}
	
	@SuppressWarnings("unchecked")
	public static void showAlert(String msg) {
		MessageBox.alert("Info", msg, new Listener() {
			public void handleEvent(BaseEvent be) {
				// empty
			}
		});
	}

	public static void showAlert(String msg, final CmAsyncRequest callback) {
	    //setBusy(false);
	    MessageBox.alert("Info", msg, new Listener<MessageBoxEvent>() {
	        public void handleEvent(MessageBoxEvent be) {
	            if(callback != null)
	                callback.requestComplete(be.getValue());
	        }
	    });
	}


	public void showMe() {
		;
	}

	public static CatchupMathAdmin getInstance() {
		if (instance == null) instance = new CatchupMathAdmin();
		return instance;
	}
}
