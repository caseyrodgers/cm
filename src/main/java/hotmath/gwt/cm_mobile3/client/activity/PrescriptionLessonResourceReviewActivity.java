package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionResourceEvent;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceReviewViewImpl;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class PrescriptionLessonResourceReviewActivity implements PrescriptionLessonResourceReviewView.Presenter {


    List<Integer> testQuestionAnswers;

    private EventBus eventBus;
    
    InmhItemData resourceItem;

    public PrescriptionLessonResourceReviewActivity(EventBus eventBus, InmhItemData resourceItem) {
        this.eventBus = eventBus;
        this.resourceItem = resourceItem;
        
        setupJsniHooks(this);
    }
    
    
    private void doResourceLoad(String type, String file) {
        // MessageBox.showError("Flie: " + file);'
        ClientFactory cf = CatchupMathMobile3.__clientFactory;
        InmhItemData newItem = new InmhItemData(CmResourceType.mapResourceType(type), file,"");
        cf.getEventBus().fireEvent(new ShowPrescriptionResourceEvent(newItem));        
    }

    private native void setupJsniHooks(PrescriptionLessonResourceReviewActivity x) /*-{
        $wnd.doLoadResource_Gwt = function(type,file) {
            x.@hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceReviewActivity::doResourceLoad(Ljava/lang/String;Ljava/lang/String;)(type,file);
        }
    }-*/;

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
        view.setHeaderTitle(resourceItem.getTitle());
    }


    @Override
    public void loadLesson(final PrescriptionLessonResourceReviewViewImpl view, final boolean isSpanish, final CallbackOnComplete callback) {
        
        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        
        GetReviewHtmlAction action = new GetReviewHtmlAction(resourceItem.getFile(),isSpanish);
        
        CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<LessonResult>() {
            public void onSuccess(LessonResult lesRes) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                view.loadLesson(resourceItem.getTitle(), lesRes.getLesson());
                callback.isComplete();
            }
            
            @Override
            public void onFailure(Throwable ex) {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                ex.printStackTrace();
                Log.error("Error reading lesson from server", ex);
            }
        });                                
    }
}
