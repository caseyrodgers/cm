package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.data.PrescriptionData;
import hotmath.gwt.cm_tools.client.data.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.model.AutoUserAdvanced;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextChangeListener;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.NextDialog;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.AutoAdvanceUserAction;
import hotmath.gwt.shared.client.rpc.action.MarkPrescriptionLessonAsViewedAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
                new CmEvent(EventBus.EVENT_TYPE_TOPIC_CHANGED, prescriptionData.getCurrSession().getTopic()));
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
        ContextController.getInstance().addContextChangeListener(new ContextChangeListener() {
            public void contextChanged(CmContext context) {
                setHeaderButtons(_previousButton, _nextButton);
            }
        });
        List<Component> list = new ArrayList<Component>();
        _previousButton = new IconButton("cm-main-panel-prev-icon");
        _previousButton.setToolTip("Move to the previous step");
        _previousButton.addListener(Events.Select, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                // _previousButton.setEnabled(false);
                // _nextButton.setEnabled(false);
                ContextController.getInstance().doPrevious();
            }
        });

        list.add(_previousButton);
        _nextButton = new IconButton("cm-main-panel-next-icon");
        _nextButton.setToolTip("Move to the next step");
        _nextButton.addListener(Events.Select, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                ContextController.getInstance().doNext();
                // _nextButton.setEnabled(false);
                // _previousButton.setEnabled(false);
            }
        });
        list.add(_nextButton);
        return list;

    }

    public void gotoNextTopic() {

        CmMainPanel.__lastInstance._mainContent.removeAll();
        NextDialog.destroyCurrentDialog();

        // deal with anomaly of no missed questions .. move to the next quiz
        // section
        final boolean hasPrescription = !(UserInfo.getInstance().getCorrectPercent() == 100);

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
             * If all required solutions have been not been viewed, then force
             * user to do so
             * 
             * if 'debug' parameter is on URL, then this check is skipped
             */
            if ((UserInfo.getInstance().isActiveUser() && !allViewed)) {

                /**
                 * YUCK ... Expand the practice problems.
                 * 
                 * @TODO: figure better way... Perhaps add listener to the
                 *        accordion
                 */
                ((PrescriptionCmGuiDefinition) CmMainPanel.__lastInstance.cmGuiDef)._guiWidget.expandResourcePracticeProblems();
                CatchupMathTools.showAlert("View All the Steps","Please view all required practice problem answers to the very last step.",new CmAsyncRequestImplDefault() {
                    
                    @Override
                    public void requestComplete(String requestData) {
                        ((PrescriptionCmGuiDefinition) CmMainPanel.__lastInstance.cmGuiDef)._guiWidget.expandResourcePracticeProblems();
                    }
                });
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
        boolean thereAreNoMoreSessions = (!hasPrescription) || !((sessionNumber + 1) < (prescriptionData.getSessionTopics().size()));
        
        
        /** Mark this lesson as being complete
         * 
         */
        if(UserInfo.getInstance().isActiveUser())
            markLessonAsComplete(UserInfo.getInstance().getRunId(),sessionNumber);        
        

        correctPercent = UserInfo.getInstance().getCorrectPercent();
        if (!hasPrescription || thereAreNoMoreSessions) {

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
            if (correctPercent >= passPercentRequired) {
                
                // User has passed this section, and is ready to move to next quiz/autoAdvance
                if (UserInfo.getInstance().isDemoUser()) {
                    showDemoCompleteMessage();
                    return;
                }

                // are there more Quizzes in this program?
                boolean areMoreSegments = UserInfo.getInstance().getTestSegment() < UserInfo.getInstance().getTestSegmentCount();
                if (areMoreSegments) {
                    msg = "You passed this section!  You will now be shown the next quiz.";
                    CatchupMathTools.showAlert(msg, new CmAsyncRequestImplDefault() {
                        public void requestComplete(String requestData) {
                            UserInfo.getInstance().setTestSegment(UserInfo.getInstance().getTestSegment() + 1);
                            
                            CmHistoryManager.getInstance().addHistoryLocation(new CmLocation(LocationType.QUIZ, UserInfo.getInstance().getTestSegment()));
                        }
                    });
                } else {
                    msg = "You passed this section!  You will now be advanced to the next program.";
                    CatchupMathTools.showAlert(msg, new CmAsyncRequestImplDefault() {
                        public void requestComplete(String requestData) {
                            autoAdvanceUser();
                        }
                    });
                }

                // any either case, get out of here.
                return;

            } else {
                msg = "Would you like to take your next quiz?";
                testSegmentToLoad = UserInfo.getInstance().getTestSegment();
            }

            final int tstl = testSegmentToLoad;
            MessageBox.confirm("Ready for next Quiz?", msg, new Listener<MessageBoxEvent>() {
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getText().equals("Yes")) {
                        UserInfo.getInstance().setTestSegment(tstl);
                        
                        CmHistoryManager.getInstance().addHistoryLocation(new CmLocation(LocationType.QUIZ, UserInfo.getInstance().getTestSegment()));
                        //CatchupMath.getThisInstance().showQuizPanel();
                    } else {
                        ContextController.getInstance().setCurrentContext(PrescriptionContext.this);
                    }
                }
            });
            return;
        } else {
            sessionNumber++; // if valid..
            CmHistoryManager.getInstance().addHistoryLocation(new CmLocation(LocationType.PRESCRIPTION, sessionNumber));
            
            // prescriptionCm.getAsyncDataFromServer(sessionNumber);
        }
    }
    
    
    /** Mark this user as having completed this lesson 
     * 
     * @param runId
     * @param session
     */
    private void markLessonAsComplete(final int runId, final int session) {
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new MarkPrescriptionLessonAsViewedAction(prescriptionData.getCurrSession().getTopic(), runId, session), new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData userAdvance) {
                Log.info("MarkPrescriptionLessonAsViewedAction complete: " + userAdvance);
            }

            @Override
            public void onFailure(Throwable caught) {
                CatchupMathTools.setBusy(false);
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }

    /**
     * Auto Advance the user to the next program
     * 
     */
    private void autoAdvanceUser() {

        CatchupMathTools.setBusy(true);

        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new AutoAdvanceUserAction(UserInfo.getInstance().getUid()), new AsyncCallback<AutoUserAdvanced>() {
            @Override
            public void onSuccess(AutoUserAdvanced userAdvance) {

                CatchupMathTools.setBusy(false);

                String msg = "<p class='completed'>You have completed this program!</p>"
                        + "<p class='advanced-to'>You will now be advanced to:" + "<div class='plan'><b>"
                        + userAdvance.getProgramTitle() + "</div>" + "</p>";

                Window w = new Window();
                w.setClosable(false);
                w.setStyleName("auto-advance-window");
                w.setHeight(200);
                w.setWidth(350);
                w.setHeading("Congratulations!");
                Html html = new Html(msg);
                w.add(html);
                Button btnOk = new Button("OK");
                btnOk.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    public void componentSelected(ButtonEvent ce) {
                        com.google.gwt.user.client.Window.Location.reload();
                    }
                });
                w.getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
                w.addButton(btnOk);
                w.setVisible(true);
            }

            @Override
            public void onFailure(Throwable caught) {
                CatchupMathTools.setBusy(false);
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }

    private void showDemoCompleteMessage() {
        String msg = "<p>Thank you for trying Catchup Math for a Pre-algebra Session.</p>  "
                + "<p>Please visit our <a href='http://catchupmath.com/schools.html'>Schools</a>, <a href='http://catchupmath.com/colleges.html'>Colleges</a>, or <a href='http://catchupmath.com/students.html'>Students</a> pages.</p>";

        Window w = new Window();
        w.setClosable(false);
        w.setStyleName("demo-complete-window");
        w.setHeight(200);
        w.setWidth(350);
        w.setHeading("Thank You");
        Html html = new Html(msg);
        w.add(html);
        w.setVisible(true);
    }

    public void gotoPreviousTopic() {

        NextDialog.destroyCurrentDialog();

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

    /**
     * Called with prev/next buttons that should have their tooltips set to the
     * next/prev options.
     * 
     * Move to next topic (<n> more to go)”; or, if zero topics left: “Move to
     * next quiz”
     * 
     */
    public void setHeaderButtons(IconButton prevBtn, IconButton nextBtn) {

        assert prescriptionData != null;

        prevBtn.setEnabled(true);
        nextBtn.setEnabled(true);
        int pn = prescriptionData.getCurrSession().getSessionNumber();
        if (pn > 0)
            prevBtn.setToolTip("Move to the previous topic ("
                    + prescriptionData.getSessionTopics().get(prescriptionData.getCurrSession().getSessionNumber() - 1)
                    + ")");
        else {
            prevBtn.setToolTip("No previous topics");
        }

        if (pn > prescriptionData.getSessionTopics().size() - 2)
            nextBtn.setToolTip("Move to next quiz");

        else {
            int sn = prescriptionData.getCurrSession().getSessionNumber();
            int ts = prescriptionData.getSessionTopics().size();
            nextBtn.setToolTip("Move to the next topic (" + (ts - sn - 1) + " more to go)");
        }
    }

    public String getStatusMessage() {
        String html1 = "<ul>"
                + "<li><b>Review and Practice</b> Choose any items from the left-side menu that you find helpful. "
                + "In order to move ahead, you must view all three of the Required Practice problems all the way to "
                + "the last step.  Please use a pencil and paper or our Show-Work feature to try the "
                + "problems on your own first - that is how you really learn!"
                + "</li>"
                + "<li>"
                + "<b>Enter Your Answer</b> You can type on the keyboard as well as draw with your mouse. "
                + " The whiteboard saves your attempts at solving the practice problems so you can discuss later "
                + " with your teacher or tutor or review on your own.  Your teacher may prefer that you show your work in a notebook. "
                + "</li>" + "</ul>";

        return html1;
    }

    public void runAutoTest() {
//        int timeToWait = 1;
//        for (final PrescriptionSessionDataResource r : prescriptionData.getCurrSession().getInmhResources()) {
//            try {
//                Timer timer = new Timer() {
//                    public void run() {
//                        final String resourceType = r.getType();
//
//                        AutoTestWindow.getInstance().addLogMessage("Testing resource: " + resourceType);
//
//                        ((PrescriptionCmGuiDefinition) CmMainPanel.__lastInstance.cmGuiDef)._guiWidget.expandResourceType(resourceType);
//
//                        // now click on each resource
//                        int timeToWait1 = 1;
//                        for (Component c : ((PrescriptionCmGuiDefinition) CmMainPanel.__lastInstance.cmGuiDef)._guiWidget.getItems()) {
//                            if (c instanceof ResourceContentPanel) {
//                                ResourceContentPanel cp = (ResourceContentPanel) c;
//                                if (!cp.resourceList.getResourceData().getType().equals(resourceType))
//                                    continue;
//                                final ResourceList rl = (ResourceList) cp.getItems().get(0);
//
//                                for (final ResourceModel rm : rl.getStore().getModels()) {
//
//                                    Timer timer1 = new Timer() {
//                                        public void run() {
//                                            AutoTestWindow.getInstance().addLogMessage(
//                                                    "Testing: " + resourceType + ", " + rm.getItem());
//
//                                            rl.showResource(rm.getItem());
//                                        }
//                                    };
//                                    timer1.schedule(timeToWait1);
//                                    timeToWait1 += AutoTestWindow.getInstance().getTimeForSingleResource();
//                                }
//                            }
//                        }
//                    }
//                };
//                timer.schedule(timeToWait);
//                timeToWait += AutoTestWindow.getInstance().getTimeForSingleResourceType();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * Move to next test, prescription or completion
//         */
//        Timer timer = new Timer() {
//            public void run() {
//                int cs = prescriptionData.getCurrSession().getSessionNumber();
//                int ts = prescriptionData.getSessionTopics().size();
//                if ((cs + 1) < ts) {
//                    AutoTestWindow.getInstance().addLogMessage("Moving to Lesson: " + cs + 1);
//                    prescriptionCm.getAsyncDataFromServer(cs + 1);
//                } else {
//                    int nextSegment = UserInfo.getInstance().getTestSegment();
//                    if (nextSegment < UserInfo.getInstance().getTestSegmentCount()) {
//                        nextSegment += 1;
//                        AutoTestWindow.getInstance().addLogMessage("Testing Quiz: " + nextSegment);
//                        UserInfo.getInstance().setTestSegment(nextSegment);
//                        CatchupMath.getThisInstance().showQuizPanel();
//                    } else {
//                        AutoTestWindow.getInstance().addLogMessage("Auto Test has completed at " + nextSegment + "!");
//                    }
//                }
//            }
//        };
//        timer.schedule(AutoTestWindow.getInstance().getTimeForSingleLesson());
    }
}
