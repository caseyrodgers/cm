package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.data.PrescriptionData;
import hotmath.gwt.cm.client.data.PrescriptionSessionDataResource;
import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.CmMainPanel;
import hotmath.gwt.cm.client.ui.ContextChangeListener;
import hotmath.gwt.cm.client.ui.ContextController;
import hotmath.gwt.cm.client.ui.NextDialog;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.IconButton;
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
        _previousButton = new IconButton();
        _previousButton.setStyleName("cm-main-panel-prev-icon");
        _previousButton.setSize(60, 30);
        _previousButton.setToolTip("Move to the previous step");
        _previousButton.addListener(Events.Select, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                // _previousButton.setEnabled(false);
                // _nextButton.setEnabled(false);
                ContextController.getInstance().doPrevious();
            }
        });

        list.add(_previousButton);
        _nextButton = new IconButton();
        _nextButton.setStyleName("cm-main-panel-next-icon");
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

        // deal with anomoly of no missed question .. move to the next quiz
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
            /** If all required solutions have been not been viewed, then force user to do so
             * 
             *  if 'debug' parameter is on URL, then this check is skipped
             */
            if (CmShared.getQueryParameter("debug") == null && (UserInfo.getInstance().isActiveUser() && !allViewed)) {
                
                /**
                 * YUCK ... Expand the practice problems.
                 * 
                 * @TODO: figure better way... Perhaps add listener to the
                 *        accordian
                 */
                ((PrescriptionCmGuiDefinition) CmMainPanel.__lastInstance.cmGuiDef)._guiWidget.expandResourcePracticeProblems();
                CmMainPanel.__lastInstance.layout();
                
                CatchupMath.showAlert("View All the Steps", "Please view all required practice problem answers to the very last step.");              
                ContextController.getInstance().setCurrentContext(PrescriptionContext.this);

                return;
            }
        }
        
        doMoveNextAux(hasPrescription);
    }
    
    
    private void doMoveNextAux(boolean hasPrescription) {
        
        int cs = (hasPrescription) ? prescriptionData.getCurrSession().getSessionNumber() : 0;
        int totSegs = UserInfo.getInstance().getTestSegmentCount();
        int correctPercent = UserInfo.getInstance().getCorrectPercent();
        int PASS_PERCENT = 80;
        if (!hasPrescription || (cs + 2) > prescriptionData.getSessionTopics().size()) {
            int currSeg = UserInfo.getInstance().getTestSegment();
            // are there more segments?
            if (correctPercent < PASS_PERCENT || currSeg < totSegs) {

                if (!UserInfo.getInstance().isActiveUser()) {
                    CatchupMath.showAlert("You are a visitor and cannot jump to the next test.");
                    ContextController.getInstance().setCurrentContext(PrescriptionContext.this);
                    return;
                }

                String msg = "";
                int testSegmentToLoad = 0;
                if (correctPercent > PASS_PERCENT) {
                    msg = "You passed this section!  You will now be shown the next quiz.";
                    CatchupMath.showAlert(msg,new CmAsyncRequestImplDefault() {
                        public void requestComplete(String requestData) {
                            UserInfo.getInstance().setTestSegment(UserInfo.getInstance().getTestSegment() + 1);
                            CatchupMath.getThisInstance().showQuizPanel();
                        }
                    });
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
                            CatchupMath.getThisInstance().showQuizPanel();
                        } else {
                            ContextController.getInstance().setCurrentContext(PrescriptionContext.this);
                        }
                    }
                });
            } else {
                // there are no more segments
                MessageBox.alert("Congratulations!",
                        "You have completed all " + totSegs + " sections of this program!",
                        new Listener<MessageBoxEvent>() {
                            public void handleEvent(MessageBoxEvent be) {
                                ContextController.getInstance().setCurrentContext(PrescriptionContext.this);
                            }
                        });
            }
            return;
        } else {
            cs++; // if valid..
            prescriptionCm.getAsyncDataFromServer(cs);
        }
    }

    /**
     * Return the current pass percent for the current quiz
     * 
     * @return
     */
    private int getPassPercent() {
        return 10;
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
     * Move to next topic (<n> more to go)”; or, if zero topics left: “Move to next quiz”
     * 
     */
    public void setHeaderButtons(IconButton prevBtn, IconButton nextBtn) {
        

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
            nextBtn.setToolTip("Move to the next topic (" + (ts-sn-1) + " more to go)");
        }
    }

    public String getStatusMessage() {
        int currSess = prescriptionData.getCurrSession().getSessionNumber();
        int totSess = prescriptionData.getSessionTopics().size();

        int totSegs = UserInfo.getInstance().getTestSegmentCount();
        int seg = UserInfo.getInstance().getTestSegment();
        String name = UserInfo.getInstance().getTestName();

        int perComplete = UserInfo.getInstance().getCorrectPercent();

        return "Topic: " + prescriptionData.getCurrSession().getTopic() + " (" + (currSess + 1) + " of " + totSess + "),  Quiz: " + seg + " of " + totSegs; 
    }

}
