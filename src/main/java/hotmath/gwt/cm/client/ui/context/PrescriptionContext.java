package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager;
import hotmath.gwt.cm.client.ui.context.CmAutoTest.ResourceObject;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_tools.client.ui.AutoTestWindow;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

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
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_TOPIC_CHANGED, prescriptionData.getCurrSession().getTopic()));
    }

    public void setCorrectPercent(int correctPercent) {
        this.correctPercent = correctPercent;
    }

    public int getCorrectPercent() {
        return this.correctPercent;
    }

    public void resetContext() {
    }

    IconButton _chooseButton;
    HTML       _buttonText;

    public List<Widget> getTools() {
        
        List<Widget> list = new ArrayList<Widget>();
        
        _buttonText = new HTML();
        _buttonText.setText(" ");
        _buttonText.addStyleName("cm-main-panel-prev-next-text");
        //_buttonText.setEnabled(true);
        
        list.add(_buttonText);
        //_buttonText.enable();
        
        
        _chooseButton = new IconButtonWithTooltip("cm-main-panel-choose-icon");
        _chooseButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                PrescriptionLessonChooserDialog.getSharedInstance().showDialog(prescriptionData);
            }
        });
        list.add(_chooseButton);

        return list;
    }

    public void gotoNextTopic() {
        CmMessageBox.showAlert("Prescription Context doNext should not be called");
    }



    public void gotoPreviousTopic() {
        final int cs = prescriptionData.getCurrSession().getSessionNumber();
        if (cs < 1) {
            CmMessageBox.showAlert("On First", "No previous topics.", new CallbackOnComplete() {
                @Override
                public void isComplete() {
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
        String html1 = "<ul><li>" 
        		+ "<b>Review and Practice</b> Choose any items on the left-side menu that you find helpful. "
        		+ "You must view all the Required Problems to the last step. Use pencil and paper or our Whiteboard "
                + "to work out the answers. Some problems prompt you to enter your answer and others do not. If you "
        		+ "enter your answer correctly, you needn't click through our solution steps."
                + "</li><li style='margin-top: 10px'>"
                + "<b>Using the Whiteboard</b> Use the keyboard or draw with your mouse to enter answers or work out "
                + "problems. Your work is saved in your account. "
                + "</li></ul>";

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

                if (onlyRpp && r.getType() == CmResourceType.PRACTICE)
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
     * Called from prev/next buttons.
     * 
     * Returns the standard or dynamic tooltip used.
     * 
     */
    public String getTooltipText(Direction direction, PrescriptionData prescriptionData) {
        return "Choose the next topic or quiz";
    }

    enum Direction {
        PREVIOUS, NEXT
    };


    /**
     * IconButton that has text tooltip
     * 
     * @author bob
     * 
     */
    class IconButtonWithTooltip extends IconButton {
        public IconButtonWithTooltip(String style) {
            super(style);
            setToolTip("Choose the next topic or quiz");
        }
    }

}
