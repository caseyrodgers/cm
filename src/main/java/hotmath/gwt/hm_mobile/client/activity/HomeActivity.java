package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.hm_mobile.client.ClientFactory;
import hotmath.gwt.hm_mobile.client.place.HomePlace;
import hotmath.gwt.hm_mobile.client.view.HomeView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class HomeActivity extends AbstractActivity implements HomeView.Presenter {

	private final ClientFactory clientFactory;
	private HomeView view;
	private HomePlace place;
	
	

	public HomeActivity(HomePlace place, ClientFactory clientFactory) {
	    this.place = place;
		this.clientFactory = clientFactory;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		this.view = clientFactory.getHomeView();
		this.view.setPresenter(this);
		panel.setWidget(view.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);	
	}

    public HomeView getView() {
        return view;
    }

    public void setView(HomeView view) {
        this.view = view;
    }

    public HomePlace getPlace() {
        return place;
    }

    public void setPlace(HomePlace place) {
        this.place = place;
    }
}