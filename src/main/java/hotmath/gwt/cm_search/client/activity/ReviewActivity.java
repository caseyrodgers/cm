package hotmath.gwt.cm_search.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_search.client.ClientFactory;
import hotmath.gwt.cm_search.client.places.ReviewPlace;
import hotmath.gwt.cm_search.client.view.ReviewView;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ReviewActivity extends AbstractActivity implements ReviewView.Presenter {

    private ReviewPlace place;
    private ClientFactory clientFactory;
    String _title = "Not Set";

    public ReviewActivity(ReviewPlace place, ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
        
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
      final ReviewView view = clientFactory.getReviewView();
      view.loadLesson("","");
        
      clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(true));
      
      GetReviewHtmlAction action = new GetReviewHtmlAction(place.getToken());
      
      CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<LessonResult>() {
          public void onSuccess(LessonResult lesRes) {
              clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
              view.loadLesson(_title, lesRes.getLesson());
          }
          
          @Override
          public void onFailure(Throwable ex) {
              clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
              ex.printStackTrace();
              Window.alert(ex.getMessage());
              Log.error("Error getting tutor", ex);
          }
      });                
      panel.setWidget(view);
    }


    
}
