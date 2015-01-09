package hotmath.gwt.cm_mobile_shared.client.data;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.background.BackgroundServerChecker;
import hotmath.gwt.cm_mobile_shared.client.event.UserLoginEvent;
import hotmath.gwt.cm_mobile_shared.client.event.UserLogoutEvent;
import hotmath.gwt.cm_mobile_shared.client.event.UserLogoutHandler;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.util.CmStorage;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.VideoPlayerWindowMobile;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * centralized wrapper for shared global data structures
 * 
 * Contains composite objects for both Quiz and Prescription data.
 * 
 * @TODO: refactor these info a better data structure. This is really a local
 *        datasource. Would be nice to be able to serialize this data at some
 *        point into local storage as well.
 * 
 * 
 * @author casey
 * 
 */
public class SharedData {

    private static UserInfo userInfo;
    private static CmProgramFlowAction flowAction;
    private static CmMobileUser __mobileUser;

    static public CmProgramFlowAction getFlowAction() {
        if (flowAction == null) {
            flowAction = new CmProgramFlowAction();
        }
        return flowAction;
    }

    static public void setFlowAction(CmProgramFlowAction flowActionIn) {
        flowAction = flowActionIn;
    }

    static public UserInfo getUserInfo() {
        return userInfo;
    }

    static public void setUserInfo(UserInfo userInfoIn) {
        userInfo = userInfoIn;
    }

    static public int getCountLessonsRemaining() {
        int cnt = SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics().size();
        for (SessionTopic topic : SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData()
                .getSessionTopics()) {
            if (topic.isComplete()) {
                cnt--;
            }
        }
        return cnt;
    }

    /**
     * Search through prescription data and return the InmhItemData that is in
     * the ordinal position of the named resource type.
     * 
     * @param type
     * @param ordinal
     */
    public static InmhItemData findInmhDataInPrescriptionByOrdinal(CmResourceType type, int ordinal) throws Exception {
        for (PrescriptionSessionDataResource dr : flowAction.getPrescriptionResponse().getPrescriptionData()
                .getCurrSession().getInmhResources()) {
            if (type.equals(dr.getType())) {
                for (int i = 0, t = dr.getItems().size(); i < t; i++) {
                    if (i == ordinal) {
                        return dr.getItems().get(i);
                    }
                }
            }
        }
        throw new Exception("No " + type + " resource at ordinal position '" + ordinal + "'");
    }

    public static String calculateCurrentLessonStatus(String topic) {
        for (PrescriptionSessionDataResource dr : flowAction.getPrescriptionResponse().getPrescriptionData()
                .getCurrSession().getInmhResources()) {
            if (CmResourceType.PRACTICE.equals(dr.getType())) {
                int viewed = 0;
                int total = 0;
                for (int i = 0, t = dr.getItems().size(); i < t; i++) {
                    InmhItemData item = dr.getItems().get(i);
                    if (item.isViewed()) {
                        viewed++;
                    }
                    total++;
                }

                return viewed + " of " + total;
            }
        }
        return "unknown status '" + topic + "'";
    }

    /**
     * Return the InmhItemData for the named resource file or null
     * 
     * @param type
     * @param file
     * @return
     * @throws Exception
     */
    public static InmhItemData findInmhDataInPrescriptionByFile(CmResourceType type, String file) throws Exception {
        for (PrescriptionSessionDataResource dr : flowAction.getPrescriptionResponse().getPrescriptionData()
                .getCurrSession().getInmhResources()) {
            if (type.equals(dr.getType())) {
                for (int i = 0, t = dr.getItems().size(); i < t; i++) {
                    if (dr.getItems().get(i).getFile().equals(file)) {
                        return dr.getItems().get(i);
                    }
                }
            }
        }
        return null;
    }

