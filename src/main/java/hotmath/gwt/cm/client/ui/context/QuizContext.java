package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.NextDialog;
import hotmath.gwt.cm.client.ui.NextPanelInfo;
import hotmath.gwt.cm.client.ui.NextPanelInfoImplDefault;
import hotmath.gwt.cm.client.util.RpcData;
import hotmath.gwt.cm.client.util.UserInfo;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
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
	
	public void setHeaderButtons(IconButton prevBtn, IconButton nextBtn) {
		prevBtn.setEnabled(false);
		nextBtn.setEnabled(true);
		
		nextBtn.setToolTip("Check your test and get Caught Up now!");
	}

	public NextPanelInfo getNextPanelInfo() {
		return new QuizContextNextPanelInfo();
	}

	public void resetContext() {
	}
	
	
	public List<Component> getTools() {
		return null;
	}

	public void doNext() {
	    CatchupMath.setBusy(true);
	    
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.createTestRun(UserInfo.getInstance().getTestId(), new AsyncCallback() {
            public void onSuccess(Object result) {
                RpcData rdata = (RpcData)result;
                int runId = rdata.getDataAsInt("run_id");
                UserInfo.getInstance().setRunId(runId);
                
                CatchupMath.getThisInstance().showPrescriptionPanel();
            }
            public void onFailure(Throwable caught) {
                CatchupMath.showAlert(caught.getMessage());
            }
        });
	}

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
