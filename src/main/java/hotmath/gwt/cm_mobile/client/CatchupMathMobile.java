package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Provide minimal CM for mobile access.
 * 
 * @author casey
 * 
 */
public class CatchupMathMobile implements EntryPoint {
    
    public static CatchupMathMobile __instance;
    
    CmMobileUser user;
    SimplePanel mainPanel;
    
    public CatchupMathMobile() {
        __instance = this;
    }
    
    public void onModuleLoad() {
        
        mainPanel = new SimplePanel();
        mainPanel.setWidget(new Label("Loading ..."));
        RootPanel.get("main-content").add(mainPanel);
        
        History.addValueChangeHandler(new CatchupMathMobileHistoryListener());
        
        
//        showTestSolution();
//        if(true)return;
        
        int uid = CatchupMathMobile.getQueryParameterInt("uid");
        if(uid > 0) {
            int testId = CatchupMathMobile.getQueryParameterInt("testId");
            int testSegment = CatchupMathMobile.getQueryParameterInt("testSegment");
            user = new CmMobileUser(uid,testId,testSegment,0,0);
            
            History.newItem("quiz");
        }
        else {
            History.newItem("login");
        }
        // bypass anchor info on startup
        // History.fireCurrentHistoryState();
    }
    
    static public CmMobileUser getUser() {
        return __instance.user;
    }
    
    public void showLoginForm() {
        mainPanel.setWidget(new LoginForm());
    }
    
    public void showQuizPanel() {
        mainPanel.setWidget(new QuizPanel());    
    }
    
    /** show the PrescriptionPanel loading the prescription
     *  currently loaded into User
     */
    public void showPrescriptionPanel() {
        mainPanel.setWidget(new PrescriptionPanel());
    }
    
    public void showResourcePanel(InmhItemData item) {
        mainPanel.setWidget(CmMobileResourceViewerFactory.createViewer(item).getViewer(item));
    }
    
    
    private void showTestSolution() {
        user = new CmMobileUser(23502,19959,4,0,0);
        InmhItemData item = new InmhItemData("practice", "samples_1_1_SampleExercises_1-Algebra_1","Test Solution");
        mainPanel.setWidget(CmMobileResourceViewerFactory.createViewer(item).getViewer(item));
    }
    

    /** Static routines used throughout app
     * 
     *  TODO: move to separate module
     *
     * @return
     */
    static CmServiceAsync getCmService() {
        return _cmService;
    }

    static CmServiceAsync _cmService;

    static private void setupServices() {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";

        _cmService = (CmServiceAsync) GWT.create(CmService.class);
        ((ServiceDefTarget) _cmService).setServiceEntryPoint(point + "services/cmService");
    }
    
    
    static {
        setupServices();
        _queryParameters = readQueryString();
    }
    /**
     * Return the parameter passed on query string
     * 
     * returns null if parameter not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameter(String name) {
        return _queryParameters.get(name);
    }
    
    static public int getQueryParameterInt(String name) {
        try {
            String val = getQueryParameter(name);
            if(val != null)
                return Integer.parseInt(val);
        }
        catch(Exception e) {
            /* silent */
        }
        return 0;
    }
    
    /**
     * Convert string+list to string+string of all URL parameters
     * 
     */
    static Map<String, String> _queryParameters;
    static private Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        return m;
    }
}
