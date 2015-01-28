package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_core.client.flow.CmProgramFlowClientManager;
import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox.CallBack;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class PrescriptionLessonListingActivity implements PrescriptionLessonListingView.Presenter {
    
    List<Integer> testQuestionAnswers;

    private EventBus eventBus;
    
    static PrescriptionData prescriptionData;
    UserInfo userInfo;

    public PrescriptionLessonListingActivity(EventBus eventBus) {
        this.eventBus = eventBus;
        this.userInfo = SharedData.getUserInfo();
        prescriptionData = SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData();
    }

    @Override
    public void setupView(PrescriptionLessonListingView view) {
        /** if custom problem (no quiz) disable next quiz button */
        view.showNextQuizButton(!userInfo.isCustomProgram());
        view.setLessonListing(prescriptionData.getSessionTopics());
        
        if(SharedData.getUserInfo().isCustomProgram()) {
            view.hideInfoAboutNextQuiz();
        }
    }
    
    @Override
    public void loadLesson(int sessionNum) {
        ClientFactory cf = CatchupMathMobile3.__clientFactory;
        new PrescriptionLessonActivity(cf,eventBus).moveToLesson(cf.getPrescriptionLessonView(), sessionNum);
        this.eventBus.fireEvent(new LoadNewPageEvent(cf.getPrescriptionLessonView()));
    }

    @Override
    public void moveToNextFlow() {
        
        int correctPercent = SharedData.getMobileUser().getUserInfo().getCorrectPercent();
        int passPercentRequired = SharedData.getMobileUser().getUserInfo().getPassPercentRequired();

        final boolean nextSegment;
        String msg="";
        if(correctPercent >= passPercentRequired) {
            nextSegment=true;
            msg = "Are you ready for the next quiz?";
        }
        else {
            nextSegment=false; // retake
            msg = "Are you ready to retake this quiz?";
        }
        
        QuestionBox.askYesNoQuestion("Ready To Move On", msg, new CallBack() {
            @Override
            public void onSelectYes() {
                if(nextSegment) {
                    CmProgramFlowClientManager.moveToNextSegmentInProgram();
                }
                else {
                    CmProgramFlowClientManager.retakeProgramSegment();
                }
            }
        });        
    }

    
    /** return a status string used to label this lesson status */
    @Override
    public String getStatusForLesson(String lesson) {
        
        for(PrescriptionSessionDataResource pr: prescriptionData.getCurrSession().getInmhResources()) {
            if(pr.getType().equals(CmResourceType.PRACTICE)) {
                int totalRp=0;
                int completeRp=0;
                for(InmhItemData item: pr.getItems()) {
                    totalRp++;
                    if(item.isViewed()) {
                        completeRp++;
                    }
                }
                
                return completeRp + " of " + totalRp;
            }
        }
        return "";
    }
}
