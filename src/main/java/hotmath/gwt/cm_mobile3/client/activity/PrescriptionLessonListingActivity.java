package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile3.client.event.MoveToNextSegmentEvent;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonListingView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import java.util.List;


public class PrescriptionLessonListingActivity implements PrescriptionLessonListingView.Presenter {
    
    List<Integer> testQuestionAnswers;

    private com.google.gwt.event.shared.EventBus eventBus;
    
    static PrescriptionData prescriptionData;
    UserInfo userInfo;

    public PrescriptionLessonListingActivity(com.google.gwt.event.shared.EventBus eventBus) {
        this.eventBus = eventBus;
        this.userInfo = CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo();
        prescriptionData = SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData();
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
        eventBus.fireEvent(new MoveToNextSegmentEvent());
    }

    
    /** return a status string used to label this lesson status */
    @Override
    public String getStatusForLesson(String lesson) {
        
        for(PrescriptionSessionDataResource pr: prescriptionData.getCurrSession().getInmhResources()) {
            if(pr.getType().equals("practice")) {
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
