package hotmath.gwt.cm_mobile_shared.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LessonActivity implements LessonView.Presenter {

    LessonModel lesson;
    public LessonActivity(LessonModel lesson) {
        this.lesson = lesson;
    }
    
    @Override
    public void loadLesson(final LessonView view, final CallbackOnComplete callback) {
        
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        
        GetReviewHtmlAction action = new GetReviewHtmlAction(lesson.getLessonFile());
        
        CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<LessonResult>() {
            public void onSuccess(LessonResult lesRes) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                view.loadLesson(lesson.getLessonName(), lesRes.getLesson());
                callback.isComplete();
            }
            
            @Override
            public void onFailure(Throwable ex) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                ex.printStackTrace();
                Window.alert(ex.getMessage());
                Log.error("Error getting tutor", ex);
            }
        });                        

        
        
        
    }

}
