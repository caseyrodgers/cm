package hotmath.gwt.assignment_print.client;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.TutorCallback;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentReport {

	private int aid;
	FlowPanel _theReport = new FlowPanel();
	private SimplePanel reportParent;
	private CallbackOnComplete callback;
	private Assignment _assignment;


	public AssignmentReport(int aid) {
		this.aid = aid;
	}

	public void buildReport(CallbackOnComplete callback, SimplePanel reportParent) {
		this.callback = callback;
		this.reportParent = reportParent;
		getAssignmentPids(this.aid);
	}
	
	private void getAssignmentPids(final int assKey) {
		
		CmServiceAsync service = AssignmentPrint.getCmService();
		GetAssignmentAction action = new GetAssignmentAction(assKey);
		service.execute(action, new AsyncCallback<Assignment>() {
			public void onSuccess(Assignment ass) {
				_assignment = ass;
				createAssignmentPrintable(ass);
			}
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				System.out.println(caught);
			}
		});
	}
	
	
	private void createAssignmentPrintable(final Assignment assignment) {
		CmServiceAsync service = AssignmentPrint.getCmService();
        MultiActionRequestAction mAction = new MultiActionRequestAction();
        for(ProblemDto p: assignment.getPids()) {
            mAction.getActions().add(new GetSolutionAction(0, 0, p.getPid()));	
        }
        
        service.execute(mAction, new AsyncCallback<CmList<Response>>() {
        	public void onSuccess(CmList<Response> responses) {
        		
        		  List<SolutionInfo> sols = new ArrayList<SolutionInfo>();
                  for(Response r: responses) {
                  	sols.add((SolutionInfo)r);
                  }
                  buildAssReport(sols);
                  
          		callback.isComplete();
        	}
        	public void onFailure(Throwable caught) {
        		caught.printStackTrace();
        		Window.alert("Error building report: " + caught.getMessage());
        	}
		});

	}	
	
	native private void jsni_fixupReportForPrint(Element element, String widgetType) /*-{
	    $wnd.fixupReportForPrint(element, widgetType);
	}-*/;
	
	protected void buildAssReport(List<SolutionInfo> sols) {
		
		reportParent.add(_theReport);
		
		
		_theReport.add(createReportHeader());
		
		TutorCallback tutorCallback = new TutorCallbackDefault();
		int count=0;
		for(SolutionInfo sol: sols) {
			count++;
			
			try {
				String context=sol.getContext() != null?sol.getContext().getContextJson():null;
				
				FlowPanel tutorHolder = new FlowPanel();
				TutorWrapperPanel tp = new TutorWrapperPanel(false, false, false, false, tutorCallback);
				tutorHolder.setStyleName("assign_problem_holder");
				tutorHolder.add(createSubReportHeader(sol, count));
				tutorHolder.add(tp);
				_theReport.add(tutorHolder);

				tp.externallyLoadedTutor(sol, tp, null, "Solution: " + sol.getPid(), false, false, context);
				
				String widgetType = null;
				try {
					widgetType = jsni_extractWidgetType(sol.getHtml());
				}
				catch(Exception e1) {
					e1.printStackTrace();
				}
				jsni_fixupReportForPrint(tp.getTutorDomNode(), widgetType);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		_theReport.add(createReportFooter());
		
	}
	
	class MyHTML extends HTML {
		public MyHTML(String html, String className) {
			super(html);
			setStyleName(className);
		}
	}

	private Widget createSubReportHeader(SolutionInfo sol, int ordinal) {
		String html = ordinal  + ":";
		return new MyHTML(html, "assign_problem_info");
	}

	private Widget createReportFooter() {
		return new MyHTML("Total Problems: " + _assignment.getPids().size(), "assign_footer_info");
	}


	private Widget createReportHeader() {
		String html = "<div>" + _assignment.getComments() + "</div>";
		return new MyHTML(html, "assign_header_info");
	}

	private native String jsni_extractWidgetType(String html) /*-{
	   
	    var ele = document.createElement('div');
	    ele.innerHTML = html;
	    
	    var widgetDefs = $wnd.$('[name=hm_flash_widget_def]', ele);
	    if(widgetDefs.size() > 0) {
	       var json = widgetDefs.get(0).innerHTML;
	       var widgetobj = eval('(' + json + ')');
	       return widgetobj.type;
	    }
	    else {
	       return null;
	    }
	}-*/;

}
