package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionLessonViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowQuizViewEvent;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class PrescriptionLessonListingActivity implements PrescriptionLessonListingView.Presenter {
    
    List<Integer> testQuestionAnswers;

    private com.google.gwt.event.shared.EventBus eventBus;
    
    static PrescriptionData prescriptionData;
    UserInfo userInfo;

    public PrescriptionLessonListingActivity(com.google.gwt.event.shared.EventBus eventBus) {
        this.eventBus = eventBus;
        this.userInfo = CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo();
        prescriptionData = CatchupMathMobileShared.getUser().getFlowAction().getPrescriptionResponse().getPrescriptionData();
    }

    @Override
    public void setupView(PrescriptionLessonListingView view) {
        view.setLessonListing(prescriptionData.getSessionTopics());
    }
    
    @Override
    public void loadLesson(int sessionNum) {
        ClientFactory cf = CatchupMathMobile3.__clientFactory;
        new PrescriptionLessonActivity(cf,eventBus).moveToLesson(cf.getPrescriptionLessonView(), sessionNum);
        this.eventBus.fireEvent(new LoadNewPageEvent(cf.getPrescriptionLessonView()));
    }

    @Override
    public void moveToNextSegment() {
        GetCmProgramFlowAction action = new GetCmProgramFlowAction(SharedData.getUserInfo().getUid(), FlowType.NEXT);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmProgramFlowAction>() {
            @Override            
            public void onSuccess(CmProgramFlowAction result) {
                SharedData.setFlowAction(result);
                
                switch(result.getPlace()) {
                case QUIZ:
                    SharedData.getUserInfo().setTestId(result.getQuizResult().getTestId());
                    eventBus.fireEvent(new ShowQuizViewEvent());
                    break;
                case PRESCRIPTION:
                    eventBus.fireEvent(new ShowPrescriptionLessonViewEvent());
                    break;
                    
                case AUTO_ADVANCED_PROGRAM:
                    Window.alert("Auto Advance Program");
                    break;

                default:
                    Window.alert("Unknown place: " + result);
                    break;
                }
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error moving to next segment", caught);
            }
        });
    }
}
