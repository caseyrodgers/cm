package hotmath.gwt.cm_mobile_assignments.client.activity;


import hotmath.gwt.cm_mobile_assignments.client.ClientFactory;
import hotmath.gwt.cm_mobile_assignments.client.place.ShowWorkPlace;
import hotmath.gwt.cm_mobile_assignments.client.util.AssData;
import hotmath.gwt.cm_mobile_assignments.client.view.ShowWorkView;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.CmTutor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ShowWorkActivity implements Activity, ShowWorkView.Presenter {

    private ShowWorkPlace place;
    private ClientFactory factory;
    private String __problemStatement;

    public ShowWorkActivity(ShowWorkPlace place, ClientFactory clientFactory) {
        this.place = place;
        this.factory = clientFactory;
    }

    @Override
    public String mayStop() {
        return null;
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        
        __problemStatement = jsni_getProblemStatementFromDocument();
        
        ShowWorkView view = factory.getShowWorkView();
        view.setPresenter(this);
        panel.setWidget(factory.getMain(view, "Show Work", true));
        
        
    }
    
    static private native String jsni_getProblemStatementFromDocument() /*-{
        var ps = $doc.getElementById('problem_statement');
        if(!ps) {
            return null;
        }
        else {
            return ps.innerHTML;
        }
    }-*/;      


    @Override
    public Action<? extends Response> getWhiteboardSaveAction(String pid, CommandType commandType, String commandData) {
        return new SaveAssignmentWhiteboardDataAction(AssData.getUserData().getUid(),place.getAssignKey(), place.getPid(),commandType, commandData, false);
    }

    @Override
    public void prepareShowWorkView(final ShowWorkView showWorkView) {
            GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(AssData.getUserData().getUid(), place.getPid(), place.getAssignKey());
            CmTutor.getCmService().execute(action, new AsyncCallback<AssignmentWhiteboardData>() {
                public void onSuccess(final AssignmentWhiteboardData data) {
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            if(showWorkView != null) {
                                showWorkView.loadWhiteboard(data.getCommands());                    }
                            }
                    });
                }

                public void onFailure(Throwable caught) {
                    Log.error("Error getting whiteboard data: " + caught.toString(), caught);
                };
            });
        } 
    
    MultiActionRequestAction whiteboardActions = new MultiActionRequestAction();
    boolean eatNextWhiteboardOut=false;
    

    @Override
    public String getProblemStatementHtml() {
        return __problemStatement; 
    }

}
