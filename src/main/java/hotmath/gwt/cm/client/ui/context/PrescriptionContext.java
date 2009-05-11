package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.data.PrescriptionData;
import hotmath.gwt.cm.client.data.PrescriptionSessionDataResource;
import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.CmMainPanel;
import hotmath.gwt.cm.client.ui.ContextController;
import hotmath.gwt.cm.client.ui.NextDialog;
import hotmath.gwt.cm.client.ui.NextPanelInfoImplDefault;
import hotmath.gwt.cm.client.util.UserInfo;

import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
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

    public List<Component> getTools() {
        return null;
    }

    public void gotoNextTopic() {

        NextDialog.destroyCurrentDialog();

        // deal with anomoly of no missed question .. move to the next quiz
        // section
        boolean hasPrescription = !(UserInfo.getInstance().getCorrectPercent() == 100);

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
            if (UserInfo.getInstance().isActiveUser() && !allViewed) {
                CatchupMath.showAlert("Please view the Required Practice problems before moving forward.");
                ContextController.getInstance().setCurrentContext(PrescriptionContext.this);
                
                /** YUCK ... 
                 *   Expand the practice problems.
                 *   @TODO: figure better way... Perhaps add listener to the accordian
                 */
                ((PrescriptionCmGuiDefinition)CmMainPanel.__lastInstance.cmGuiDef)._guiWidget.expandResourcePracticeProblems();
                CmMainPanel.__lastInstance.layout();
                return;
            }

        }
        int cs = (hasPrescription)?prescriptionData.getCurrSession().getSessionNumber():0;
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
                    msg = "You passed this section!   Would you like to take your next quiz section?";
                    testSegmentToLoad = UserInfo.getInstance().getTestSegment() + 1;
                } else {
                    msg = "Would you like to re-take quiz section #" + currSeg;
                    testSegmentToLoad = UserInfo.getInstance().getTestSegment();
                }
                msg += " (pass percent: " + correctPercent + "%)";

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
        String title = CatchupMath.getThisInstance().getProgramInfo().getProgramName();
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
            nextBtn.setToolTip("Move to next test segment");

        else
            nextBtn.setToolTip("Move to the next topic ("
                    + prescriptionData.getSessionTopics().get(prescriptionData.getCurrSession().getSessionNumber() + 1)
                    + ")");
    }

    class NextPanelInfoImplPrescription extends NextPanelInfoImplDefault {

        public void doNext() {
            CatchupMath.showAlert("Do next on pres panel");
        }

        public Widget getNextPanelWidget() {

            boolean areMore = areMoreSessions();

            int sn = prescriptionData.getCurrSession().getSessionNumber();
            LayoutContainer lc = new LayoutContainer();
            lc.setStyleName("prescription-next-panel");

            String html = "";
            if (false) {
                html = "<p>You have completed the required review and practice for this topic.  "
                        + "However, you may continue working with it, or return later.</p>";
                lc.add(new HTML(html));
            } else {
                html = "<p style='margin-bottom: 10px'>You have not completed the required review and practice for this topic.</p> ";
                lc.add(new HTML(html));
            }

            lc.add(new HTML("<p>Here are your options: <ul>"));

            if (areMore) {
                lc.add(new HTML("<li>"));
                Anchor a = new Anchor("Next Topic");
                a.setTitle("Move to the next topic ("
                        + prescriptionData.getSessionTopics().get(
                                prescriptionData.getCurrSession().getSessionNumber() + 1) + ")");
                a.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        gotoNextTopic();
                    }
                });
                lc.add(a);
                lc.add(new HTML("</li>"));
            }
            if (sn > 0) {
                lc.add(new HTML("<li>"));
                Anchor a = new Anchor("Prevous Topic");
                a.setTitle("Move to the previous topic ("
                        + prescriptionData.getSessionTopics().get(
                                prescriptionData.getCurrSession().getSessionNumber() - 1) + ")");
                a.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        gotoPreviousTopic();
                    }
                });
                lc.add(a);
                lc.add(new HTML("</li>"));
            }
            if (true) {
                lc.add(new HTML("<li>"));
                Anchor a = new Anchor("Next Quiz Segment");
                a.setTitle("Move to the Next Quiz Segment");
                a.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        CatchupMath.getThisInstance().showQuizPanel();
                    }
                });
                lc.add(a);
                lc.add(new HTML("</li>"));
            }
            lc.add(new HTML("</ul>"));

            return lc;
        }

        private boolean areMoreSessions() {
            // are we done
            if (prescriptionData == null)
                return false;

            int sn = prescriptionData.getCurrSession().getSessionNumber();
            int st = prescriptionData.getSessionTopics().size();
            boolean isDone = (sn >= (st - 1));

            return !isDone;
        }

        public List<Widget> getNextPanelActions() {
            super.getNextPanelActions().clear();

            boolean areMore = areMoreSessions();

            Button btn = new Button("Next Quiz", new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    CatchupMath.getThisInstance().showQuizPanel();
                }
            });
            btn.setToolTip("Take the next Quiz Segment");
            super.getNextPanelActions().add(btn);

            if (prescriptionData.getCurrSession().getSessionNumber() > 0) {
                btn = new Button("Previous Topic", new SelectionListener<ButtonEvent>() {
                    public void componentSelected(ButtonEvent ce) {
                        gotoPreviousTopic();
                    }
                });
                btn.setToolTip("Move to the previous topic ("
                        + prescriptionData.getSessionTopics().get(
                                prescriptionData.getCurrSession().getSessionNumber() - 1) + ")");
                super.getNextPanelActions().add(btn);
            }

            if (areMore) {
                btn = new Button("Next Topic", new SelectionListener<ButtonEvent>() {
                    public void componentSelected(ButtonEvent ce) {
                        gotoNextTopic();
                    }
                });
                super.getNextPanelActions().add(btn);
                btn.setToolTip("Move to the next topic ("
                        + prescriptionData.getSessionTopics().get(
                                prescriptionData.getCurrSession().getSessionNumber() + 1) + ")");
            }

            return super.getNextPanelActions();
        }

        public String getNextPanelToolTip() {
            return "Move to the next topic in your prescription";
        }
    }

    public String getStatusMessage() {
        int currSess = prescriptionData.getCurrSession().getSessionNumber();
        int totSess = prescriptionData.getSessionTopics().size();

        int totSegs = UserInfo.getInstance().getTestSegmentCount();
        int seg = UserInfo.getInstance().getTestSegment();
        String name = UserInfo.getInstance().getTestName();

        int perComplete = UserInfo.getInstance().getCorrectPercent();

        return "Topic " + (currSess + 1) + " of " + totSess + ", Section " + seg + " of " + totSegs + ", "
                + perComplete + "% correct";
    }

}
