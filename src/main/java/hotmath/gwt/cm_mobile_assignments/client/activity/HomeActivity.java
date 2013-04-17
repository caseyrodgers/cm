package hotmath.gwt.cm_mobile_assignments.client.activity;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.Item;
import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;
import hotmath.gwt.cm_mobile_assignments.client.view.HomeView;
import hotmath.gwt.cm_mobile_assignments.client.view.MainView;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler;
import com.googlecode.mgwt.ui.client.widget.base.PullArrowStandardHandler.PullActionHandler;

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
    
    private boolean failedHeader = false;
    private boolean failedFooter = false;
    private int counter;
    
    private List<Item> list = new LinkedList<Item>();

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        final HomeView display = factory.getHomeView();
        MainView mainView = factory.getMain(display, "Catchup Math Assignments (Pull to Refresh)",false);
        panel.setWidget(mainView.asWidget());

        PullArrowStandardHandler headerHandler = new PullArrowStandardHandler(display.getPullHeader(), display.getPullPanel());

        headerHandler.setErrorText("Error");
        headerHandler.setLoadingText("Loading");
        headerHandler.setNormalText("pull down");
        headerHandler.setPulledText("release to load");
        headerHandler.setPullActionHandler(new PullActionHandler() {

            @Override
            public void onPullAction(final AsyncCallback<Void> callback) {
                new Timer() {

                    @Override
                    public void run() {

                        if (failedHeader) {
                            callback.onFailure(null);

                        } else {
                            for (int i = 0; i < 5; i++) {
                                list.add(0, new Item("generated Item " + (counter + 1)));
                                counter++;
                            }
                            display.render(list);
                            display.refresh();

                            callback.onSuccess(null);

                        }
                        failedHeader = !failedHeader;

                    }
                }.schedule(1000);

            }
        });

        display.setHeaderPullHandler(headerHandler);

        PullArrowStandardHandler footerHandler = new PullArrowStandardHandler(display.getPullFooter(), display.getPullPanel());

        footerHandler.setErrorText("Error");
        footerHandler.setLoadingText("Loading");
        footerHandler.setNormalText("pull up");
        footerHandler.setPulledText("release to load");
        footerHandler.setPullActionHandler(new PullActionHandler() {

            @Override
            public void onPullAction(final AsyncCallback<Void> callback) {
                new Timer() {

                    @Override
                    public void run() {

                        if (failedFooter) {
                            callback.onFailure(null);

                        } else {
                            for (int i = 0; i < 5; i++) {
                                list.add(list.size(), new Item("generated Item " + (counter + 1)));
                                counter++;
                            }
                            display.render(list);
                            display.refresh();

                            callback.onSuccess(null);

                        }
                        failedFooter = !failedFooter;

                    }
                }.schedule(1000);

            }
        });

        display.setFooterPullHandler(footerHandler);

        display.render(list);

        panel.setWidget(display);        
        
    }

}
