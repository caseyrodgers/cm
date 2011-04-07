package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager;
import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager.Callback;
import hotmath.gwt.cm.client.ui.context.CmAutoTest.ResourceObject;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.UserProgramCompletionAction;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.AutoTestWindow;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.ui.EndOfProgramWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionContext implements CmContext {

    PrescriptionData prescriptionData;
    PrescriptionCmGuiDefinition prescriptionCm;

    int correctPercent;

    public PrescriptionContext(PrescriptionCmGuiDefinition prescriptionCm) {
        this.prescriptionCm = prescriptionCm;
    }

    public PrescriptionData getPrescriptionData() {
        return prescriptionData;
    }

    public void setPrescriptionData(PrescriptionData prescriptionData) {
        this.prescriptionData = prescriptionData;
        EventBus.getInstance().fireEvent(
                new CmEvent(EventType.EVENT_TYPE_TOPIC_CHANGED, prescriptionData.getCurrSession().getTopic()));
    }

    public void setCorrectPercent(int correctPercent) {
        this.correctPercent = correctPercent;
    }

    public int getCorrectPercent() {
        return this.correctPercent;
    }

    public void resetContext() {
    }

    IconButton _previousButton;
    IconButton _nextButton;

    public List<Component> getTools() {

        List<Component> list = new ArrayList<Component>();
        _previousButton = new IconButtonWithDropDownTooltip("cm-main-panel-prev-icon");
        _previousButton.addListener(Events.Select, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                ContextController.getInstance().doPrevious();
            }
        });

        list.add(_previousButton);
        _nextButton = new IconButtonWithDropDownTooltip("cm-main-panel-next-icon");
        _nextButton.addListener(Events.Select, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                ContextController.getInstance().doNext();
            }
        });
        list.add(_nextButton);
        return list;
    }

    public void gotoNextTopic() {

        /**
         * deal with anomaly of no missed questions .. move to the next
         * quiz/section
         */
        final boolean hasPrescription = UserInfo.getInstance().isCustomProgram()
                || !(UserInfo.getInstance().getCorrectPercent() == 100);

        // before anything can happen, the user must view the required practice
        // problems
        boolean allViewed = true;
        if (hasPrescription) {
            for (PrescriptionSessionDataResource r : prescriptionData.getCurrSession().getInmhResources()) {
                if (!r.getType().equals("practice"))
                    continue;

                for (InmhItemData id : r.getItems()) {
                    if (!id.isViewed()) {
                        allViewed = false;
                        break;
                    }
                }
            }
            /**
             * If all required solutions have not been viewed, then force user
             * to do so
             * 
             * if 'debug' parameter is on URL, then this check is skipped
             */
            if ((UserInfo.getInstance().isActiveUser() && !allViewed) && CmShared.getQueryParameter("debug") == null) {

                /**
                 * YUCK ... Expand the practice problems.
                 * 
                 * @TODO: figure better way... Perhaps add listener to the
                 *        accordion
                 */
                ((PrescriptionCmGuiDefinition) CmMainPanel.__lastInstance.cmGuiDef)._guiWidget
                        .expandResourcePracticeProblems();

                /**
                 * show an appropriate message for either RPA or RPP
                 * 
                 */
                String msg = null;
                if (!prescriptionData.getCurrSession().isSessionRpa()) {
                    msg = "Please view all required practice problem answers to the very last step.";
                } else {
                    msg = "Please complete all required practice activities.";
                }

                /**
                 * make resource area is clean
                 * 
                 */
                ((PrescriptionCmGuiDefinition) CmMainPanel.__lastInstance.cmGuiDef)._guiWidget
                        .expandResourcePracticeProblems();

                new RequiredPracticeCompleteDialog("More Practice Required", msg);

                // InfoPopupBox.display("More Practice Required", msg);

                // CatchupMathTools.showAlert("More Practice Required",msg,new
                // CmAsyncRequestImplDefault() {
                // @Override
                // public void requestComplete(String requestData) {
                // ((PrescriptionCmGuiDefinition)
                // CmMainPanel.__lastInstance.cmGuiDef)._guiWidget.expandResourcePracticeProblems();
                // }
                // });
                ContextController.getInstance().setCurrentContext(PrescriptionContext.this);

                return;
            }
        }

        doMoveNextAux(hasPrescription);
    }

    private void doMoveNextAux(boolean hasPrescription) {

        /**
         * The current session number
         * 
         */
        int sessionNumber = (hasPrescription) ? prescriptionData.getCurrSession().getSessionNumber() : 0;
        boolean thereAreNoMoreSessions = (!hasPrescription)
                || !((sessionNumber + 1) < (prescriptionData.getSessionTopics().size()));

        correctPercent = UserInfo.getInstance().getCorrectPercent();
        if (!hasPrescription || thereAreNoMoreSessions) {

            /**
             * hard exit after completion of prescription for any demo
             * 
             */
            if (UserInfo.getInstance().isDemoUser()) {
                new SampleDemoMessageWindow();
                return;
            }

            // there are no more sessions, so need to move to the 'next'.
            // Next might be the same Quiz, the next Quiz or AutoAdvance.

            int passPercentRequired = UserInfo.getInstance().getPassPercentRequired();
            if (!UserInfo.getInstance().isActiveUser()) {
                CatchupMathTools.showAlert("You are a visitor and cannot jump to the next quiz.");
                ContextController.getInstance().setCurrentContext(PrescriptionContext.this);
                return;
            }

            String msg = "";
            int testSegmentToLoad = 0;

            CmLogger.debug("Correct percent: " + correctPercent + ", " + passPercentRequired);

            if (UserInfo.getInstance().isCustomProgram() || correctPercent >= passPercentRequired) {
                /**
                 * user passed the quiz
                 * 
                 */
                handlePassedQuiz();
            } else {
                /**
                 * user did not pass quiz
                 * 
                 */
                handleNotPassedQuiz();
            }
            return;
        } else {
            sessionNumber++; // if valid..
            CmHistoryManager.getInstance().addHistoryLocation(new CmLocation(LocationType.PRESCRIPTION, sessionNumber));

            // prescriptionCm.getAsyncDataFromServer(sessionNumber);
        }
    }

    private void handleNotPassedQuiz() {
        String msg = "Are you ready to be quizzed again on this section?";
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));
        MessageBox.confirm("Ready for next Quiz?", msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED, this));
                if (be.getButtonClicked().getText().equals("Yes")) {
                    CmProgramFlowClientManager.retakeProgramSegment();
                } else {
                    ContextController.getInstance().setCurrentContext(PrescriptionContext.this);
                }
            }
        });

    }

    private void handlePassedQuiz() {
        /**
         * User has passed this section, and is ready to move to next
         * quiz/autoAdvance
         */
        if (UserInfo.getInstance().isDemoUser()) {
            new SampleDemoMessageWindow();
            return;
        }

        /**
         * are there more Quizzes in this program?
         */
        boolean areMoreSegments = UserInfo.getInstance().getTestSegment()+1 < UserInfo.getInstance().getProgramSegmentCount();
        if (areMoreSegments) {
            new PassedSectionWindow();
        } else {
            if (UserInfo.getInstance().getOnCompletion() == UserProgramCompletionAction.STOP) {
                new EndOfProgramWindow();
            } else {
                QuizCheckResultsWindow.autoAdvanceUser();
            }
        }
    }

    public void gotoPreviousTopic() {
        final int cs = prescriptionData.getCurrSession().getSessionNumber();
        if (cs < 1) {
            MessageBox.alert("On First", "No previous topics.", new Listener<MessageBoxEvent>() {
                public void handleEvent(MessageBoxEvent be) {
                    ContextController.getInstance().setCurrentContext(PrescriptionContext.this);
                }
            });
            return;
        } else {
            prescriptionCm.getAsyncDataFromServer(cs - 1);
        }
    }

    public String getContextTitle() {
        return UserInfo.getInstance().getTestName();
    }

    public String getContextSubTitle() {
        String title = getContextTitle();
        return title + "  <h2>" + prescriptionData.getCurrSession().getTopic() + "</h2>";
    }

    public String getContextHelp() {
        return "<p>Choose the resource you want to use, then click on one of its items.</p>";
    }

    public String getNextToolTipText() {
        return "Click to see your options";
    }

    public Widget getContextWidget() {
        return new Label("The Prescription widget");
    }

    public int getContextCompletionPercent() {
        // this will be in additional to current position

        if (prescriptionData == null)
            return 0;

        double cs = prescriptionData.getCurrSession().getSessionNumber();
        double ts = prescriptionData.getSessionTopics().size();

        double d = ((cs * 100) / ts);

        int i = (int) Math.round(d);
        return i;
    }

    public void doNext() {
        gotoNextTopic();
    }

    public void doPrevious() {
        gotoPreviousTopic();
    }

    public String getStatusMessage() {
        String html1 = "<ul>"
                + "<li><b>Review and Practice</b> Choose any items from the left-side menu that you find helpful. "
                + "In order to move ahead, you must view all three of the Required Practice problems all the way to "
                + "the last step.  Please use a pencil and paper or our Show-Work feature to try the "
                + "problems on your own first - that is how you really learn!" + "</li>"
                + "<li style='margin-top: 10px'>"
                + "<b>Using the Whiteboard</b> Use the keyboard or draw with your mouse to enter answers or work out "
                + "problems. Your work is saved in your account. " + "</li>" + "</ul>";

        return html1;
    }

    /**
     * Run auto testing for prescription
     * 
     * 
     * @TODO: move to external interface
     * 
     */
    public void runAutoTest() {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                runAutoTestAux();
            }

            @Override
            public void onFailure(Throwable reason) {
                reason.printStackTrace();
            }
        });
    }

    /**
     * Test the prescription.
     * 
     * If URL param test_rpp_only is set to true, the only RPP will be loaded.
     * 
     */
    public void runAutoTestAux() {
        AutoTestWindow.getInstance().addLogMessage("Testing lesson: " + 
                prescriptionData.getCurrSession().getTopic() + ", " +
                UserInfo.getInstance().getUserStatus());
        /**
         * prepare a stack of resources to run, then run them one by one
         * 
         */
        List<ResourceObject> resourcesToRun = new ArrayList<ResourceObject>();

        for (String rt : PrescriptionCmGuiDefinition._registeredResources.keySet()) {
            final String resourceType = rt;
            List<InmhItemData> resources = PrescriptionCmGuiDefinition._registeredResources.get(resourceType);
            int which = 0;
            boolean onlyRpp = CmShared.getQueryParameter("test_rpp_only") != null;
            for (final InmhItemData r : resources) {

                if (!r.getType().equals(resourceType))
                    continue;

                if (onlyRpp && r.getType().indexOf("practice") == -1)
                    continue;

                CmAutoTest.ResourceObject ro = new CmAutoTest.ResourceObject(r, which++);
                resourcesToRun.add(ro);
            }
        }

        new CmAutoTest(resourcesToRun, new CmAsyncRequestImplDefault() {

            @Override
            public void requestComplete(String requestData) {
                int cs = prescriptionData.getCurrSession().getSessionNumber();
                int ts = prescriptionData.getSessionTopics().size();
                if ((cs + 1) < ts) {
                    prescriptionCm.getAsyncDataFromServer(cs + 1);
                } else {
                    CmProgramFlowClientManager.moveToNextSegmentInProgram();
                }
            }
        });
    }

    /**
     * Called with prev/next buttons that should have their tooltips set to the
     * next/prev options.
     * 
     * Move to next topic (<n> more to go)”; or, if zero topics left: “Move to
     * next quiz”
     * 
     */
    public String getTooltipText(Direction direction, PrescriptionData prescriptionData) {

        assert prescriptionData != null;

        int pn = prescriptionData.getCurrSession().getSessionNumber();
        if (direction == Direction.PREVIOUS) {
            if (pn > 0) {
                return "Move to the previous topic ("
                        + prescriptionData.getSessionTopics().get(
                                prescriptionData.getCurrSession().getSessionNumber() - 1) + ")";
            } else {
                return "No previous topics";
            }
        } else {
            if (pn > prescriptionData.getSessionTopics().size() - 2) {
                if (UserInfo.getInstance().isCustomProgram()) {
                    return "No more lessons";
                } else {
                    return "Move to next quiz";
                }
            }

            else {
                int sn = prescriptionData.getCurrSession().getSessionNumber();
                int ts = prescriptionData.getSessionTopics().size();
                return "Move to the next topic (" + (ts - sn - 1) + " more to go)";
            }
        }
    }

    enum Direction {
        PREVIOUS, NEXT
    };

    /**
     * IconButton that has drop down tooltip that allows fully contained
     * tooltips that do not have z-order issues with flash components.
     * 
     * @author casey
     * 
     */
    class IconButtonWithDropDownTooltip extends IconButton {
        public IconButtonWithDropDownTooltip(String style) {
            super(style);

            addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
                @Override
                public void handleEvent(BaseEvent be) {
                    Direction dir = (IconButtonWithDropDownTooltip.this == _previousButton) ? Direction.PREVIOUS
                            : Direction.NEXT;
                    String tip = getTooltipText(dir, prescriptionData);

                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_CONTEXT_TOOLTIP_SHOW, tip));
                }
            });
        }
    }
}
