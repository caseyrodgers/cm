package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class TutorPreviewDialog extends GWindow {
	
	private String pid;

	public TutorPreviewDialog(String label, String pid) {
		super(true);
		
		setPixelSize(500,450);
		this.pid = pid;
		setHeadingText("Tutor Problem Preview: " + label);
		
		loadTutor(pid);
		setVisible(true);
	}

	TutorWrapperPanel tutorPanel;
	FlowLayoutContainer tutorFlow;
	private void loadTutor(String pid) {
		tutorPanel = new TutorWrapperPanel(true, false, false, false, new TutorCallbackDefault() {
            @Override
            public void solutionHasBeenInitialized() {
            	System.out.println("Is initied");
            }
            
            @Override
            public boolean moveFirstHintOnWidgetIncorrect() {
                return false;
            }
            
            @Override
            public void scrollToBottomOfScrollPanel() {
            	tutorFlow.getScrollSupport().scrollToBottom();
            }
        });
        tutorFlow = new FlowLayoutContainer();
        tutorFlow.setScrollMode(ScrollMode.AUTO);
        tutorFlow.add(tutorPanel);
        setWidget(tutorFlow);
        
        
        loadSolutionFromServer(pid);
	}
	
    public void loadSolutionFromServer(final String pid) {
        
        CmBusyManager.setBusy(true);
        new RetryAction<SolutionInfo>() {
            @Override
            public void attempt() {
            	GetSolutionAction action = new GetSolutionAction(0, 0, pid);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(SolutionInfo solution) {
                CmBusyManager.setBusy(false);
                tutorPanel.externallyLoadedTutor(solution,tutorFlow,"", "Tutor Preview", false, false, null);
                
                forceLayout();
            }
        }.register();
    }
    	
	
	
}
