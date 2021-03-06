package hotmath.gwt.cm.client;

import hotmath.gwt.cm.client.history.CatchupMathHistoryListener;
import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm.client.ui.AssignmentsOnlyPanel;
import hotmath.gwt.cm.client.ui.EndOfProgramPanel;
import hotmath.gwt.cm.client.ui.FooterPanel;
import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm.client.ui.NoProgramAssignedPanel;
import hotmath.gwt.cm.client.ui.StudentAssignmentSelectorDialog;
import hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition;
import hotmath.gwt.cm.client.ui.context.PrescriptionContext;
import hotmath.gwt.cm.client.ui.context.QuizCheckResultsWindow;
import hotmath.gwt.cm.client.ui.context.QuizCmGuiDefinition;
import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.event.CmLogoutEvent;
import hotmath.gwt.cm_core.client.flow.CmProgramFlowClientManager;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.CmBusyManager.BusyHandler;
import hotmath.gwt.cm_core.client.util.CmBusyManager.BusyState;
import hotmath.gwt.cm_core.client.util.CmIdleTimeWatcher;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.UserProgramCompletionAction;
import hotmath.gwt.cm_rpc.client.event.TutorContainerActivatedEvent;
import hotmath.gwt.cm_rpc.client.event.TutorContainerActivatedEventHandler;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.search.LessonSearchWindow;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.assignment.StudentAssignmentViewerPanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmMainResourceWrapper.ResourceWrapperCallback;
import hotmath.gwt.cm_tools.client.util.VideoPlayerWindow;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.NetTestWindow;
import hotmath.gwt.shared.client.util.SystemSyncChecker;
import hotmath.gwt.shared.client.util.UserInfoDao;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasNativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMath implements EntryPoint, HasNativeEvent {
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

    public Viewport _mainPort;
    BorderLayoutContainer _mainPortWrapper = new BorderLayoutContainer();

    static CatchupMath __thisInstance;

    public SimpleContainer _mainContainer;
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

        if (CatchupMathTests.runTest()) {
            CmBusyManager.showLoading(false);
            return;
        }

        // GXT.setDefaultTheme(Theme.GRAY, true);

        _mainPort = new Viewport() {
            protected void onWindowResize(int width, int height) {
                super.onWindowResize(width, height);
                if (CmMainPanel.getActiveInstance() != null
                        && CmMainPanel.getActiveInstance()._mainContentWrapper != null) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WINDOW_RESIZED));
                }
                if (CmRpcCore.EVENT_BUS != null) {
                    CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
                }
            }
        };
        _mainPort.setWidget(_mainPortWrapper);

        CmBusyManager.setBusyHandler(new BusyHandler() {
            @Override
            public void hideMask() {
                _mainPort.unmask();
            }

            @Override
            public void showMask(BusyState state) {
                _mainPort.mask();
            }
        });

        BorderLayoutData bdata = new BorderLayoutData(38);
        _headerPanel = new HeaderPanel();
        _mainPortWrapper.setNorthWidget(_headerPanel, bdata);

        _mainContainer = new SimpleContainer();
        _mainContainer.setStyleName("main-container");
        _mainContainer.getElement().setId("main-container");

        bdata = new BorderLayoutData();
        _mainPortWrapper.setCenterWidget(_mainContainer, bdata);

        bdata = new BorderLayoutData(20);
        if (CmCore.isDebug() == true || CmCore.getQueryParameter("debug_uid") != null) {
            FooterPanel footer = new FooterPanel();
            _mainPortWrapper.setSouthWidget(footer, new BorderLayoutData(20));
        }

        /** Turn on debugging CSS */
        if (CmCore.getQueryParameter("debug") != null) {
            _mainPortWrapper.addStyleName("debug-mode");
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

        if (CmCore.getQueryParameterValue("test").equals("assignment")) {
            showAssignment(1, null);
        } else if (CmCore.getQueryParameterValue("test").equals("search")) {
            new LessonSearchWindow();
            return;
        }

        /**
         * add a low level down handler to catch any mouse down event
         *
         */
        _mainContainer.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                CmIdleTimeWatcher.getInstance().didKeyBoardActivity();
            }
        }, MouseDownEvent.getType());
        CmIdleTimeWatcher.getInstance();

        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> event) {
                CmRpcCore.EVENT_BUS.fireEvent(new CmLogoutEvent());
            }
        });

        CmProgramFlowClientManager.setFlowCallback(new StandardFlowCallback());

        // Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
        // public void onPreviewNativeEvent(NativePreviewEvent event) {
        // NativeEvent ne = event.getNativeEvent();
        // GWT.log(ne.getCharCode() + " (" + ((char) ne.getCharCode()) + ") " +
        // (ne.getButton() != 1 ? " button=" + ne.getButton() : "") +
        // (ne.getKeyCode() != ne.getCharCode() ? " keyCode=" + ne.getKeyCode()
        // : "") +
        // (ne.getAltKey() ? " ALT" : "") +
        // (ne.getCtrlKey() ? " CTRL" : "") +
        // (ne.getMetaKey() ? " META" : "") +
        // (ne.getShiftKey() ? " SHIFT" : ""));
        // }
        // });

        // CmNotifyManager.getInstance().notify("This is a test notification");
    }

    /**
     * Called when successfully logged into CM server
     *
     * @param uid
     */
    private void processLoginComplete(final Integer uid) {

        try {
            String jsonUserInfo = CmGwtUtils.getUserInfoFromExtenalJs();

            String startType = UserInfoBase.getInstance().getCmStartType();
            if (startType == null)
                startType = "";

            // startType = "AUTO_CREATE";

            CmDestination firstLocation = UserInfoDao.loadUserAndReturnFirstAction(jsonUserInfo);

            if (CmCore.getQueryParameterValue("type").equals("su")) {
                UserInfo.getInstance().setUserAccountType(UserInfo.UserType.SINGLE_USER);
            }
            if (UserInfo.getInstance().isSingleUser())
                Window.setTitle("Catchup Math: Student");

            if (firstLocation.getPlace() == CmPlace.END_OF_PROGRAM
                    && UserInfo.getInstance().getOnCompletion() == UserProgramCompletionAction.STOP) {
                showEndOfProgramPanel();
            } else if (firstLocation.getPlace() == CmPlace.NO_PROGRAM_ASSIGNED) {
                showNoProgramAssigned();
            } else {

                if (startType.equals("AUTO_CREATE")) {
                    /**
                     * self registration
                     *
                     * mark as not owner, since this is templated.
                     */
                    UserInfo.getInstance().setActiveUser(false);
                    showAutoRegistration_gwt();
                } else if (startType.equals("PARALLEL_PROGRAM")) {
                    /**
                     * Parallel Program - need student's password which must be
                     * associated with same account as Parallel Program
                     *
                     */
                    UserInfo.getInstance().setActiveUser(false);
                    showParallelProgramPasswordPanel();
                    return;
                } else if (CmCore.getQueryParameterValue("type").equals("auto_test")) {
                    startNormalOperation();
                } else if (CmCore.getQueryParameter("debug_info") != null) {
                    setDebugOverrideInformation(CmCore.getQueryParameter("debug_info"));
                    startNormalOperation();
                } else if (firstLocation.getPlace() == CmPlace.PRESCRIPTION || firstLocation.getPlace() == CmPlace.QUIZ) {
                    /**
                     * already has active session, just move to current
                     * position.
                     */
                    startNormalOperation();
                } else if (firstLocation.getPlace() == CmPlace.ASSIGNMENTS_ONLY) {
                    showAssignmentsOnly();
                } else {

                    /**
                     * Otherwise, show the welcome screen to new visits
                     *
                     */
                    showWelcomePanel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CatchupMathTools.showAlert("There has been a problem creating the user object: " + e.getMessage());
        } finally {
            CmBusyManager.showLoading(false);
        }

        SystemSyncChecker.monitorVersionChanges();
    }

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

        History.addValueChangeHandler(new CatchupMathHistoryListener());

        /**
         * Don't allow bookmark to move past server's location
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

        /**
         * List for event when a new TutorWContainer is displayed. We need to
         * hide the main tutor window because of hard-coding issues with the
         * tutor-content id.
         */
        CmRpcCore.EVENT_BUS.addHandler(TutorContainerActivatedEvent.TYPE, new TutorContainerActivatedEventHandler() {
            @Override
            public void tutorContainerActivated(TutorContainerActivatedEvent event) {
                CmMainPanel.getActiveInstance().removeResourceIfTutor();
            }
        });

        String ac = CmCore.getQueryParameterValue("type");
        if (ac.equals("auto_test_net")) {
            /** should we only run net test? */
            new NetTestWindow(TestApplication.CM_STUDENT, UserInfo.getInstance().getUid()).repeatTestEvery(10000);
        } else if (ac.equals("auto_test")) {
            /** or, run the full test? */
            FooterPanel.startAutoTest_Gwt();
        }

        jumpToFirstLocation();
    }

    private void jumpToFirstLocation() {
        CmProgramFlowClientManager.getActiveProgramState(new StandardFlowCallback() {
            @Override
            public void programFlow(CmProgramFlowAction flowResponse) {

                switch (flowResponse.getPlace()) {
                case QUIZ:

                    /**
                     * show the quiz panel with data included in the next action
                     * object.
                     */
                    showQuizPanel(flowResponse.getQuizResult());
                    break;

                case PRESCRIPTION:
                    showPrescriptionPanel(flowResponse.getPrescriptionResponse());
                    break;

                case END_OF_PROGRAM:
                    showEndOfProgramPanel();
                    break;

                case AUTO_ADVANCED_PROGRAM:
                    QuizCheckResultsWindow.autoAdvanceUser();
                    break;

                case ASSIGNMENTS_ONLY:
                    showAssignmentsOnly();
                    break;

                case NO_PROGRAM_ASSIGNED:
                    showNoProgramAssigned();
                    break;

                default:
                    CmLogger.error("Unknown NextAction type: " + flowResponse);

                }
                CmLogger.info(flowResponse.toString());
            }

        });
    }

    protected void showAssignmentsOnly() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                _mainContainer.clear();
                _mainContainer.add(new AssignmentsOnlyPanel());
                _mainContainer.forceLayout();

                StudentAssignmentSelectorDialog.showSharedDialog();
            }
        });
    }

    protected void showNoProgramAssigned() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                _mainContainer.clear();
                _mainContainer.add(new NoProgramAssignedPanel());
                _mainContainer.forceLayout();
            }
        });
    }

    public void showAssignments_gwt() {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onSuccess() {
                StudentAssignmentSelectorDialog.showSharedDialog();
            }

            @Override
            public void onFailure(Throwable reason) {
                reason.printStackTrace();
                Log.error("Error showing Assingment Selector dialog: " + reason.getMessage());
            }
        });
    }

    public void showAssignment(final int assignKey, final String pid) {

        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                final Widget assignmentViewer = new StudentAssignmentViewerPanel(assignKey, pid,
                        new CallbackOnComplete() {
                            @Override
                            public void isComplete() {
                                HeaderPanel.showRppDetails(true);
                                _mainPortWrapper.setCenterWidget(_mainContainer);
                                _mainPort.forceLayout();
                            }
                        });
                _mainPortWrapper.remove(_mainContainer);
                HeaderPanel.showRppDetails(false);
                BorderLayoutData bdata = new BorderLayoutData();
                _mainPortWrapper.setCenterWidget(assignmentViewer, bdata);
                _mainPort.forceLayout();
            }

            @Override
            public void onFailure(Throwable reason) {
                reason.printStackTrace();
                Log.error("Error loading assignments GUI: " + reason.getMessage());
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

                _mainContainer.clear();
                _mainContainer.add(new CmMainPanel(new QuizCmGuiDefinition(quizHtml)));

                _mainContainer.forceLayout();
            }
        });
    }

    public void showQuizPanel_gwt(final int segmentNumber) {

        GWT.runAsync(new CmRunAsyncCallback() {

            @Override
            public void onSuccess() {
                HeaderPanel.__instance.enable();

                _mainContainer.clear();
                _mainContainer.add(new CmMainPanel(new QuizCmGuiDefinition(segmentNumber)));

                _mainContainer.forceLayout();
            }
        });
    }

    public void showWelcomePanel() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                HeaderPanel.__instance.enable();

                _mainContainer.clear();
                _mainContainer.add(new WelcomePanel());

                _mainContainer.forceLayout();
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
        if (ContextController.getInstance().getTheContext() instanceof PrescriptionContext) {
            /**
             * PrescriptionPage is currently in view, simply update its display
             *
             */
            PrescriptionCmGuiDefinition.__instance.getAsyncDataFromServer(UserInfo.getInstance().getSessionNumber());

        } else {
            /**
             * Load the PrescriptionContext
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

                _mainContainer.clear();
                PrescriptionCmGuiDefinition prescriptionGui = new PrescriptionCmGuiDefinition();
                _mainContainer.add(new CmMainPanel(prescriptionGui));

                _mainContainer.forceLayout();

                /**
                 * set the data returned from the server as the initial lesson
                 * shown
                 */
                prescriptionGui.setPrescriptionData(prescriptionResponse, prescriptionResponse.getPrescriptionData()
                        .getCurrSession().getSessionNumber());
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
                _mainContainer.clear();
                _mainContainer.add(new AutoStudentRegistrationPanel(new ResourceWrapperCallback() {
                    @Override
                    public ResizeContainer getResizeContainer() {
                        return CmMainPanel.getActiveInstance();
                    }
                }).getResourceWrapper());
                _mainContainer.forceLayout();
            }
        });
    }

    public void showParallelProgramPasswordPanel() {
        GWT.runAsync(new CmRunAsyncCallback() {

            @Override
            public void onSuccess() {
                _mainContainer.clear();
                _mainContainer.add(new ParallelProgramPasswordPanel(new ResourceWrapperCallback() {
                    @Override
                    public ResizeContainer getResizeContainer() {
                        return CmMainPanel.getActiveInstance();
                    }
                }).getResourceWrapper());
                _mainContainer.forceLayout();
            }
        });
    }

    public void showEndOfProgramPanel() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                _mainContainer.clear();
                _mainContainer.add(new EndOfProgramPanel());
                _mainContainer.forceLayout();
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

    static protected void showStudentHowToVideo() {
        new VideoPlayerWindow("How to use Catchup Math",
                "assets/teacher_videos/student-how-to/student-how-to-desktop.mp4");
    }

    static public Hyperlink getStudentHowToVideoHyperlink() {
        Hyperlink hl = new Hyperlink();
        hl.setText("Video: How to use Catchup Math");
        hl.getElement().setAttribute("style",
                "margin-left:25px; text-decoration:underline; color:#00A8FF; cursor:pointer;");
        ClickHandler handler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showStudentHowToVideo();
            }
        };
        hl.addHandler(handler, ClickEvent.getType());
        return hl;
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
                                                          }-*/;

    @Override
    public NativeEvent getNativeEvent() {
        // TODO Auto-generated method stub
        return null;
    }
}
