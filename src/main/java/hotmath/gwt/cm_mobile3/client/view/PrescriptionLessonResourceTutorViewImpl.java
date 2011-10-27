package hotmath.gwt.cm_mobile3.client.view;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.TutorMobileWrapperPanel;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class PrescriptionLessonResourceTutorViewImpl extends AbstractPagePanel implements PrescriptionLessonResourceTutorView {

	ProblemNumber problem;
	
    TutorMobileWrapperPanel tutorPanel;
    SolutionResponse lastResponse;
    String _title;
    
    static {
        setupJsniStatic();
    }
    
	public PrescriptionLessonResourceTutorViewImpl() {
	    tutorPanel = new TutorMobileWrapperPanel();
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
	    this.lastResponse = solution;
	    this.problem = solution.getProblem();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				initializeTutor(PrescriptionLessonResourceTutorViewImpl.this, problem.getPid(), presenter.getItemData().getWidgetJsonArgs(), solution.getSolutionData(), solution.getTutorHtml(),problem.getProblem(), false, false);
				
				//tutorPanel.getElement().setAttribute("style", "display:block");
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
	
	PopupPanel pp; 
	private void solutionSetComplete_Gwt(int correct, int limit) {
	    presenter.markSolutionAsComplete();
	    
	    FlowPanel flowPanel = new FlowPanel();
	    flowPanel.add(new HTML("<p>You have completed this problem set.<br/>You answered <b style='font-size: 2em'>" + correct + "</b> out of <b style='font-size: 2em'>" + limit + "</b> problems correct.</p>"));
	    flowPanel.add(new Button("Back to Lesson",new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                Controller.navigateBack();
                pp.hide();
            }
        }));
        pp = MessageBox.showMessage(flowPanel,null);
	}
	
	private void resetTutor_Gwt() {
	    initializeTutor(PrescriptionLessonResourceTutorViewImpl.this, problem.getPid(), presenter.getItemData().getWidgetJsonArgs(),lastResponse.getSolutionData(), lastResponse.getTutorHtml(),problem.getProblem(), false, false);
	}
	
	private void setSolutionTitle_Gwt(int probNum, int total) {
	    
	    String title = null;
	    if(total > 0) {
	        title = "Flashcard Set: problem " + probNum + " of " + total;
	    }
	    else {
	        title = "Problem 1 of 1";
	    }
	    this.tutorPanel.setTutorTitle(title);
	}
	
	/** initialize external tutor JS/HTML and provide
	 *  glue between external JS methods and GWT.
	 *  
	 */
    private native void initializeTutor(PrescriptionLessonResourceTutorViewImpl instance, String pid, String jsonConfig, String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,boolean shouldExpandSolution) /*-{
    
          $wnd.solutionSetComplete = function(numCorrect, limit) {
              instance.@hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl::solutionSetComplete_Gwt(II)(numCorrect,limit);
          }
          
          $wnd.TutorDynamic.setSolutionTitle = function(probNum, total) {
             instance.@hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl::setSolutionTitle_Gwt(II)(probNum,total);
          }
          
          $wnd.TutorManager.initializeTutor(pid, jsonConfig, solutionDataJs,solutionHtml,title,hasShowWork,shouldExpandSolution);
          
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
