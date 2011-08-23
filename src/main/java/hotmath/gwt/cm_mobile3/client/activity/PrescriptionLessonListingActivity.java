package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;

import java.util.List;


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
}
