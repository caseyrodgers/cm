package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tutor.client.view.TutorCallback;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Frame;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ViewPrintAssignmentWindow extends GWindow {

	public ViewPrintAssignmentWindow(Assignment assignment) {
		super(true);
		loadAssignment(assignment);
	}

	FlowLayoutContainer _mainFlow = new FlowLayoutContainer();
	private void loadAssignment(final Assignment assignment) {
        setPixelSize(640,500);

        setModal(true);
        setResizable(false);
        setHeadingText("Assignment with " + assignment.getProblemCount() + " problems");

         // String resource = "/assets/util/assignment.jsp?key=" + assignment.getAssignKey();
        
        String resource = "/assignment_print/AssignmentPrint.html?aid=" + assignment.getAssignKey();
        
         Frame frame = new Frame(resource);
         frame.setSize("100%", "450px");

         setWidget(frame);
         
//        _mainFlow.add(new HTML("<h1>Loading report ...</h1>"));
//        setWidget(_mainFlow);
//        
//        getAssignmentPids(assignment);
//        
        TextButton print = new TextButton("Print", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				jsni_printWindow();
			}
		});
        addButton(print);

        setVisible(true);
	}

	
	native protected void jsni_printWindow() /*-{
	    window.print();
	}-*/;


	private void getAssignmentPids(final Assignment ass) {
        new RetryAction<Assignment>() {
            @Override
            public void attempt() {
                GetAssignmentAction action = new GetAssignmentAction(ass.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(Assignment fullAssignment) {
                CmBusyManager.setBusy(false);    
                createAssignmentPrintable(fullAssignment);
            }
        }.register();		
	}
	private void createAssignmentPrintable(final Assignment assignment) {
		
        CmBusyManager.setBusy(true);
        new RetryAction<CmList<Response>>() {
            @Override
            public void attempt() {
                MultiActionRequestAction mAction = new MultiActionRequestAction();
                for(ProblemDto p: assignment.getPids()) {
                    mAction.getActions().add(new GetSolutionAction(0, 0, p.getPid()));	
                }
                setAction(mAction);
                CmShared.getCmService().execute(mAction, this);
            }

            @Override
            public void oncapture(CmList<Response> responses) {
                CmBusyManager.setBusy(false);        
                
                List<SolutionInfo> sols = new ArrayList<SolutionInfo>();
                for(Response r: responses) {
                	sols.add((SolutionInfo)r);
                }
                buildAssReport(sols);
            }
        }.register();		
	}


	protected void buildAssReport(List<SolutionInfo> sols) {
		
		_mainFlow.clear();
		_mainFlow.setScrollMode(ScrollMode.AUTO);
		
		TutorCallback tutorCallback = new TutorCallbackDefault();
		
		for(SolutionInfo sol: sols) {
			
			String context=null;
			TutorWrapperPanel tp = new TutorWrapperPanel(false, false, false, false, tutorCallback);
			_mainFlow.add(tp);
			forceLayout();
			tp.externallyLoadedTutor(sol, tp, null, "Solution: " + sol.getPid(), false, false, context);
		}
		
	}

}
