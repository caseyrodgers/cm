package hotmath.gwt.cm_mobile_assignments.client.activity;

import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.CmMobileAssignments;
import hotmath.gwt.cm_mobile_assignments.client.place.AssProblemPlace;
import hotmath.gwt.cm_mobile_assignments.client.place.AssignmentPlace;
import hotmath.gwt.cm_mobile_assignments.client.util.AssAlertBox;
import hotmath.gwt.cm_mobile_assignments.client.util.AssBusy;
import hotmath.gwt.cm_mobile_assignments.client.util.AssData;
import hotmath.gwt.cm_mobile_assignments.client.view.AssProblemView;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AssProblemActivity implements Activity, AssProblemView.Presenter {

    private ClientFactory factory;
    private AssProblemPlace place;

    public AssProblemActivity(AssProblemPlace place, ClientFactory clientFactory) {
        this.place = place;
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

    static private AssignmentProblem __lastProblem;

    private void loadProblem(AcceptsOneWidget panel, AssignmentProblem problem) {
        __lastProblem = problem;
        AssBusy.showBusy(false);
        AssProblemView view = factory.getAssProblemView();
        view.setPresenter(this);
        view.showProblem(problem);
        panel.setWidget(factory.getMain(view, "Assignment Problem", true));
    }    

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        
        if(__lastProblem != null && __lastProblem.getInfo().getPid().equals(place.getPid())) {
            loadProblem(panel, __lastProblem);
            return;
        }

        AssBusy.showBusy(true);
        GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(AssData.getUserData().getUid(),place.getAssignKey(), place.getPid());
        CmMobileAssignments.getCmService().execute(action, new AsyncCallback<AssignmentProblem>() {
            @Override
            public void onSuccess(AssignmentProblem problem) {
                loadProblem(panel, problem);
            }

            @Override
            public void onFailure(Throwable caught) {
                AssBusy.showBusy(false);
                Log.error("Error loading solution: " + place, caught);
                AssAlertBox.showAlert("There was a problem talking to the server");
            }
        });
        
    }

    @Override
    public void setupView(AssProblemView view) {
    }

    @Override
    public void showWhiteboard(String title) {
    }

    @Override
    public void markSolutionAsComplete() {
    }

    @Override
    public InmhItemData getItemData() {
        return new InmhItemData("practice", __lastProblem.getInfo().getPid(), __lastProblem.getInfo().getPid());
    }
    
    @Override
    public void newProblem() {
        factory.getPlaceController().goTo(new AssignmentPlace(__lastProblem.getAssignKey()));
    }

}
