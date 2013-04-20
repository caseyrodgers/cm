package hotmath.gwt.cm_mobile_assignments.client.activity;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.Item;
import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;
import hotmath.gwt.cm_mobile_assignments.client.util.AssData;
import hotmath.gwt.cm_mobile_assignments.client.view.HomeView;
import hotmath.gwt.cm_mobile_assignments.client.view.MainView;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignmentInfo;

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

    private int counter;

    private List<Item> list = new LinkedList<Item>();

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {

        list.clear();

        loadDataIntoList(list);

        final HomeView display = factory.getHomeView();
        MainView mainView = factory.getMain(display, "Catchup Math Assignments (Pull to Refresh)", false);
        panel.setWidget(mainView.asWidget());

        PullArrowStandardHandler headerHandler = new PullArrowStandardHandler(display.getPullHeader(), display.getPullPanel());
        headerHandler.setErrorText("Error");
        headerHandler.setLoadingText("Getting assignments ..");
        headerHandler.setNormalText("pull down");
        headerHandler.setPulledText("release to load");
        headerHandler.setPullActionHandler(new MyPullAction());
        display.setHeaderPullHandler(headerHandler);

//        PullArrowStandardHandler footerHandler = new PullArrowStandardHandler(display.getPullFooter(), display.getPullPanel());
//        footerHandler.setErrorText("Error");
//        footerHandler.setLoadingText("Loading");
//        footerHandler.setNormalText("pull up");
//        footerHandler.setPulledText("release to load");
//        footerHandler.setPullActionHandler(new MyPullAction());
//        display.setFooterPullHandler(footerHandler);
        // display.render(list);
    }

    private void loadDataIntoList(List<Item> list2) {
        list.clear();
        for (StudentAssignmentInfo assInfo : AssData.getUserData().getAssignments()) {
            list.add(new Item(assInfo.getComments()));
        }
    }

    class MyPullAction implements PullActionHandler {

        public MyPullAction() {
        }

        @Override
        public void onPullAction(final AsyncCallback<Void> callback) {
            new Timer() {
                @Override
                public void run() {
                    loadDataIntoList(list);
                    //factory.getHomeView().render(list);
                    factory.getHomeView().refresh();
                    callback.onSuccess(null);
                }
            }.schedule(1000);
        }
    }
}


