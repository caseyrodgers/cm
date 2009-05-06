package hotmath.gwt.cm.client;

import hotmath.gwt.cm.client.data.CmAsyncRequest;
import hotmath.gwt.cm.client.service.PrescriptionService;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.ui.CmMainPanel;
import hotmath.gwt.cm.client.ui.FooterPanel;
import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm.client.ui.context.LoginCmGuiDefinition;
import hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition;
import hotmath.gwt.cm.client.ui.context.QuizCmGuiDefinition;
import hotmath.gwt.cm.client.util.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.startup.SetAllPropertiesRule;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMath implements EntryPoint {

    /**
     * Return last create instance
     * 
     * @return
     */
    public static CatchupMath getThisInstance() {
        return __thisInstance;
    }

    Viewport _mainPort;
    LayoutContainer _prescriptionPort;

    static CatchupMath __thisInstance;

    LayoutContainer _mainContainer;
    HeaderPanel _headerPanel;

    CmProgramInfo _cmProgramInfo = new CmProgramInfo();

    Map<String,String> parameters = new HashMap<String,String>();
    
    /**
     * This is the entry point method.
     */
    int userId;
    public void onModuleLoad() {
        __thisInstance = this;
        GXT.setDefaultTheme(Theme.GRAY, true);

        setupServices();

        _mainPort = new Viewport();
        // _mainPort.setStyleName("main-port");

        _mainPort.setLayout(new BorderLayout());
        BorderLayoutData bdata = new BorderLayoutData(LayoutRegion.NORTH, 38);
        _headerPanel = new HeaderPanel();
        _mainPort.add(_headerPanel, bdata);

        _mainContainer = new LayoutContainer();
        _mainContainer.setStyleName("main-container");
        _mainContainer.getElement().setId("main-container");

        bdata = new BorderLayoutData(LayoutRegion.CENTER);
        _mainPort.add(_mainContainer, bdata);

        bdata = new BorderLayoutData(LayoutRegion.SOUTH, 20);
        FooterPanel footer = new FooterPanel();
        _mainPort.add(footer, bdata);

        parameters = readQueryString();
        
        // first see if run_id is passed, if so
        // the user is in 'view' mode and we must
        // inform the server not to update the 
        // current state as the user moves around system.
        int runId=0;
        int testId=0;
        String key2="";
        try {
            String cmKey = Cookies.getCookie("cm_key");
            if(cmKey == null) {
                throw new Exception("No login cookie found");   
            }
            cmKey = cmKey.substring(1,cmKey.length()-1);
            
            JSONValue jsonValue = JSONParser.parse(cmKey);
            JSONObject o = jsonValue.isObject();
            String keyVal = o.get("key").isString().stringValue();
            if(keyVal == null) {
                throw new Exception("Invalid security key found in cookie");
            }
            if(parameters.get("key") != null) {
                key2 = parameters.get("key");
            }
            if(key2 == null) {
                throw new Exception("No key parameter found");
            }
            if(!key2.equals(keyVal)) {
                throw new Exception("Security exception: key values do not match");
            }
            
            
            // we are valid ...
            // 
            userId = (int)o.get("uid").isNumber().doubleValue();
            if(userId == 0) {
                throw new Exception("'uid' is not valid");
            }
            
            if(parameters.get("run_id") != null) {
                runId = Integer.parseInt(parameters.get("run_id"));
                // setup user to mascarade as real user
                UserInfo user = new UserInfo(0,0);
                user.setRunId(runId);
                user.setSessionNumber(0);
                UserInfo.setInstance(user);
            }
        }
        catch(Exception e) {
            CatchupMath.showAlert(e.getMessage());
            return;
        }
        
        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                String historyToken = event.getValue();
                
                if(UserInfo.getInstance() != null) {
                    
                    if(UserInfo.getInstance().getRunId() > 0) {
                        showPrescriptionPanel_gwt();
                    }
                    else {
                        showQuizPanel_gwt();
                    }
                    return;
                }
                
                UserInfo.loadUser(userId,new CmAsyncRequest() {
                    public void requestComplete(String requestData) {
                        
                        if(UserInfo.getInstance().getRunId() > 0) {
                            // load the existing run
                            showPrescriptionPanel_gwt();
                        }
                        else {
                            // show the quiz and prepare for a new run
                            showQuizPanel_gwt();
                        }
                    }
                    public void requestFailed(int code, String text) {
                        showAlert("There was a problem logging in ");
                    }
                });
            }
        });
        RootPanel.get().add(_mainPort);

        History.fireCurrentHistoryState();
    }
    
    
    /** Convert string+list to string+string
     * 
     */
    private Map<String,String> readQueryString() {
        Map<String,String> m = new HashMap<String,String>();
        Map<String,List<String>> query = Window.Location.getParameterMap();
        for(String s: query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        return m;
    }

    
    private native String jsDecode(String s) /*-{
        return decodeURIComponent(s);
    }-*/;

    /**
     * Return the information about the current CM Program
     * 
     * @return
     */
    public CmProgramInfo getProgramInfo() {
        return _cmProgramInfo;
    }

    /**
     * Register any RPC services with the system
     * 
     */
    private void setupServices() {

        final PrescriptionServiceAsync prescriptionService = (PrescriptionServiceAsync) GWT
                .create(PrescriptionService.class);

        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))            point += "/";
        point += "services/prescriptionService";

        ((ServiceDefTarget) prescriptionService).setServiceEntryPoint(point);
        Registry.register("prescriptionService", prescriptionService);
    }

    /**
     * Display or hide the modal busy dialog
     * 
     * @param trueFalse
     */
    static public void setBusy(boolean trueFalse) {
        RootPanel.get("loading").setVisible(trueFalse);
    }

    /**
     * Display standard message dialog
     * 
     * @param msg
     */
    static public void showAlert(String msg) {
        setBusy(false);
        MessageBox.alert("Info", msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
            }
        });
    }

    /**
     * Helper page to create the Login page
     * 
     * @TODO: get out of main
     * 
     */
    public void showLoginPage() {
        History.newItem("login");
    }

    private void showLoginPage_gwt() {
        HeaderPanel.__instance.disable();

        _mainContainer.removeAll();
        _mainContainer.setLayout(new FitLayout());
        _mainContainer.add(new LoginCmGuiDefinition().getCenterWidget());
        _mainContainer.layout();
    }

    /**
     * Helper page to create the Quiz page
     * 
     * @TODO: get out of main
     * 
     */
    public void showQuizPanel() {
        // History.newItem("quiz");
        showQuizPanel_gwt();
    }

    private void showQuizPanel_gwt() {
        HeaderPanel.__instance.enable();

        _mainContainer.removeAll();
        _mainContainer.setLayout(new FitLayout());
        _mainContainer.add(new CmMainPanel(new QuizCmGuiDefinition()));
        _mainContainer.layout();
    }

    /**
     * Helper page to create the Prescription page
     * 
     * @TODO: get out of main
     * 
     */
    public void showPrescriptionPanel() {
        // History.newItem("pres");
        showPrescriptionPanel_gwt();
    }

    private void showPrescriptionPanel_gwt() {
        HeaderPanel.__instance.enable();

        _mainContainer.removeAll();
        _mainContainer.setLayout(new FitLayout());
        _mainContainer.add(new CmMainPanel(new PrescriptionCmGuiDefinition()));
        _mainContainer.layout();
    }
}
