package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.NextDialog;
import hotmath.gwt.cm.client.ui.NextPanelInfo;
import hotmath.gwt.cm.client.ui.NextPanelInfoImplDefault;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
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
	    
	    Button btn = new Button("Check Quiz");
	    btn.setStyleName("cm-main-panel-next-quiz");
	    btn.setToolTip("Done taking the quiz");
	    
	    btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
	        public void componentSelected(ButtonEvent ce) {
	            doNext();
	        }
	    });
	    list.add(btn);
	    return list;
	}
	
	
	private void showAutoAssignedProgram(String assignedName) {
	    final Window window = new Window();
	    window.setModal(true);
	    window.setHeight(200);
	    window.setWidth(300);
	    window.setClosable(false);
	    window.setResizable(false);
	    window.setStyleName("auto-assignment-window");
	    String msg = "<p>After evaluating your test responses, the following proficiency program is best suited to your requirements:</p> " 
	               + "<p><b>" + assignedName + "</b></p>";
	    
	    Html html = new Html(msg);
	    
	    window.setHeading("Math Placment");
	    window.add(html);
	    

	    Button close = new Button();
	    close.setText("Begin " + assignedName);
	    close.addSelectionListener(new SelectionListener<ButtonEvent>() {
	        public void componentSelected(ButtonEvent ce) {
	            CatchupMath.getThisInstance().showQuizPanel();
	            window.close();
	        }
	    });
	    
	    window.addButton(close);
	    window.setVisible(true);
	}

	public void doNext() {
	    String msg = "Are you sure you are ready to have the quiz scored?";
	    MessageBox.confirm("Ready to Check Test?", msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (be.getButtonClicked().getText().equals("Yes")) {
                    CatchupMath.setBusy(true);
                    PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
                    s.createTestRun(UserInfo.getInstance().getTestId(), new AsyncCallback() {
                        public void onSuccess(Object result) {
                            try {
                                RpcData rdata = (RpcData)result;
                                
                                String na = rdata.getDataAsString("redirect_action");
                                if(na != null) {
                                    if(na.equals("AUTO_ASSIGNED")) {
                                        UserInfo.getInstance().setTestSegment(0);  // reset
                                        showAutoAssignedProgram(rdata.getDataAsString("assigned_test"));
                                    }
                                    else if(na.equals("QUIZ")) {
                                        int testSegment = UserInfo.getInstance().getTestSegment();
                                        int totalSegments = UserInfo.getInstance().getTestSegmentCount();
                                        if((testSegment+1) > totalSegments) {
                                            CatchupMath.showAlert("redirect_action QUIZ: No More Sessions");
                                        }
                                        else {
                                            UserInfo.getInstance().setTestSegment(testSegment+1);
                                            CatchupMath.getThisInstance().showQuizPanel();
                                        }
                                    }
                                    
                                    return;
                                }
                                int runId = rdata.getDataAsInt("run_id");
                                UserInfo.getInstance().setRunId(runId);
                                
                                CatchupMath.getThisInstance().showPrescriptionPanel();
                            }
                            finally {
                                CatchupMath.setBusy(false);
                            }
                        }
                        public void onFailure(Throwable caught) {
                            CatchupMath.showAlert(caught.getMessage());
                        }
                    });
                }
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
	    return "Taking quiz";
	}
}

class QuizContextNextPanelInfo extends NextPanelInfoImplDefault {

	public void doNext() {
		CatchupMath.showAlert("Do next from the quiz");
	}

	public Widget getNextPanelWidget() {
		LayoutContainer cp = new LayoutContainer();
		cp.setStyleName("quiz-next-panel");
		cp.add(new HTML("When ready, you can "));
		Anchor a = new Anchor("Check Your Test.");
		a.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
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
