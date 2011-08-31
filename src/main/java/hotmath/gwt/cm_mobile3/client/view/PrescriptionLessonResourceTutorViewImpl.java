package hotmath.gwt.cm_mobile3.client.view;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class PrescriptionLessonResourceTutorViewImpl extends AbstractPagePanel implements PrescriptionLessonResourceTutorView {

	ProblemNumber problem;
	
    SimplePanel tutorPanel;
    String _title;
    
    static {
        setupJsniStatic();
    }
    
	public PrescriptionLessonResourceTutorViewImpl() {
	    tutorPanel = new SimplePanel();
	    initWidget(tutorPanel);
	    setStyleName("prescriptionLessonResourceTutorViewImpl");
	    
	    setupJsniInstance(this);
	}

	Presenter presenter;

	@Override
    public void setPresenter(Presenter p) {
		presenter = p;
		p.setupView(this);
    }

	@Override
    public void loadSolution(final SolutionResponse solution) {
		this.problem = solution.getProblem();

		
		tutorPanel.clear();
		tutorPanel.add(new HTML(solution.getTutorHtml()));
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				initializeTutor(PrescriptionLessonResourceTutorViewImpl.this, problem.getPid(), solution.getSolutionData(), problem.getProblem(), false, false);
			}
		});
    }
	
	@Override
	public ProblemNumber getLoadedProblem() {
		return problem;
	}
	
	/** Scroll to bottom of scroll panel 
	 * 
	 * */
	static private native void scrollToBottom(int top)  /*-{
	    $wnd.scrollTo(0,top);
    }-*/;
	
	
	/** called from external #new_problem button
	 *  in the tutor template. 
	 * 
	 */
	static private void tutorNewProblem() {
		Controller.navigateBack();
	}
	
	private void showWhiteboard_Gwt() {
	    presenter.showWhiteboard();
	}
	
	
	/** initialize external tutor JS/HTML and provide
	 *  glue between external JS methods and GWT.
	 *  
	 */
    private native void initializeTutor(PrescriptionLessonResourceTutorViewImpl instance, String pid, String solutionDataJs, String title, boolean hasShowWork,boolean shouldExpandSolution) /*-{
          $wnd.TutorManager.initializeTutor(pid, solutionDataJs,title,hasShowWork,shouldExpandSolution);
          
          // Customize Tutor HTML for Mobile 
          var tutorFooter = $doc.getElementById('steps_tail');
          if(tutorFooter) {
              try {
                 var showWhiteboard = $doc.createElement("button");
                 showWhiteboard.className = 'sexybutton';
                 showWhiteboard.id = 'show_whiteboard_button';
                 showWhiteboard.innerHTML = "<span><span>Whiteboard</span></span>";
                 showWhiteboard.onclick = function() {
                    instance.@hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl::showWhiteboard_Gwt()();};
                 tutorFooter.appendChild(showWhiteboard);
                 
                 
                 var newProblem = $doc.getElementById('new_problem');
                 if(newProblem) {
                     newProblem.innerHTML = "<span><span>Next Problem</span></span>";
                 }
                 
              }
              catch(E) {
                 alert(E);
             }
          }
    }-*/;

    
    public void gwt_solutionHasBeenViewed() {
        presenter.markSolutionAsComplete();
    }

    static private native void expandAllSteps() /*-{
                                          $wnd.expandAllSteps();
                                          }-*/;

    native private void setupJsniInstance(PrescriptionLessonResourceTutorViewImpl instance) /*-{
    	$wnd.gwt_solutionHasBeenViewed = function(){
    	    instance.@hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl::gwt_solutionHasBeenViewed()();
    	 };
    }-*/;
    
    native private static void setupJsniStatic() /*-{
        $wnd.gwt_scrollToBottomOfScrollPanel = @hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl::scrollToBottom(I);
        $wnd.gwt_tutorNewProblem = @hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl::tutorNewProblem();
    }-*/;


    @Override
    public String getBackButtonText() {
        return "back";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }
    
    @Override
    public String getTitle() {
        return _title;
    }
    
    @Override
    public void setTitle(String title) {
        _title = title;
    }
}
