package hotmath.gwt.cm_search.client.view;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.TutorMobileWrapperPanel;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class TutorViewImpl extends AbstractPagePanel implements TutorView {
    
    Presenter presenter;
    TutorMobileWrapperPanel tutorPanel;
    
    public TutorViewImpl() {
        tutorPanel = new TutorMobileWrapperPanel();        
        initWidget(tutorPanel);
        setupJsniInstance(this);
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
    
    
    
    native private void setupJsniInstance(TutorViewImpl instance) /*-{
        $wnd.gwt_solutionHasBeenViewed = function(){
            instance.@hotmath.gwt.cm_search.client.view.TutorViewImpl::gwt_solutionHasBeenViewed()();
         };
    
        $wnd.gwt_tutorNewProblem = function(){
            instance.@hotmath.gwt.cm_search.client.view.TutorViewImpl::tutorNewProblem()();
        };      
   }-*/;


    SolutionResponse lastResponse;
    ProblemNumber problem;
    
    @Override
    public void loadSolution(final SolutionResponse solution) {
        
        // this.tutorPanel.getElement().setAttribute("style", "display: none");
        this.tutorPanel.setVisible(false);
        
        this.lastResponse = solution;
        this.problem = solution.getProblem();
        this.tutorPanel.setPid(solution.getProblem().getPid());
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                
                String widgetJsonConfig="";
                initializeTutor(TutorViewImpl.this, problem.getPid(),widgetJsonConfig, solution.getSolutionData(), solution.getTutorHtml(),problem.getProblem(), false, false);
                
                tutorPanel.setVisible(true);                
            }
        });
    }   
    
    private native void initializeTutor(TutorViewImpl instance, String pid, String jsonConfig, String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,boolean shouldExpandSolution) /*-{

    $wnd.solutionSetComplete = function(numCorrect, limit) {
        instance.@hotmath.gwt.cm_search.client.view.TutorViewImpl::solutionSetComplete_Gwt(II)(numCorrect,limit);
    }
    
        
    $wnd.TutorDynamic.setSolutionTitle = function(probNum, total) {
       instance.@hotmath.gwt.cm_search.client.view.TutorViewImpl::setSolutionTitle_Gwt(II)(probNum,total);
    }
    
    
    $wnd.tutorWidgetCompleteAux = function(yesNo) {
       instance.@hotmath.gwt.cm_search.client.view.TutorViewImpl::tutorWidgetCompleteAux(Z)(yesNo);
    }
    
    $wnd.TutorManager.initializeTutor(pid, jsonConfig, solutionDataJs,solutionHtml,title,hasShowWork,shouldExpandSolution);
    
    $wnd.gwt_scrollToBottomOfScrollPanel = @hotmath.gwt.cm_search.client.view.TutorViewImpl::scrollToBottom(I);
    
    // Customize Tutor HTML for Mobile 
    var tutorFooter = $doc.getElementById('steps_tail');
    if(tutorFooter) {
        try {
           var showWhiteboard = $doc.getElementById('show_whiteboard_button');
           if(!showWhiteboard) {
               showWhiteboard = $doc.createElement("button");
               showWhiteboard.className = 'sexybutton sexy_cm_silver';
               showWhiteboard.id = 'show_whiteboard_button';
               showWhiteboard.innerHTML = "<span><span>Whiteboard</span></span>";
               tutorFooter.appendChild(showWhiteboard);
           }
           
           showWhiteboard = $doc.getElementById('show_whiteboard_button');
           showWhiteboard.onclick = function() {
               instance.@hotmath.gwt.cm_search.client.view.TutorViewImpl::showWhiteboard_Gwt()();
           };
        }
        catch(E) {
           alert(E);
       }
    }
}-*/;
    
    private void showWhiteboard_Gwt() {
        presenter.showWhiteboard(getTitle());
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
   
    
    int _probNum;
    int _total;
    private void setSolutionTitle_Gwt(int probNum, int total) {
        _probNum = probNum;     _total = total;
        String title = null;
        if(total > 0) {
            title = "Problem Set: " + probNum + " of " + total;
        }
        else {
            title = "Problem 1 of 1";
        }
        this.tutorPanel.setTutorTitle(title);
    }
    
    private void tutorWidgetCompleteAux(boolean yesNo) {
        if(yesNo) {
            // MessageBox.showMessage("Excellent! You are correct.  You can move on to the next problem.");
        }
        else {
            // MessageBox.showMessage("Not right.  You can work through the solution and find the right answer.");
        }
    }
    
    static private native void scrollToBottom(int top)  /*-{
        $wnd.scrollTo(0,top);
    }-*/;
    
    public void gwt_solutionHasBeenViewed() {
        presenter.markSolutionAsComplete();
    }
    
}
