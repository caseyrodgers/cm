package hotmath.gwt.cm_mobile_assignments.client.activity;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;
import hotmath.gwt.cm_mobile_assignments.client.util.AssData;
import hotmath.gwt.cm_mobile_assignments.client.util.AssData.CallbackWhenDataReady;
import hotmath.gwt.cm_mobile_assignments.client.view.HomeView;
import hotmath.gwt.cm_mobile_assignments.client.view.MainView;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;

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

    private List<StudentAssignmentInfo> list = new LinkedList<StudentAssignmentInfo>();

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {

        list.clear();

        loadDataIntoList(list);

        final HomeView display = factory.getHomeView();

        PullArrowStandardHandler headerHandler = new PullArrowStandardHandler(display.getPullHeader(), display.getPullPanel());
        headerHandler.setErrorText("Error");
        headerHandler.setLoadingText("Refreshing assignments ..");
        headerHandler.setNormalText("pull down to refresh");
        headerHandler.setPulledText("release to load");
        headerHandler.setPullActionHandler(new MyPullAction());
        display.setHeaderPullHandler(headerHandler);
        display.render(list);

        MainView mainView = factory.getMain(display, "Catchup Math Assignments", false);
        panel.setWidget(mainView.asWidget());
    }

    private void loadDataIntoList(List<StudentAssignmentInfo> listIn) {
        listIn.clear();
        listIn.addAll(AssData.getUserData().getAssignments());
    }
    
    class MyPullAction implements PullActionHandler {
        
        public MyPullAction() {
        }

        @Override
        public void onPullAction(final AsyncCallback<Void> callback) {
            new Timer() {
                @Override
                public void run() {
                    
                    AssData.refreshAssData(new CallbackWhenDataReady() {
                        @Override
                        public void isReady() {
                            loadDataIntoList(list);
                            factory.getHomeView().render(list);
                            factory.getHomeView().refresh();
                            callback.onSuccess(null);
                        }
                    });
                }
            }.schedule(1000);
        }
    }
}


