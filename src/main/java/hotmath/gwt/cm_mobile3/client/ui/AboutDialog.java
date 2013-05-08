package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_core.client.event.ForceSystemSyncCheckEvent;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.background.BackgroundServerChecker;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchAnchor;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData.CallbackWhenDataReady;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.GetUserWidgetStatsAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveFeedbackAction;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemAnnotation;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class AboutDialog extends DialogBox  {
	
    private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

    interface AboutDialogUiBinder extends UiBinder<Widget, AboutDialog> {
    }

	
	public AboutDialog() {
		super(true);
		setSize("300px", "250px");
        setText("Info");		
        setGlassEnabled(true);
        setAnimationEnabled(true);
        setAutoHideEnabled(true);
        
        TabPanel _tabPanel = new TabPanel();
        
        
        FlowPanel mainPanel = new FlowPanel();
        mainPanel.add(uiBinder.createAndBindUi(this));
		
		String loggedIn="not logged in";
		String name=null;
		String segment=null;
		
		if(SharedData.getUserInfo() != null) {
		    loggedIn = SharedData.getUserInfo().getUserName();
		    name = SharedData.getUserInfo().getTestName();
		    
	        int seg = SharedData.getUserInfo().getTestSegment();
	        int segCnt = SharedData.getUserInfo().getProgramSegmentCount();
	        if(segCnt > 1) {
	            segment = seg + " of " + segCnt;
	        }
	        
	        if(name != null) {
	            String value = name;
	            if(segment != null) {
	                value += " " + segment;
	            }
	            programName.setInnerHTML(value);
	        }
	        
	        getScoreFromServer();
	        
	        AssignmentData.readAssData(new CallbackWhenDataReady() {
	            @Override
	            public void isReady() {
	                String info="Open Assignments: " + AssignmentData.getUserData().getAssignments().size() + "</br>";
	                AssignmentUserInfo au = BackgroundServerChecker.getLastAssignmentInfo();
	                if(au != null) {
	                    info += "Unread Teacher Notes: " + au.getUnreadAnnotations().size();
	                    if(au.getUnreadAnnotations().size() > 0) {
	                        assignmentInfo.add(new NextUnreadTeacherNoteButton(au.getUnreadAnnotations())); 
	                    }
	                }
	                assignmentInfo.add(new HTML(info));
	                
	            }
	        });
		}
        else {
            discloseProgram.setVisible(false);
            discloseGoto.setVisible(false);
            discloseAssignment.setVisible(false);
        }
		
		loggedInAs.setInnerHTML(loggedIn);
		
		
		
		
		HorizontalPanel hp = new HorizontalPanel();
		TouchButton close = new TouchButton("Close",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
		hp.add(close);
		
		TouchButton check = new TouchButton("Check Server",new ClickHandler() {
	            @Override
	            public void onClick(ClickEvent event) {
	                CmRpcCore.EVENT_BUS.fireEvent(new ForceSystemSyncCheckEvent());
	            }
	        });
	    hp.add(check);

		hp.getElement().setAttribute("style",  "margin: 10px");
		
		if(SharedData.getMobileUser() == null) {
		    discloseGoto.setVisible(false);
		}
		
		
        DisclosurePanel feedback = new DisclosurePanel();
        Anchor feedbackButton = new TouchAnchor("Feedback");
        feedbackButton.getElement().setAttribute("style", "float: right;margin-right: 10px;margin-bottom:5px;font-size: .9em;");
        feedback.setHeader(feedbackButton);
        feedback.setContent(new FeedbackPanel());
        
        mainPanel.add(feedback);
		mainPanel.add(hp);
		
		
		_tabPanel.add(mainPanel, "About");
		_tabPanel.add(new HTML("ASSIGNMENTS"), "Assignments");
		_tabPanel.selectTab(0);
		
		
		setWidget(mainPanel);
		
        setVisible(true);
	}
	
	private void getScoreFromServer() {
	    if(SharedData.getUserInfo() == null || SharedData.getUserInfo().getUid() == 0) {
	        return;
	    }
	    
	    CatchupMathMobileShared.getCmService().execute(new GetUserWidgetStatsAction(SharedData.getUserInfo().getUid()), new AsyncCallback<UserTutorWidgetStats>() {
	        
	        @Override
	        public void onSuccess(UserTutorWidgetStats result) {
	            score.setInnerHTML(result.getCorrectPercent() + "% (" + result.getCountCorrect()  + "/" + result.getCountWidgets() + ")");
	        }
	        
	        @Override
	        public void onFailure(Throwable caught) {
	            Log.error("Error getting user program stats from server");
	        }
        });
    }

    public void showCentered() {
		center();
	}
    
    
    @UiHandler("assignmentsButton")
    protected void handleAssignments(ClickEvent ce) {
        History.newItem("assignment_list:" + System.currentTimeMillis());
    }
    
    @UiHandler("programButton")
    protected void handleProgram(ClickEvent ce) {
        CmRpcCore.EVENT_BUS.fireEvent(new HandleNextFlowEvent(SharedData.getMobileUser().getFlowAction()));
    }
	
	
    @UiField
    HTMLPanel assignmentInfo;
    
	@UiField
	Element loggedInAs,programInfo,programName, score, assignmentsDiv, programDiv;
	
	@UiField
	TouchButton assignmentsButton, programButton;
	
	@UiField
	DisclosurePanel discloseProgram,discloseGoto, discloseAssignment;
}


class FeedbackPanel extends FlowPanel {
    TextArea textArea = new TextArea();
    public FeedbackPanel() {
        setWidth("270px");
        setHeight("130px");
        getElement().setAttribute("style", "margin: 0px 10px 20px 10px;");
        textArea.setWidth("280px");
        textArea.setHeight("100px");
        add(textArea);
        add(new Button("Save",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveFeedback();
                textArea.setText("");
            }
        }));
    }
    
    private native String  getQueryStringHash() /*-{
        return $wnd.location.hash;
    }-*/;
    
    private void saveFeedback() {
        String comments = textArea.getText();
        if(comments == null || comments.length() == 0) {
            return;
        }

        String url = getQueryStringHash();
        String stateInfo = SharedData.getUserInfo()!=null?SharedData.getUserInfo().toString():"not logged in";
        SaveFeedbackAction action = new SaveFeedbackAction(comments, "cm_mobile: " + url, stateInfo); 
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                MessageBox.showMessage("Feedback saved");
            }
            @Override
            public void onFailure(Throwable caught) {
                MessageBox.showError("Feedback error");
            }
        });
    }
}
