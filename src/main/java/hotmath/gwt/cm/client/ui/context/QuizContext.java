package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.NextDialog;
import hotmath.gwt.cm_tools.client.ui.NextPanelInfo;
import hotmath.gwt.cm_tools.client.ui.NextPanelInfoImplDefault;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.action.CreateTestRunAction;
import hotmath.gwt.shared.client.rpc.result.CreateTestRunResponse;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Where in the quiz is the user?
 * 
 * @author Casey
 * 
 */
public class QuizContext implements CmContext {

	QuizCmGuiDefinition guiDef;
	String title;
	
	public QuizContext(QuizCmGuiDefinition guiDef) {
		this.guiDef = guiDef;
	}
	
	public int getContextCompletionPercent() {
		// depends on on segment number (4 segments 25% each)
		int sn = UserInfo.getInstance().getTestSegment();
		int ts = 4;
		
		return 25;
	}

	public String getContextHelp() {
		return "Try to answer the questions as best you can.  Then click the '>' button to check your answers.";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContextTitle() {
		return getTitle();
	}
	
	public String getContextSubTitle() {
	    String title = getTitle();
	    String section = UserInfo.getInstance().getTestSegment() + " of " + UserInfo.getInstance().getTestSegmentCount();	    
	    return title + "<h2>Section " + UserInfo.getInstance().getTestSegment() + " of " + UserInfo.getInstance().getTestSegmentCount() +
	                   "</h2>";
	}

	public NextPanelInfo getNextPanelInfo() {
		return new QuizContextNextPanelInfo();
	}

	public void resetContext() {
	}
	
	public List<Component> getTools() {
	    List<Component> list = new ArrayList<Component>();
	    
	    IconButton btn = new IconButton("cm-main-panel-next-quiz");
	    btn.setToolTip("Done taking the quiz");
	    
	    btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
	        public void componentSelected(IconButtonEvent ce) {
	            doNext();
	        }
	    });
	            
