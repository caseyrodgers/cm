package hotmath.gwt.cm_search.client.activity;

import hotmath.gwt.cm_mobile_shared.client.activity.PrescriptionLessonResourceVideoActivity;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoViewImpl;
import hotmath.gwt.cm_search.client.ClientFactory;
import hotmath.gwt.cm_search.client.places.VideoPlace;
import hotmath.gwt.cm_search.client.view.VideoView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class VideoActivity extends AbstractActivity implements VideoView.Presenter {

    private VideoPlace place;
    private ClientFactory clientFactory;
    String _title = "Not Set";

    public VideoActivity(VideoPlace place, ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
        
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        PrescriptionLessonResourceVideoView view = new PrescriptionLessonResourceVideoViewImpl();
        PrescriptionLessonResourceVideoActivity cmActivity = new PrescriptionLessonResourceVideoActivity(eventBus, place.getResource());
        cmActivity.setupView(view);
        
        panel.setWidget((Widget)view);
        
    }
    
}
