package hotmath.gwt.cm_search.client.view;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_search.client.view.HeaderView.HeaderCallback;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TutorViewImpl extends AbstractPagePanel implements TutorView {
    
    Presenter presenter;
    TutorWrapperPanel tutor;

    interface MyUiBinder extends UiBinder<Widget, TutorViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField HTMLPanel tutorPanel;
    @UiField HeaderView headerPanel;
    
    
    public TutorViewImpl() {
        tutor = new TutorWrapperPanel(true, true,false,false,new TutorCallbackDefault(){
            @Override
            public void onNewProblem(int problemNumber) {
                tutorNewProblem();
            }
        });
        
        initWidget(uiBinder.createAndBindUi(this));
        
        tutorPanel.add(tutor);
        

        headerPanel.setCallback(new HeaderCallback() {
            @Override
            public void goBack() {
                tutorNewProblem();
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    
    /** called from external #new_problem button
     *  in the tutor template. 
     * 
     */
    private void tutorNewProblem() {
        History.back();
    }    
    
    private native String getReferrer() /*-{
        return $doc.referrer;
    }-*/;
    

    SolutionResponse lastResponse;
    ProblemNumber problem;
    
    @Override
    public void loadSolution(final String title, final SolutionResponse solution) {
        
        // this.tutorPanel.getElement().setAttribute("style", "display: none");
        this.tutorPanel.setVisible(false);
        
        this.lastResponse = solution;
        this.problem = solution.getProblem();
        
        SolutionInfo info = new SolutionInfo(problem.getPid(), solution.getTutorHtml(), solution.getSolutionData(),false);

        tutor.externallyLoadedTutor(info, getWidget(), info.getPid(), null, info.getJs(), info.getHtml(), problem.getLabel(), false, false, null);
        
        headerPanel.setHeaderTitle(title);
        
        this.tutorPanel.setVisible(true);
    }   
}
