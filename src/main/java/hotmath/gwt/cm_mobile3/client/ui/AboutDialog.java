package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile3.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveFeedbackAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
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
        
        FlowPanel mainPanel = new FlowPanel();
        mainPanel.add(uiBinder.createAndBindUi(this));
		
		String loggedIn="nobody";
		String name=null;
		if(SharedData.getUserInfo() != null) {
		    loggedIn = SharedData.getUserInfo().getUserName();
		    name = SharedData.getUserInfo().getTestName();
		    
	        int seg = SharedData.getUserInfo().getTestSegment();
	        int segCnt = SharedData.getUserInfo().getProgramSegmentCount();
	        if(segCnt > 1) {
	            segment.setInnerHTML(seg + " of " + segCnt);
	            segmentInfo.setAttribute("style", "display: block");
	        }
		}
		loggedInAs.setInnerHTML(loggedIn);

		if(name != null) {
		    programName.setInnerHTML(name);
		    programInfo.setAttribute("style", "display: block");
		}
		
		FlowPanel hp = new FlowPanel();
		Button close = new SexyButton("Close");
		close.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
		close.getElement().setInnerHTML("<span><span>Close</span></span>");
		hp.add(close);
		hp.getElement().setAttribute("style", "margin: 5px 0 5px 5px");
		
        DisclosurePanel feedback = new DisclosurePanel();
        Anchor feedbackButton = new Anchor("Feedback");
        feedbackButton.getElement().setAttribute("style", "float: right;margin-right: 10px;margin-bottom:5px;font-size: .9em;");
        feedback.setHeader(feedbackButton);
        feedback.setContent(new FeedbackPanel());
        
        mainPanel.add(feedback);
		mainPanel.add(hp);
		
		setWidget(mainPanel);
		
		
		
        setVisible(true);
	}
	
	public void showCentered() {
		center();
	}
	
	
	@UiField
	Element loggedInAs,programInfo,programName, segment, segmentInfo;
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
