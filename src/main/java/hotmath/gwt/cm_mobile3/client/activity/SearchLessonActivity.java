package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile3.client.view.SearchLessonView;
import hotmath.gwt.cm_mobile3.client.view.SearchLessonResourceReviewView;
import hotmath.gwt.cm_mobile_shared.client.activity.PrescriptionLessonResourceVideoActivity;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;



public class SearchLessonActivity implements SearchLessonView.Presenter {

    private PrescriptionSessionData data;
    private ClientFactory cf;

    public SearchLessonActivity(PrescriptionSessionData data) {
        this.data = data;
        this.cf = CatchupMathMobile3.__clientFactory;
    }

    @Override
    public void loadWhiteboard(ShowWorkPanel2 _showWork, String _lastPid) {
    }

    @Override
    public void setupView(SearchLessonView p) {
        p.loadLesson(data);
    }

    @Override
    public void loadResource(InmhItemData resourceItem) {
        PopupMessageBox.showMessage("Load resource: " + resourceItem);
        switch(resourceItem.getType()) {
            case REVIEW:
                loadResource_Review(resourceItem);
                break;
                
                
            case VIDEO:
                loadResource_Video(resourceItem);
                break;
                
            case PRACTICE:
                loadResource_Solution(resourceItem);
                break;
                
                default:
                    break;
        }
        
    }

    private void loadResource_Solution(InmhItemData resourceItem) {
        PrescriptionLessonResourceTutorView theView = cf.getPrescriptionLessonResourceTutorView();
        PrescriptionLessonResourceTutorActivity presenter = new PrescriptionLessonResourceTutorActivity(cf.getEventBus(), resourceItem);
        theView.setPresenter(presenter);
        cf.getEventBus().fireEvent(new LoadNewPageEvent(theView));          
    }

    private void loadResource_Review(InmhItemData resourceItem) {
        SearchLessonResourceReviewView theView = cf.getSearchLessonResourceReviewView();
        SearchLessonResourceReviewActivity presenter = new SearchLessonResourceReviewActivity(resourceItem);
        theView.setPresenter(presenter);
        cf.getEventBus().fireEvent(new LoadNewPageEvent(theView));        
    }
    
    private void loadResource_Video(InmhItemData resourceItem) {
        PrescriptionLessonResourceVideoView theView = cf.getPrescriptionLessonResourceVideoView();
        PrescriptionLessonResourceVideoActivity presenter = new PrescriptionLessonResourceVideoActivity(cf.getEventBus(),resourceItem);
        theView.setPresenter(presenter);
        cf.getEventBus().fireEvent(new LoadNewPageEvent(theView));        
    }
}


