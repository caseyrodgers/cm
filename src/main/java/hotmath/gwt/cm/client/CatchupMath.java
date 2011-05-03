package hotmath.gwt.cm.client;

import hotmath.gwt.cm.client.history.CatchupMathHistoryListener;
import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager;
import hotmath.gwt.cm.client.ui.EndOfProgramPanel;
import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition;
import hotmath.gwt.cm.client.ui.context.PrescriptionContext;
import hotmath.gwt.cm.client.ui.context.QuizCmGuiDefinition;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.FooterPanel;
import hotmath.gwt.cm_tools.client.util.GenericVideoPlayerForMona;
import hotmath.gwt.cm_tools.client.util.GenericVideoPlayerForMona.MonaVideo;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.NetTestWindow;
import hotmath.gwt.shared.client.util.UserInfoDao;

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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMath implements EntryPoint {
    static {
       publishNative();
       publishNativeJsAfterLoad();
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

    /**
     * Flag to indicate message about show work has been given (one per login)
     * 
     */
    public static boolean __hasBeenInformedAboutShowWork;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        __thisInstance = this;
    	CmLogger.info("Catchup Math Startup");

        // GXT.setDefaultTheme(Theme.GRAY, true);

        _mainPort = new Viewport();

        CmBusyManager.setViewPort(_mainPort);

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
        if (CmShared.getQueryParameter("debug") != null || CmShared.getQueryParameter("debug_uid") != null) {
            FooterPanel footer = new FooterPanel();
            _mainPort.add(footer, new BorderLayoutData(LayoutRegion.SOUTH, 20));
        }

        /** Turn on debugging CSS */
        if (CmShared.getQueryParameter("debug") != null) {
            _mainPort.addStyleName("debug-on");
        }

        /**
         * Add the main panel to the "hm_content" div on the CatchupMath.html
         * 
         */
        RootPanel.get("main-content").add(_mainPort);

        CmShared.handleLoginProcessAsync(new CmLoginAsync() {
            public void loginSuccessful(Integer uid) {
                processLoginComplete(uid);
            }
        });
    }

    
    public void installMainPanelListener() {
        /**
         * Install listener to track any changes to the main window
         * 
         */
        _mainPort.addListener(Events.Resize, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                if (CmMainPanel.__lastInstance != null && CmMainPanel.__lastInstance._mainContent != null) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WINDOW_RESIZED));
                }
            }   
        });        
    }
    
    

    /**
     * Called when successfully logged into CM server
     * 
     * @param uid
     */
    private void processLoginComplete(final Integer uid) {
    	try {
	    	String jsonUserInfo = getUserInfoFromExtenalJs();
	    	CmDestination firstLocation = UserInfoDao.loadUserAndReturnFirstAction(jsonUserInfo);
	
	    	if (CmShared.getQueryParameterValue("type").equals("su")) {
	    		UserInfo.getInstance().setUserAccountType(UserInfo.UserType.SINGLE_USER);
	    	}
	    	if (UserInfo.getInstance().isSingleUser())
	    		Window.setTitle("Catchup Math: Student");
	    	
            if(firstLocation.getPlace() == CmPlace.END_OF_PROGRAM) {
                showEndOfProgramPanel();
                return;
            }
            
            
	    	String ac = UserInfoBase.getInstance().getCmStartType();
	    	if(ac == null)ac = "";
	    	if (ac.equals("AUTO_CREATE")) {
	    		/**
	    		 * self registration
	    		 * 
	    		 * mark as not owner, since this is templated.
	    		 */
	    		UserInfo.getInstance().setActiveUser(false);
	    		CatchupMath.__thisInstance.showAutoRegistration_gwt();
	    	} else if (CmShared.getQueryParameterValue("type").equals("auto_test")) {
	    		__thisInstance.startNormalOperation();
	    	} else if (CmShared.getQueryParameter("debug_info") != null) {
	    		setDebugOverrideInformation(CmShared.getQueryParameter("debug_info"));
	    		__thisInstance.startNormalOperation();
	    	} else if (firstLocation.getPlace() == CmPlace.PRESCRIPTION || firstLocation.getPlace() == CmPlace.QUIZ) {
	    		/**
	    		 * already has active session, just move to current
	    		 * position.
	    		 */
	    		startNormalOperation();
	    	} else {
	    		/**
	    		 * Otherwise, show the welcome screen to new visits
	    		 * 
	    		 */
	    		showWelcomePanel();
	    	}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		CatchupMathTools.showAlert("There has been a problem creating the user object: " + e.getMessage());
    	}
    }

    /** return the user_info data passed in the bootstrap html 
     * 
     * @return
     */
    static private native String getUserInfoFromExtenalJs() /*-{
       var d = $doc.getElementById('user_info');
       return d.innerHTML;
    }-*/;    

    /**
     * read token containing the uid, test and run ids:
     * 
     * uid:tid:runid
     * 
     * 
     * @param di
     */
    private void setDebugOverrideInformation(String di) {
        UserInfo ui = UserInfo.getInstance();
        String p[] = di.split(":");
        int uid = Integer.parseInt(p[0]);
        if (uid > 0)
            ui.setUid(uid);
        if (p.length > 1)
            ui.setTestId(Integer.parseInt(p[1]));
        if (p.length > 2)
            ui.setRunId(Integer.parseInt(p[2]));
    }

    /**
     * Startup the history and initial history state check
     * 
     * This should happen just once during program operation
     * 
     */
    public void startNormalOperation() {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onSuccess() {
                startNormalOperations();
            }
            
            @Override
            public void onFailure(Throwable reason) {
                Window.alert("Error starting up Catchup Math: " + reason);
            }
        });
        
    }
    
    private void startNormalOperations() {
        
        installMainPanelListener();
        
        History.addValueChangeHandler(new CatchupMathHistoryListener());
        
        /** Don't allow bookmark to move paste server's location
         * 
         */
        // History.fireCurrentHistoryState();
        

        /**
         * Register an event lister waiting to see if user's data change. If it
         * does, we must reset this user
         * 
         */
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_USER_PROGRAM_CHANGED) {
                    if (((Boolean) event.getEventData()) == true) {
                        CmShared.resetProgram_Gwt(UserInfo.getInstance().getUid());
                    } else {
                        // just refresh page
                        CmShared.refreshPage();
                    }
                }
            }
        });

        String ac = CmShared.getQueryParameterValue("type");
        if (ac.equals("auto_test_net")) {
            /** should we only run net test? */
            new NetTestWindow(TestApplication.CM_STUDENT,UserInfo.getInstance().getUid()).repeatTestEvery(10000);
        } else if (ac.equals("auto_test")) {
            /** or, run the full test? */
            FooterPanel.startAutoTest_Gwt();
        }
        
        
        jumpToFirstLocation();
        
    }
    
    
    private void jumpToFirstLocation() {
        CmProgramFlowClientManager.getActiveProgramState(new CmProgramFlowClientManager.Callback() {
            @Override
            public void programFlow(CmProgramFlowAction flowResponse) {
                
                switch(flowResponse.getPlace()) {
                    case QUIZ:
                        /** show the quiz panel with data included
                         *  in the next action object.
                         */
                        showQuizPanel(flowResponse.getQuizResult());
                        break;
                        
                    case PRESCRIPTION:
                        
                        showPrescriptionPanel(flowResponse.getPrescriptionResponse());
                        break;
                        
                    case END_OF_PROGRAM:
                        showEndOfProgramPanel();
                        break;
                        
                     default:
                         CmLogger.error("Unknown NextAction type: " + flowResponse);
                            
                }
                CmLogger.info(flowResponse.toString());
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
    public void showQuizPanel(int segmentNumber) {
        showQuizPanel_gwt(segmentNumber);
    }
    
    public void showQuizPanel(final QuizHtmlResult quizHtml) {
            GWT.runAsync(new CmRunAsyncCallback() {

                @Override
                public void onSuccess() {
                    HeaderPanel.__instance.enable();

                    _mainContainer.removeAll();
                    _mainContainer.setLayout(new FitLayout());
                    _mainContainer.add(new CmMainPanel(new QuizCmGuiDefinition(quizHtml)));
                    _mainContainer.layout();
                }
            });
        }
    

    public void showQuizPanel_gwt(final int segmentNumber) {

        GWT.runAsync(new CmRunAsyncCallback() {

            @Override
            public void onSuccess() {
                HeaderPanel.__instance.enable();

                _mainContainer.removeAll();
                _mainContainer.setLayout(new FitLayout());
                _mainContainer.add(new CmMainPanel(new QuizCmGuiDefinition(segmentNumber)));
                _mainContainer.layout();
            }
        });
    }

    public void showWelcomePanel() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                HeaderPanel.__instance.enable();

                _mainContainer.removeAll();
                _mainContainer.setLayout(new FitLayout());
                _mainContainer.add(new WelcomePanel());

                _mainContainer.layout();
            }
        });
    }

    /**
     * Helper page to create the Prescription page
     * 
     * @TODO: get out of main
     * 
     */
    public void showPrescriptionPanel() {
        CmLocation location = new CmLocation(LocationType.PRESCRIPTION, UserInfo.getInstance().getSessionNumber());
        CmHistoryManager.getInstance().addHistoryLocation(location);
    }
    
    public void showPrescriptionPanel(PrescriptionSessionResponse prescriptionResponse) {
        CmLogger.info("Showing prescription panel: " + prescriptionResponse);
        UserInfo.getInstance().setCorrectPercent(prescriptionResponse.getCorrectPercent());
        if(ContextController.getInstance().getTheContext() instanceof PrescriptionContext) {
            /** PrescriptionPage is currently in view, simply update its display
             * 
             */
            PrescriptionCmGuiDefinition.__instance.getAsyncDataFromServer(UserInfo.getInstance().getSessionNumber());
            
        }
        else {
            /** Load the PrescriptionContext 
             * 
             */
            showPrescriptionPanel_gwt(prescriptionResponse);
        }
        
    }


    public void showPrescriptionPanel_gwt(final PrescriptionSessionResponse prescriptionResponse) {

        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                HeaderPanel.__instance.enable();

                _mainContainer.removeAll();
                _mainContainer.setLayout(new FitLayout());
                PrescriptionCmGuiDefinition prescriptionGui = new PrescriptionCmGuiDefinition();
                _mainContainer.add(new CmMainPanel(prescriptionGui));
                _mainContainer.layout();
                
                /** set the data returned from the server
                 *  as the initial lesson shown
                 */
                prescriptionGui.setPrescriptionData(prescriptionResponse,prescriptionResponse.getPrescriptionData().getCurrSession().getSessionNumber());
            }
        });
    }

    /**
     * Display the Auto Registration panel
     * 
     * Does not push onto history stack.
     * 
     */
    public void showAutoRegistration_gwt() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                _mainContainer.removeAll();
                _mainContainer.setLayout(new FitLayout());
                _mainContainer.add(new AutoStudentRegistrationPanel());
                _mainContainer.layout();
            }
        });
    }

    
    public void showEndOfProgramPanel() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                _mainContainer.removeAll();
                _mainContainer.setLayout(new FitLayout());
                _mainContainer.add(new EndOfProgramPanel());
                _mainContainer.layout();
            }
        });
    }
    
    
    /**
     * Provides helper method to load a resource into the current
     * PrespccriptionContext
     * 
     * @param type
     * @param file
     */
    static private void doResourceLoad(final String type, final String file) {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                CmLocation location = new CmLocation("p:" + UserInfo.getInstance().getSessionNumber() + ":" + type
                        + ":" + file);
                CmHistoryManager.getInstance().addHistoryLocation(location);
            }
        });
    }

    static public void showMotivationalVideo_Gwt() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                new GenericVideoPlayerForMona(MonaVideo.MOTIVATIONAL);
            }
        });
    }

    /**
     * Push a GWT method onto the global space for the app window
     * 
     * This will be called from CatchupMath.js:doResourceLoad
     * 
     */
    static private native void publishNative() /*-{
        // Set global variable to signal that CM system has been initialized.
        // This is checked in CatchupMath.html to indicate that a loading error occurred.
        $wnd.__cmInitialized = true;
    }-*/;
    
    static private native void publishNativeJsAfterLoad() /*-{
                                               $wnd.doLoadResource_Gwt = @hotmath.gwt.cm.client.CatchupMath::doResourceLoad(Ljava/lang/String;Ljava/lang/String;);
                                               
                                               $wnd.showMotivationalVideo_Gwt = @hotmath.gwt.cm.client.CatchupMath::showMotivationalVideo_Gwt();
                                               }-*/;
}
