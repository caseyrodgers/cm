package hotmath.gwt.cm_mobile_assignments.client.activity;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;
import hotmath.gwt.cm_mobile_assignments.client.view.MainView;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class HomeActivity implements Activity {
    
    private ClientFactory factory;

    public HomeActivity(HomePlace place, ClientFactory clientFactory) {
        this.factory = clientFactory;
    }

    @Override
    public String mayStop() {
        return null;
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        MainView mainView = factory.getMain(factory.getHomeView(), "Catchup Math Assignments",false);
        panel.setWidget(mainView.asWidget());
    }

}
