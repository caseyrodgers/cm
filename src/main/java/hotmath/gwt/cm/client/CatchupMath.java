package hotmath.gwt.cm.client;

import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;
import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition;
import hotmath.gwt.cm.client.ui.context.QuizCmGuiDefinition;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.FooterPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.FlashVersionNotSupportedWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMath implements EntryPoint {
    
    final static String version = "1.0 beta";
    
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
    int userId;
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

        try {
            userId = CmShared.handleLoginProcess();

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
            
        }
        catch(Exception e) {
            CatchupMathTools.showAlert(e.getMessage(), new CmAsyncRequestImplDefault()  {
                public void requestComplete(String requestData) {
                    Window.Location.assign(CmShared.CM_HOME_URL); // goto home
                }
            });
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
                        
                        if(UserInfo.getInstance().isSingleUser())
                            Window.setTitle("Catchup Math: Student");


                        String ac=CmShared.getQueryParameter("type");
                        if(ac != null && ac.equals("ac")) {
                            showAutoRegistration_gwt();
                        }                        
                        else if(UserInfo.getInstance().getRunId() > 0) {
                            // load the existing run
                            showPrescriptionPanel_gwt();
                        }
                        else {
                            // show the quiz and prepare for a new run
                            showQuizPanel_gwt();
                        }
                    }
                    public void requestFailed(int code, String text) {
                        CatchupMathTools.showAlert("There was a problem logging in ");
                    }
                });
            }
        });
        
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
            public String getEventOfInterest() {
                return EventBus.EVENT_TYPE_USER_PROGRAM_CHANGED;
            }
        });
        
        
        History.fireCurrentHistoryState();
    }
    

    
    private native String jsDecode(String s) /*-{
        return decodeURIComponent(s);
    }-*/;





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
    
    
    /** Display the Auto Registration panel
     * 
     */
    private void showAutoRegistration_gwt() {
        _mainContainer.removeAll();
        _mainContainer.setLayout(new FitLayout());
        _mainContainer.add(new AutoStudentRegistrationPanel() );
        _mainContainer.layout();
    }
    

    static private void doResourceLoad(String type, String file) {
        InmhItemData resourceItem = new InmhItemData();
        resourceItem.setType(type);
        resourceItem.setFile(file);
        
        CmMainPanel.__lastInstance._mainContent.showResource(resourceItem);
    }
    
    /** Push a GWT method onto the global space for the app window
     * 
     *   This wil be called from CatchupMath.js:doResourceLoad
     *   
     */
    static private native void publishNative() /*-{
                                    $wnd.doLoadResource_Gwt = @hotmath.gwt.cm.client.CatchupMath::doResourceLoad(Ljava/lang/String;Ljava/lang/String;);
                                    }-*/;

    
    
    
    
    
}