	    list.add(btn);
	    return list;
	}
	
	
	private void showAutoAssignedProgram(String assignedName) {
	    final CmWindow window = new CmWindow();
	    window.setModal(true);
	    window.setHeight(175);
	    window.setWidth(300);
	    window.setClosable(false);
	    window.setResizable(false);
	    window.setStyleName("auto-assignment-window");
	    String msg = "<p>Thank you - You are now enrolled in Catchup Program:</p> " 
	               + "<p><b>" + assignedName + "</b></p>";
	    
	    Html html = new Html(msg);
	    
	    window.setHeading("Quiz results");
	    window.add(html);
	    

	    Button close = new Button();
	    close.setText("OK");
	    close.addSelectionListener(new SelectionListener<ButtonEvent>() {
	        public void componentSelected(ButtonEvent ce) {
	            CatchupMath.getThisInstance().showQuizPanel();
	            window.close();
	        }
	    });
	    
	    window.addButton(close);
	    window.setVisible(true);
	}
	
	private void showNextPlacmentQuiz() {
        CatchupMathTools.showAlert("Quiz results", "Good job - we'll now give another quiz.", new CmAsyncRequestImplDefault() {
            public void requestComplete(String requestData) {
                CatchupMath.getThisInstance().showQuizPanel();
            }
        });	    
	}
	
	
	private void showPrescriptionPanel(final CreateTestRunResponse runInfo) {
	    
	    
	    if(UserInfo.getInstance().isAutoTestMode()) {
	        CatchupMath.getThisInstance().showPrescriptionPanel();
	    }
	    else {
    	    final CmWindow window = new CmWindow();
    	    window.setModal(true);
    	    window.setAutoHeight(true);
    	    window.setWidth(350);
    	    window.setClosable(false);
    	    window.setResizable(false);
            window.setStyleName("auto-assignment-window");
            window.setHeading("Quiz results");
    	    
    	    
    	    
    	    int correct = runInfo.getCorrect();
    	    int total = runInfo.getTotal();
    	    
    	    
    	    String msg = "";
    	    if(runInfo.getPassed()) {
    	        if(correct != total) {
    	            msg += "Your quiz score: " + runInfo.getTestCorrectPercent() + "%</br>" +
        	                "Congratulations, you passed!</br>" +
        	                "You have " + runInfo.getSessionCount() + " review topics to study before advancing to the next quiz.<br/>" +
           	                "First topic: <b>" + runInfo.getSessionName() + "</b></br>";
    	        }
    	        else {
    	            msg += "Your quiz score: " + runInfo.getTestCorrectPercent() + "%</br>" +
                    "Congratulations, you passed!</br>";
    	        }
    	    }
    	    else {
    	        // did not pass
    	        msg += "Your quiz score: " + runInfo.getTestCorrectPercent() + "%</br>" +
    	                "You need " + UserInfo.getInstance().getPassPercentRequired() + "% to pass.</br>" +
                        "You have " + runInfo.getSessionCount() + " review topics to study before advancing to the next quiz.<br/>" +
                        "First topic: <b>" + runInfo.getSessionName() + "</b></br>";
    	    }
    	        
    	    Html html = new Html("<p>" + msg + "</p>");
            window.add(html);
    	         
            Button close = new Button();
            close.setText("Continue");
            close.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {

                    if(runInfo.getPassed()) {
                        // are there more Quizzes in this program?
                        boolean areMoreSegments = UserInfo.getInstance().getTestSegment() < UserInfo.getInstance().getTestSegmentCount();
                        if (areMoreSegments) {
                            UserInfo.getInstance().setTestSegment(UserInfo.getInstance().getTestSegment() + 1);
                            CmHistoryManager.getInstance().addHistoryLocation(new CmLocation(LocationType.QUIZ, UserInfo.getInstance().getTestSegment()));
                        } else {
                            PrescriptionContext.autoAdvanceUser();
                        }
                    }
                    else {
                        UserInfo.getInstance().setSessionNumber(0);  // beginning of prescription
                        CatchupMath.getThisInstance().showPrescriptionPanel();
                    }
                    window.close();
                }
            });
    	        
            window.addButton(close);
            window.setVisible(true);
	    }
	}

	public void doNext() {
	    String msg = "Are you sure you are ready to have the quiz scored?";
	    MessageBox.confirm("Ready to Check Quiz?", msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (be.getButtonClicked().getText().equals("Yes")) {
                    doCheckTest();
                }
            }
        });	    
	    
	}

	
	
	public void doCheckTest() {
        CatchupMathTools.setBusy(true);
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new CreateTestRunAction(UserInfo.getInstance().getTestId()), new AsyncCallback<CreateTestRunResponse>() {
            public void onSuccess(CreateTestRunResponse testRunInfo) {
                try {
                    String na = testRunInfo.getAction();
                    if(na != null) {
                        if(na.equals("AUTO_ASSIGNED")) {
                            UserInfo.getInstance().setTestSegment(0);  // reset
                            String testName = testRunInfo.getAssignedTest();
                            UserInfo.getInstance().setTestName(testName);
                            showAutoAssignedProgram(testName);
                        }
                        else if(na.equals("QUIZ")) {
                            int testSegment = UserInfo.getInstance().getTestSegment();
                            int totalSegments = UserInfo.getInstance().getTestSegmentCount();
                            if((testSegment+1) > totalSegments) {
                                CatchupMathTools.showAlert("redirect_action QUIZ: No More Sessions");
                            }
                            else {
                                UserInfo.getInstance().setTestSegment(testSegment+1);
                                showNextPlacmentQuiz();
                            }
                        }
                        
                        return;
                    }
                    int runId = testRunInfo.getRunId();
                    UserInfo.getInstance().setRunId(runId);
                    UserInfo.getInstance().setSessionNumber(0);  // start over
                    
                    showPrescriptionPanel(testRunInfo);
                }
                finally {
                    CatchupMathTools.setBusy(false);
                }
            }
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });
	}
	
    
    /** Return the count of correct answers
     * 
     * @return
     */
    private native int getQuizResultsCorrect() /*-{
        return $wnd.getQuizResultsCorrect();
    }-*/;
    

    private native int getQuizQuestionCount() /*-{
        return $wnd.getQuizQuestionCount();
    }-*/;

	public void doPrevious() {
		CatchupMath.getThisInstance().showLoginPage();
	}
	
	public String getStatusMessage() {
	    
        String msg1="<ul><li><b>Relax! Work out each answer at your own pace using pencil and paper. " +
        "Press Check Quiz when you are ready to receive review and practice for " +
        "the lessons that you are ready to learn! Your quiz score is not " +
        "important, only learning is important.</li></ul>";
        return msg1;
	}
	

    public void runAutoTest() {
        doCheckTest();
    }
}

class QuizContextNextPanelInfo extends NextPanelInfoImplDefault {

	public void doNext() {
		CatchupMathTools.showAlert("Do next from the quiz");
	}

	public Widget getNextPanelWidget() {
		LayoutContainer cp = new LayoutContainer();
		cp.setStyleName("quiz-next-panel");
		cp.add(new HTML("When ready, you can "));
		Anchor a = new Anchor("Check Your Test.");
		a.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent event) {
				// History.newItem("pres=0");
				History.newItem("pres=1");
				NextDialog.destroyCurrentDialog();				
			}
		});
		cp.add(a);
		cp.add(new HTML("<p style='margin-top: 15px;'>After checking your test Catchup Math will assign a personal set of " +
				        "review and practice problems to guide you through " +
				        " your trouble spots.</p>"));
		return cp;
	}
}
