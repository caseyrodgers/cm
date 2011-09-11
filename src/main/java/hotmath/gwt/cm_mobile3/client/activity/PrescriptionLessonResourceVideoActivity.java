package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import java.util.List;


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
        String video = resourceItem.getFile();
        String nonFlashUrl = "/video_convert/flvs/tw/" + video;
        view.setVideoUrlWithOutExtension(nonFlashUrl);
        view.setVideoTitle(resourceItem.getTitle());
    }

}