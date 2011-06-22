package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.hm_mobile.client.ClientFactory;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.model.ProblemNumber;
import hotmath.gwt.hm_mobile.client.place.TutorViewPlace;
import hotmath.gwt.hm_mobile.client.rpc.GetSolutionAction;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.TutorView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class TutorViewActivity extends AbstractActivity implements TutorView.Presenter {
	private ClientFactory clientFactory;
	private String token;
	

	public TutorViewActivity(TutorViewPlace place, ClientFactory clientFactory) {
		this.token = place.getToken();
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		BookListView view = clientFactory.getBookListView();
		containerWidget.setWidget(view.asWidget());
		view.showBookList(null);		
	}

	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
		return null; // "Are you sure you want to stop this Yoga Class?";
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	@Override
    public void getTutor(String pid) {
		GetSolutionAction action = new GetSolutionAction(pid);
		HmMobile.getCmService().execute(action,new AsyncCallback<SolutionResponse>() {
			public void onSuccess(SolutionResponse solutionResponse) {
				clientFactory.getTutorView().loadSolution(new ProblemNumber(), solutionResponse);
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
				Window.alert(arg0.getMessage());
			}
		});
    }
}