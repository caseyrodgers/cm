package hotmath.gwt.cm_mobile3.client.view;


import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.ShowPrescriptionLessonViewEvent;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
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
	
    TutorWrapperPanel tutorPanel;
    SolutionInfo lastResponse;
    String _title;
    
	public PrescriptionLessonResourceTutorViewImpl() {
	    tutorPanel = new TutorWrapperPanel(true,true,true,true,new TutorCallbackDefault(){
	        @Override
	        public void solutionHasBeenViewed(String value) {
	            presenter.markSolutionAsComplete();
	        }
	        
	        @Override
	        public void onNewProblem(int problemNumber) {
	            tutorNewProblem();
	        }
	        
	        @Override
	        public void tutorWidgetComplete(String inputValue, boolean correct) {
	            if(correct) {
	                presenter.markSolutionAsComplete();
	            }
	        }
	        
	        @Override
	        public void showWhiteboard() {
	            presenter.showWhiteboard(getTitle());
	        }
	        
	        @Override
	        public boolean showTutorWidgetInfoOnCorrect() {
	            return true;
	        }
	        
	        
	        @Override
	        public Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid, int problemNumber) {
	            int uid=SharedData.getUserInfo().getUid();
	            int rid=SharedData.getUserInfo().getRunId();
	            return new SaveSolutionContextAction(uid, rid, pid, problemNumber, variablesJson);
	        }
	        
	        @Override
            public Action<UserTutorWidgetStats> getSaveTutorWidgetCompleteAction(String value, boolean yesNo) {
                return new SaveTutorInputWidgetAnswerAction(SharedData.getUserInfo().getUid(), SharedData.getUserInfo().getRunId(),problem.getPid(), value, yesNo);
            }
	        
	        @Override
	        public void solutionHasBeenInitialized() {
	            if(presenter.getItemData().isViewed()) {
	                tutorPanel.unlockSolution();
	            }
	        }
	        
	    });
	    initWidget(tutorPanel);
	    setStyleName("prescriptionLessonResourceTutorViewImpl");
	}

	Presenter presenter;

	@Override
    public void setPresenter(Presenter p) {
		presenter = p;
		p.setupView(this);
    }

	@Override
    public void loadSolution(final SolutionInfo solution) {
	    
	    // this.tutorPanel.getElement().setAttribute("style", "display: none");
	    this.tutorPanel.setVisible(false);
	    
	    this.lastResponse = solution;
	    this.problem = new ProblemNumber(solution.getPid());
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
			    String context = solution.getContext() != null?solution.getContext().getContextJson():null;
				tutorPanel.externallyLoadedTutor(solution, getWidget(),solution.getPid(), null, solution.getJs(), solution.getHtml(), _title, false, false, context);
				
				tutorPanel.setVisible(true);				
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
	
	static private native void scrollToTop()  /*-{
        $wnd.scrollTo(0,0);
    }-*/;
	
	
	/** called from external #new_problem button
	 *  in the tutor template. 
	 * 
	 */
	private void tutorNewProblem() {
	    if(_total < 2 || __probNum >= _total) {
	        //CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new ShowPrescriptionLessonViewEvent());
            Controller.navigateBack();
	        return;
	    }
	    
        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new HTML("<p>You are currently on problem " + __probNum + " of " + _total + ".</p>" +
                               "<p style='margin-top: 15px'>Are you sure you want to Return to the lesson?</p>"
                               ));
        Button btn = new Button("Return to Lesson",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new ShowPrescriptionLessonViewEvent());
                pp.hide();
            }
        });
        
        btn.getElement().setInnerHTML("<span><span>" + btn.getText() + "</span></span>");
        btn.addStyleName("sexybutton sexy_cm_silver");
        flowPanel.add(btn);
        
        
        Button btn2 = new Button("Cancel",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                pp.hide();
            }
        });
        btn2.getElement().setInnerHTML("<span style='margin-left: 10px'><span>" + btn2.getText() + "</span></span>");
        btn2.addStyleName("sexybutton sexy_cm_silver");
        flowPanel.add(btn2);
        
        
        
        pp = MessageBox.showMessage(flowPanel,null);
	}
	
	
	PopupPanel pp; 
	private void solutionSetComplete_Gwt(int correct, int limit) {
	    presenter.markSolutionAsComplete();
	    
	    FlowPanel flowPanel = new FlowPanel();
	    flowPanel.add(new HTML("<p>You have completed this problem set.<br/>You answered <b style='font-size: 2em'>" + correct + "</b> out of <b style='font-size: 2em'>" + limit + "</b> problems correctly.</p>"));
        Button btn = new Button("Back to Lesson",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Controller.navigateBack();
                pp.hide();
            }
        });
        btn.getElement().setInnerHTML("<span><span>" + btn.getText() + "</span></span>");
        btn.addStyleName("sexybutton");
	    flowPanel.add(btn);
        pp = MessageBox.showMessage(flowPanel,null);
	}
	
	private void resetTutor_Gwt() {
	    Log.debug("reset tutor not implemented");
	}
	
	public static int __probNum;
	int _total;
	private void setSolutionTitle_Gwt(int probNum, int total) {
	    __probNum = probNum;	    _total = total;
	    String title = null;
	    if(total > 0) {
	        title = "Problem Set: " + probNum + " of " + total;
	    }
	    else {
	        title = "Problem 1 of 1";
	    }
	    
	    
	    Log.debug("NEED TO SET TITLE: " + title);
	    //this.tutorPanel.setTutorTitle(title);
	}
	
	private void tutorWidgetCompleteAux(boolean yesNo) {
	    if(yesNo) {
	        // MessageBox.showMessage("Excellent! You are correct.  You can move on to the next problem.");
	    }
	    else {
	        // MessageBox.showMessage("Not right.  You can work through the solution and find the right answer.");
	    }
	}
	
	

    static private native void expandAllSteps() /*-{
                                          $wnd.expandAllSteps();
                                          }-*/;
    
    native private static void setupJsniStatic() /*-{
        $wnd.gwt_scrollToBottomOfScrollPanel = @hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl::scrollToBottom(I);
    }-*/;

    @Override
    public String getBackButtonText() {
        return "back";
    }
    
    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                //tutorNewProblem();
                return true;
            }
        };
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
 