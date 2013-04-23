package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.place.AssignmentPlace;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class AssProblemViewImpl extends BaseComposite implements AssProblemView {
    
    ScrollPanel scroll = new ScrollPanel();
    TutorWrapperPanel tutor;
    private AssignmentProblem problem;
    public AssProblemViewImpl() {
        tutor = new TutorWrapperPanel(true, true, true, false, new TutorCallbackDefault());
        scroll.setWidget(tutor);
        initWidget(scroll);
    }

    @Override
    public void showProblem(AssignmentProblem problem) {
        this.problem = problem;
        String context = null; // problem.getInfo().getContext().getContextJson();
        tutor.externallyLoadedTutor(problem.getInfo(), (Widget)this, problem.getInfo().getPid(),null, problem.getInfo().getJs(), problem.getInfo().getHtml(), problem.getInfo().getPid(), false, false, context);
    }
    
    @Override
    public Place getBackPlace() {
        return new AssignmentPlace(problem.getAssignKey());
    }

}