    public static int findOrdinalPositionOfResource(InmhItemData itemIn) {
        CmResourceType type = itemIn.getType();
        for (PrescriptionSessionDataResource dr : flowAction.getPrescriptionResponse().getPrescriptionData()
                .getCurrSession().getInmhResources()) {
            if (type == dr.getType()) {
                for (int i = 0, t = dr.getItems().size(); i < t; i++) {
                    InmhItemData item = dr.getItems().get(i);
                    if (item.getTitle().equals(itemIn.getTitle())) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    static public CmMobileUser getMobileUser() {
        if (__mobileUser == null) {
            Log.debug("CmMobileUser is not set");
        }
        return __mobileUser;
    }

    public static void setData(CmMobileUser result) {
        if (result == null) {
            SharedData.setMobileUser(null);
            SharedData.setUserInfo(null);
            SharedData.setFlowAction(null);

            BackgroundServerChecker.stopInstanceTimer();
        } else {
            SharedData.setMobileUser(result);
            SharedData.setUserInfo(result.getBaseLoginResponse().getUserInfo());
            SharedData.setFlowAction(result.getFlowAction());

            saveUidToLocalStorage(result.getUserId());

            BackgroundServerChecker.getInstance(result.getUserId());

            CmRpcCore.EVENT_BUS.fireEvent(new UserLoginEvent(result));
        }

        if (result != null) {
            CmRpcCore.EVENT_BUS.fireEvent(new AssignmentsUpdatedEvent(result.getAssignmentInfo()));
        }
    }

    private static void setMobileUser(CmMobileUser result) {
        __mobileUser = result;
    }

    static public void makeSureUserHasBeenRead(final CallbackOnComplete callback) {
        if (__mobileUser == null) {
            final int uid = getUidFromLocalStorage();
            if (uid != 0) {
                GetCmMobileLoginAction action = new GetCmMobileLoginAction(uid);
                CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmMobileUser>() {
                    @Override
                    public void onSuccess(CmMobileUser result) {
                        SharedData.setData(result);
                        callback.isComplete();

                        showWelcomePopup();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error("Error logging in with saved uid: " + uid, caught);
                        PopupMessageBox.showError("Could not login with saved uid: " + caught.getMessage());
                    }
                });
            } else {
                PopupMessageBox.showError("no user id saved");
                History.newItem("login");
            }
        } else {
            callback.isComplete();
        }
    }

    static private void showWelcomePopup() {
        String testName = SharedData.getUserInfo().getTestName();
        UserInfo ui = SharedData.getUserInfo();
        int runId = ui.getRunId();
        int testId = ui.getTestId();
        int segment = ui.getTestSegment();
        int segmentsTotal = ui.getProgramSegmentCount();
        int lessonNumber = ui.getSessionNumber();
        int lessonsTotal = ui.getSessionCount();
        boolean isCustom = ui.isCustomProgram();
        boolean isCustomQuiz = ui.isCustomProgram() && ui.getRunId() == 0;

        String status = null;
        if (SharedData.getFlowAction().getPlace() == CmPlace.ASSIGNMENTS_ONLY) {
            status = "Welcome to Catchup Math Assignments.";
        } else if (!isCustomQuiz) {
            String section = "";
            if (!isCustom) {
                section = "section " + segment + " of " + segmentsTotal + " of ";
            }
            status = "<p>You are in " + section + " the <b>" + testName + " </b> program.</p>";

            if (runId > 0) {
                status += "<p>You have " + lessonsTotal + " lesson" + (lessonsTotal > 1 ? "s" : "") + " to study.";
            }
        } else {
            status = "<p>You are in the <b>" + testName + "</b> program.</p>";
        }

        FlowPanel fp = new FlowPanel();
        fp.add(new HTML(status));
        fp.add(getHyperlink());

        PopupMessageBox.showMessage("Welcome " + ui.getUserName(), fp, null);
    }

    static int __lastUid;
    public static void saveUidToLocalStorage(int uid) {
        try {
            Storage store = CmStorage.getLocalStorage();
            if (store != null) {
                store.setItem("uid", Integer.toString(uid));
            }
        } catch (JavaScriptException e) {
            Log.error("Could not save uid to local storage", e);
        }
        finally {
            __lastUid = uid;
        }
    }

    public static int getUidFromLocalStorage() {
        int uid = 0;
        try {
            Storage store = CmStorage.getLocalStorage();
            if (store != null) {
                String sUid = store.getItem("uid");
                if (sUid != null) {
                    uid = Integer.parseInt(sUid);
                    __lastUid = uid;
                }
            }
        } catch (JavaScriptException e) {
            Log.error("Could not get uid from local storage", e);
        }
        return __lastUid;
    }

    static {
        CmRpcCore.EVENT_BUS.addHandler(UserLogoutEvent.TYPE, new UserLogoutHandler() {
            @Override
            public void userLogout() {
                SharedData.setData(null);
            }
        });

        CmRpcCore.EVENT_BUS.addHandler(AssignmentsUpdatedEvent.TYPE, new AssignmentsUpdatedHandler() {
            @Override
            public void assignmentsUpdated(AssignmentUserInfo info) {
                SharedData.getMobileUser().setAssignmentInfo(info);
            }
        });
    }

    static protected void showStudentHowToVideo() {
    	new VideoPlayerWindowMobile("How to use Catchup Math", "assets/teacher_videos/student-how-to/student-how-to-mobile.mp4");
	}

    static protected Hyperlink getHyperlink() {
        Hyperlink hl = new Hyperlink();
        hl.setText("Video: How to use Catchup Math");
		hl.getElement().setAttribute("style", "margin-left:25px; margin-top:10px; text-decoration:underline; color:#00A8FF; cursor:pointer;");
        ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showStudentHowToVideo();
			}
        };
		hl.addHandler(handler, ClickEvent.getType());
        return hl;
    }

}
