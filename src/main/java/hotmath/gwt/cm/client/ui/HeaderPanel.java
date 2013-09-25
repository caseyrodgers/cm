package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.history.CmHistoryQueue;
import hotmath.gwt.cm_core.client.event.CmLogoutEvent;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.MyIconButton;
import hotmath.gwt.cm_tools.client.ui.ShowDebugUrlWindow;
import hotmath.gwt.cm_tools.client.ui.ShowUserProgramStatusDialog;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tutor.client.event.UserTutorWidgetStatusUpdatedEvent;
import hotmath.gwt.cm_tutor.client.event.UserTutorWidgetStatusUpdatedHandler;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.util.CmIdleTimeWatcher;
import hotmath.gwt.shared.client.util.CmInfoConfig;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.MyResources;
import hotmath.gwt.shared.client.util.SystemSyncChecker;
import hotmath.system.SystemCheck;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class HeaderPanel extends FlowLayoutContainer {

    static public HeaderPanel __instance;

    Label _headerText;
    HTML _helloInfo = new HTML();
    private MyIconButton helpButton;
    StudentAssignmentButton _assignmentsAnchor;

    MyResources myResources = GWT.create(MyResources.class);

    public HeaderPanel() {
        __instance = this;
        setStyleName("header-panel");
        _helloInfo.setStyleName("hello-info");
        _helloInfo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ShowUserProgramStatusDialog();
            }
        });

        add(_helloInfo);

        helpButton = new MyIconButton("header-panel-help-btn");
        helpButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                Event currentEvent = helpButton.getCurrentEvent();
                if (currentEvent != null && currentEvent.getCtrlKey()) {
                    new ShowDebugUrlWindow();
                } else {
                    GWT.runAsync(new CmRunAsyncCallback() {
                        @Override
                        public void onSuccess() {
                            new HelpWindow();
                        }
                    });
                }
            }
        });
        add(helpButton);

        _headerText = new Label();
        _headerText.addStyleName("header-panel-title");
        // add(_headerText);

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(final CmEvent event) {
                switch (event.getEventType()) {
                case EVENT_TYPE_USERCHANGED:
                    setLoginInfo();
                    break;

                case EVENT_TYPE_CONTEXTCHANGED:
                    CmContext context = (CmContext) event.getEventData();
                    boolean tr = CmMainPanel.__lastInstance == null;
                    if (CmMainPanel.__lastInstance != null) {
                        /**
                         * note we set a default heading, no matter what the
                         * test type
                         */
                        CmMainPanel.__lastInstance._westPanelWrapper.setHeadingText(context.getContextSubTitle());
                    }
                    break;
                case EVENT_TYPE_TOPIC_CHANGED:
                    /**
                     * Only show modal popup if not in auto test mode
                     * 
                     */
                    if (CmShared.getQueryParameter("debug") != null || UserInfo.getInstance().isAutoTestMode()
                            || CmHistoryQueue.getInstance().isInitializingToNonStandard())
                        InfoPopupBox.display(new CmInfoConfig("Current Topic", "Current topic is: " + event.getEventData()));
                    else {
                       // new ContextChangeMessage((String) event.getEventData());
                    }
                    break;

                case EVENT_TYPE_USER_LOGIN:
                    /** done after login to allow parter info to be set first */
                    addLogoutButton();
                    break;

                case EVENT_TYPE_LOGOUT:
                    setLogout();
                    break;
                }

            }
        });
    }

    public void enable() {
        // no op
    }

    protected void updateUserTutorStats(UserTutorWidgetStats userStats) {
        UserInfo.getInstance().setTutorInputWidgetStats(userStats);
        setLoginInfo();
    }

    private void updateAssignmentMessage(AssignmentUserInfo assInfo) {
        if(assInfo.isAdminUsingAssignments()) {
            if(_assignmentsAnchor == null) {
                _assignmentsAnchor = new StudentAssignmentButton();
        
                /**
                 * Assignments not available for retail accounts
                 * 
                 */
                add(_assignmentsAnchor);
            }
            if (_assignmentsAnchor != null) {
                _assignmentsAnchor.setState(UserInfo.getInstance().getAssignmentMetaInfo());
            }
        }
    }

    /**
     * TODO: how to share this between student and admin
     * 
     */
    IconButton logoutButton;

    private void addLogoutButton() {
        final CmPartner partner = UserInfoBase.getInstance().getPartner();
        String logoClass = null;
        if (partner != null) {
            logoClass = "header-panel-logout-btn_cm-partner-" + partner.key;
        } else {
            logoClass = "header-panel-logout-btn";
        }

        logoutButton = new IconButton(logoClass);
        logoutButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                SystemSyncChecker.checkForUpdate(false, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        if (partner != null) {
                            CmLogger.info("Doing custom thing: " + partner.onCloseLink);
                            try {
                                Window.Location.assign(partner.onCloseLink);
                            } catch (Exception e) {
                                CatchupMathTools.showAlert("Error returning to our partner page: " + e.getMessage());
                            }
                        } else {
                            Window.Location.assign(CmShared.CM_HOME_URL);
                        }
                    }
                });
            }
        });

        add(logoutButton);
    }

    private void setLogout() {
        if (logoutButton != null) {
            remove(logoutButton);
            logoutButton = null;
        }

        if (helpButton != null) {
            remove(helpButton);
            helpButton = null;
        }
    }

    public void setLoginInfo() {
        UserInfo user = UserInfo.getInstance();
        int viewCount = UserInfo.getInstance().getViewCount();
        if (user != null) {
            String nameCap = user.getUserName();
            if (nameCap == null)
                return;

            /**
             * Check for demo user and normalize the display name
             * 
             */
            if (nameCap.startsWith("Student: "))
                nameCap = "Student";

            nameCap = nameCap.substring(0, 1).toUpperCase() + nameCap.substring(1);
            String s = "Welcome <b>" + nameCap + "</b>.";
            if (viewCount > 1) {
                s += "  You have completed " + viewCount + " problems. ";
                if (UserInfo.getInstance().getTutorInputWidgetStats().getCountWidgets() > 0) {
                    s += "Your <a href='#'>score</a> is " + UserInfo.getInstance().getTutorInputWidgetStats().getCorrectPercent() + "%";
                }
            }
            _helloInfo.setHTML(s);
        }
    }

    /**
     * Update all info fields and titles in header areas
     */
    public void setHeaderInfo() {
        CatchupMathTools.showAlert("Set Header info");
    }

    static {
        CmRpcCore.EVENT_BUS.addHandler(UserTutorWidgetStatusUpdatedEvent.TYPE, new UserTutorWidgetStatusUpdatedHandler() {
            @Override
            public void userStatsUpdate(UserTutorWidgetStats userStats) {
                __instance.updateUserTutorStats(userStats);
            }
        });
        
        CmRpcCore.EVENT_BUS.addHandler(AssignmentsUpdatedEvent.TYPE, new AssignmentsUpdatedHandler() {
            @Override
            public void assignmentsUpdated(AssignmentUserInfo assInfo) {
                UserInfo.getInstance().setAssignmentMetaInfo(assInfo);
                __instance.updateAssignmentMessage(assInfo);
            }
        });

    }
}
