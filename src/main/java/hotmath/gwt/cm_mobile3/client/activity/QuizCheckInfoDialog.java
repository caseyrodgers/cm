package hotmath.gwt.cm_mobile3.client.activity;


import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowWelcomeViewEvent;
import hotmath.gwt.cm_mobile3.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.ShowFlashRequiredEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class QuizCheckInfoDialog extends DialogBox {
    private static QuizCheckInfoDialogUiBinder uiBinder = GWT.create(QuizCheckInfoDialogUiBinder.class);

    interface QuizCheckInfoDialogUiBinder extends UiBinder<Widget, QuizCheckInfoDialog> {
    }
    
    EventBus eventBus;
    CreateTestRunResponse testRunResponse;
    public QuizCheckInfoDialog(EventBus eventBus, CreateTestRunResponse testRunResponse) {
        this.eventBus = eventBus;
        this.testRunResponse = testRunResponse;
        addStyleName("quizCheckInfoDialog");
        setSize("300px","200px");
        setWidget(uiBinder.createAndBindUi(this));
        setText("Quiz Results");

        
        score.setInnerHTML(testRunResponse.getTestCorrectPercent() + "%");
        passed.setInnerHTML(testRunResponse.getPassed()?"passed":"did not pass" + ".");
        
        
        String toDo="";
        CmProgramFlowAction cpfa = testRunResponse.getNextAction();
        CmPlace place = cpfa!=null?cpfa.getPlace():CmPlace.WELCOME;
        switch(place) {
            case PRESCRIPTION:
                int lessons = testRunResponse.getNextAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics().size();
                toDo = "You have " + lessons + " lesson" + (lessons>1?"s":"") + " to view before moving on to the next segment.";
                break;
                
            case END_OF_PROGRAM:
                toDo = "You have completed this program!";
                break;
                
                
            case QUIZ:
                SharedData.setFlowAction(cpfa);
                toDo = "You will now be shown a new quiz.";
                break;
                
                
            case AUTO_ADVANCED_PROGRAM:
                toDo = " You will now be advanced to the next program.";
                break;
                
                
            case WELCOME:
                toDo = "You will now be shown the Welcome screen.";
                break;
                
            case AUTO_PLACEMENT:
                toDo = "You have been assigned to the " + cpfa.getAssignedTest() + " based on your quiz score.";
                break;
                
                default:
                    MessageBox.showError("QuizCheckInfoDialog Unknown place: " + cpfa.getPlace());
        }
        
        whatToDo.setInnerHTML(toDo);
        
        center();
        setVisible(true);
    }
    
    
    @UiField
    Button continueButton;
    
    
    @UiHandler("continueButton")
    void onContinue(ClickEvent ce) {
        /** fire standard CM flow handler */
        setVisible(false);
        removeFromParent();
        
        
        if(testRunResponse.getNextAction().getPlace() == CmPlace.AUTO_PLACEMENT) {
            /** force a new login to retrieve the newly setup program */
            reLogin();
        }
        else if(testRunResponse.getNextAction().getPlace() == CmPlace.QUIZ) {
            GetCmProgramFlowAction action = new GetCmProgramFlowAction(SharedData.getUserInfo().getUid(), FlowType.ACTIVE);
            eventBus.fireEvent(new SystemIsBusyEvent(true));
            CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmProgramFlowAction>() {
                @Override            
                public void onSuccess(CmProgramFlowAction result) {
                    eventBus.fireEvent(new SystemIsBusyEvent(false));
                    SharedData.setFlowAction(result);
                    
                    eventBus.fireEvent(new HandleNextFlowEvent(result));
                }
                @Override
                public void onFailure(Throwable caught) {
                    eventBus.fireEvent(new SystemIsBusyEvent(false));
                    Log.error("Error moving to next segment", caught);
                }
            });
        }
        else {
            eventBus.fireEvent(new HandleNextFlowEvent(testRunResponse.getNextAction()));
        }
    }
    
    
    private void reLogin() {
        new LoginActivity(CatchupMathMobile3.__clientFactory.getEventBus()).doLogin(SharedData.getUserInfo().getUid(), null,null);
    }
    
    
    
    @UiField
    Element score,passed,whatToDo;
}
