package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;


public class PrescriptionLessonResourceReviewActivity implements PrescriptionLessonResourceReviewView.Presenter {


    List<Integer> testQuestionAnswers;

    private com.google.gwt.event.shared.EventBus eventBus;
    
    InmhItemData resourceItem;

    public PrescriptionLessonResourceReviewActivity(com.google.gwt.event.shared.EventBus eventBus, InmhItemData resourceItem) {
        this.eventBus = eventBus;
        this.resourceItem = resourceItem;
    }

    @Override
    public void setupView(final PrescriptionLessonResourceReviewView view) {
        
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        GetReviewHtmlAction action = new GetReviewHtmlAction(resourceItem.getFile());
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<LessonResult>() {
            @Override
            public void onSuccess(LessonResult result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                view.setReviewHtml(result.getLesson());
            }

            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                caught.printStackTrace();               
            }
        });
        view.setTitle(resourceItem.getTitle());
    }
}
