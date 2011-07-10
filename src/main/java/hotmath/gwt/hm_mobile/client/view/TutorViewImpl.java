package hotmath.gwt.hm_mobile.client.view;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.hm_mobile.client.HmMobile;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class TutorViewImpl extends AbstractPagePanel implements TutorView, IPage {

	ProblemNumber problem;
	
    SimplePanel tutorPanel;
    
	public TutorViewImpl() {
	    tutorPanel = new SimplePanel();
	    initWidget(tutorPanel);
	}

	Presenter presenter;

	@Override
    public void setPresenter(Presenter p) {
		presenter = p;
    }

	@Override
    public void loadSolution(final SolutionResponse solution) {
		this.problem = solution.getProblem();
		setupJsni();
		
		tutorPanel.clear();
		tutorPanel.add(new HTML(solution.getTutorHtml()));
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				initializeTutor(problem.getPid(), solution.getSolutionData(), problem.getProblem(), false, false);
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
	
	static private void tutorNewProblem() {
		HmMobile.__clientFactory.getEventBus().fireEvent(new BackDiscoveryEvent((IPage)HmMobile.__clientFactory.getTutorView()));
	}
	
    private native void initializeTutor(String pid, String solutionDataJs, String title, boolean hasShowWork,boolean shouldExpandSolution) /*-{
          $wnd.TutorManager.initializeTutor(pid, solutionDataJs,title,hasShowWork,shouldExpandSolution);
    }-*/;


    static private native void expandAllSteps() /*-{
                                          $wnd.expandAllSteps();
                                          }-*/;

    native private void setupJsni() /*-{
    	$wnd.gwt_scrollToBottomOfScrollPanel = @hotmath.gwt.hm_mobile.client.view.TutorViewImpl::scrollToBottom(I);
    	$wnd.gwt_tutorNewProblem = @hotmath.gwt.hm_mobile.client.view.TutorViewImpl::tutorNewProblem();
    }-*/;

    @Override
    public String getBackButtonText() {
        return "Back";
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
        return problem.getLabel();
    }
}
