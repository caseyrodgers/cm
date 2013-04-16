package hotmath.gwt.cm_mobile_assignments.client.activity;

import hotmath.gwt.cm_mobile_assignments.client.AboutPlace;
import hotmath.gwt.cm_mobile_assignments.client.AboutViewImpl;
import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AboutActivity implements Activity {
    
    private ClientFactory factory;

    public AboutActivity(AboutPlace place, ClientFactory clientFactory) {
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
        panel.setWidget(factory.getMain(new AboutViewImpl(), "About Assignments", true));
    }

}
