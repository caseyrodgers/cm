package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionResourceEvent;
import hotmath.gwt.cm_mobile3.client.view.LessonChooserDialog;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class PrescriptionLessonActivity implements PrescriptionLessonView.Presenter {


    List<Integer> testQuestionAnswers;

    private com.google.gwt.event.shared.EventBus eventBus;
    
    static PrescriptionData prescriptionData;
    UserInfo userInfo;

    public PrescriptionLessonActivity(com.google.gwt.event.shared.EventBus eventBus) {
        this.eventBus = eventBus;
        this.userInfo = CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo();
        prescriptionData = CatchupMathMobileShared.getUser().getFlowAction().getPrescriptionResponse().getPrescriptionData();
    }

    @Override
    public void prepareView(final PrescriptionLessonView view) {
        view.setLesson(prescriptionData.getCurrSession());
    }

    @Override
    public void loadResource(InmhItemData resourceItem) {
        eventBus.fireEvent(new ShowPrescriptionResourceEvent(resourceItem));
    }

    @Override
    public void moveToNextLesson(final PrescriptionLessonView view) {
        int sessionNumber = userInfo.getSessionNumber();
        moveToLesson(view, sessionNumber+1);
    }

    @Override
    public void moveToPreviousLesson(final PrescriptionLessonView view) {
        int sessionNumber = userInfo.getSessionNumber();
        moveToLesson(view, sessionNumber-1);
    }
    
    static public PrescriptionData getPrescriptionData() {
        return prescriptionData;
    }
    
    public void moveToLesson(final PrescriptionLessonView view, int sessionNumber) {
        
        if(sessionNumber < 0) {
            MessageBox.showMessage("No previous lesson");
        }
        else if(sessionNumber > userInfo.getSessionCount()) {
            MessageBox.showMessage("No more lessons");  // move to next segment?
        }
            
        
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        int runId = userInfo.getRunId();
        GetPrescriptionAction action = new GetPrescriptionAction(runId, sessionNumber,  true);
        CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<PrescriptionSessionResponse>() {
            public void onSuccess(PrescriptionSessionResponse result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                
                /** install new data ..
                 * 
                 */
                prescriptionData = result.getPrescriptionData();
                userInfo.setSessionNumber(prescriptionData.getCurrSession().getSessionNumber());
                view.setLesson(prescriptionData.getCurrSession());
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error moving to lesson", caught);
                eventBus.fireEvent(new SystemIsBusyEvent(false));
            }
        });
        
    }

    @Override
    public void showLessonChooser() {
        new LessonChooserDialog(this);
    }
}
