package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import java.util.List;

import com.google.gwt.event.shared.EventBus;


public class PrescriptionLessonResourceVideoActivity implements PrescriptionLessonResourceVideoView.Presenter {


    List<Integer> testQuestionAnswers;

    private com.google.gwt.event.shared.EventBus eventBus;

    InmhItemData resourceItem;

    public PrescriptionLessonResourceVideoActivity(com.google.gwt.event.shared.EventBus eventBus, InmhItemData resourceItem) {
        this.eventBus = eventBus;
        this.resourceItem = resourceItem;
    }

    @Override
    public void setupView(final PrescriptionLessonResourceVideoView view) {
        String nonFlashUrl = null;
        String video = resourceItem.getFile();
        
        if(!isANumber(video) && !video.startsWith("/")) {
            
            /** must be mathtv
             * MOVE TO CORRECT PLACE!
             */
            video = "/help/flvs/mathtv/" + video + ".flv";
        }

        
        if(video.indexOf("youtube") > -1 || video.indexOf("mathtv") > -1) {
            nonFlashUrl = video.substring(0, video.indexOf(".flv"));
        }
        else {
            nonFlashUrl = "/help/flvs/tw/" + video;
        }
        
        
        view.setVideoUrlWithOutExtension(nonFlashUrl);
        view.setVideoTitle(resourceItem.getTitle());
    }
    
    private boolean isANumber(String x) {
        try {
            Integer.parseInt(x);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

}
