package hotmath.gwt.cm.client;

import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition;
import hotmath.gwt.cm.client.ui.context.QuizCmGuiDefinition;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.FooterPanel;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.history.CatchupMathHistoryListener;
import hotmath.gwt.shared.client.history.CmHistoryManager;
import hotmath.gwt.shared.client.history.CmLocation;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMath implements EntryPoint {
    
    final static String version = "1.0";
    
    static {
        publishNative();
    }

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

    /** Flag to indicate message about show work has been given (one per login)
     * 
     */
    public static boolean __hasBeenInformedAboutShowWork;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        Log.info("Catchup Math Startup: " + version);
        
        __thisInstance = this;

        // GXT.setDefaultTheme(Theme.GRAY, true);

        __hasBeenInformedAboutShowWork=false;
        
        _mainPort = new Viewport();
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

        
        /** Call routine to acquire the users uid
         * 
         */
        CmShared.handleLoginProcessAsync(new CmLoginAsync() {
            public void loginSuccessful(Integer uid) {
                
                processLoginComplete(uid);
                
            }
        });
        
        /** Install listener to track any changes to the main window 
         * 
         */
        _mainPort.addListener(Events.Resize, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                if(CmMainPanel.__lastInstance != null && CmMainPanel.__lastInstance._mainContent != null) {
                    CmMainPanel.__lastInstance._mainContent.resetChildSize();
                }
            }
        });
        RootPanel.get("main-content").add(_mainPort);
        
        
        /** Register an event lister waiting to see if user's data change.
         *  If it does, we must reset this user
         * 
         */
        EventBus.getInstance().addEventListener(new CmEventListener() {
            public void handleEvent(CmEvent event) {
                if(((Boolean)event.getEventData()) == true) {
                    FooterPanel.resetProgram_Gwt();
                }
                else {
                    // just refresh page
                    FooterPanel.refreshPage();
                }
            }
            public String[] getEventsOfInterest() {
                String types[] = {EventBus.EVENT_TYPE_USER_PROGRAM_CHANGED};
                return types;
            }
        });
    }
    
    
    
    /** Call when successfully determined users uid
     * 
     * @param uid
     */
    private void processLoginComplete(final Integer uid) {
        
        // if run_id passed in, then allow user to view_only
        if(CmShared.getQueryParameter("run_id") != null) {
            int runId = Integer.parseInt(CmShared.getQueryParameter("run_id"));
            // setup user to masquerade as real user
            UserInfo visiter = new UserInfo(0,0);
            UserInfo.setInstance(visiter);
            UserInfo.getInstance().setRunId(runId);
        }
        
        /** Turn on debugging CSS */
        if(CmShared.getQueryParameter("debug") != null) {
            _mainPort.addStyleName("debug-on");
        }
        
        
        UserInfo.loadUser(uid,new CmAsyncRequest() {
            public void requestComplete(String requestData) {
                
                if(UserInfo.getInstance().isSingleUser())
                    Window.setTitle("Catchup Math: Student");


                String ac=CmShared.getQueryParameter("type");
                if(ac != null && ac.equals("ac")) {
                    
                    /** 
                     * self registration
                     * 
                     * mark as not owner, since this is a
                     */
                    UserInfo.getInstance().setActiveUser(false);
                    showAutoRegistration_gwt();
                }                        
                else {
                    
                    // ok, we are good to go
                    /** Add a history listener to manage the state changes
                     * 
                     */
                    History.addValueChangeHandler(new CatchupMathHistoryListener());
                    History.fireCurrentHistoryState();    
                }
                
            }
            public void requestFailed(int code, String text) {
                CatchupMathTools.showAlert("There was a problem reading user information from server" );
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

    public void showQuizPanel_gwt() {
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
    
    public void showPrescriptionPanel_gwt() {
        HeaderPanel.__instance.enable();

        _mainContainer.removeAll();
        _mainContainer.setLayout(new FitLayout());
        _mainContainer.add(new CmMainPanel(new PrescriptionCmGuiDefinition()));
        _mainContainer.layout();
    }
    
    
    /** Display the Auto Registration panel
     * 
     */
    private void showAutoRegistration_gwt() {
        _mainContainer.removeAll();
        _mainContainer.setLayout(new FitLayout());
        _mainContainer.add(new AutoStudentRegistrationPanel());
        _mainContainer.layout();
    }
    

    /** Provides helper method to load a resource into the current 
     *  PrespccriptionContext
     *  
     * @param type
     * @param file
     */
    static private void doResourceLoad(String type, String file) {
        CmLocation location = new CmLocation("p:" + UserInfo.getInstance().getSessionNumber() + ":" + type + ":" + file);
        CmHistoryManager.getInstance().addHistoryLocation(location);
        
        // CmMainPanel.__lastInstance._mainContent.showResource(resourceItem);
    }
    
    /** Push a GWT method onto the global space for the app window
     * 
     *   This will be called from CatchupMath.js:doResourceLoad
     *   
     */
    static private native void publishNative() /*-{
                                    $wnd.doLoadResource_Gwt = @hotmath.gwt.cm.client.CatchupMath::doResourceLoad(Ljava/lang/String;Ljava/lang/String;);
                                    }-*/;

    
    
    
    
    
}
