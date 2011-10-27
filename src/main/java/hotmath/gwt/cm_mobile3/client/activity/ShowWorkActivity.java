package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ShowWorkActivity implements ShowWorkView.Presenter {
    
    EventBus eventBus;
    String pid;
    int runId;
    
    static ShowWorkActivity __lastInstance;
    public ShowWorkActivity(EventBus eventBus, String pid, int runId) {
        this.eventBus = eventBus;
        __lastInstance = this;
        if(pid == null || pid.length() == 0) {
            pid = "quiz:quiz";
        }
        this.pid = pid;
        this.runId = runId;
    }

    @Override
    public void prepareShowWorkView(ShowWorkView view) {
        setExternalJsniHooks(this);
        
        /** TODO: why use global? */
        //int runId = pid.startsWith("quiz") ? 0 : CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo().getRunId();

        eventBus.fireEvent(new SystemIsBusyEvent(true));
        GetWhiteboardDataAction action = new GetWhiteboardDataAction(CatchupMathMobileShared.getUser().getUserId(), pid, runId);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmList<WhiteboardCommand>>() {
            final String flashId="";
            public void onSuccess(hotmath.gwt.cm_rpc.client.rpc.CmList<WhiteboardCommand> commands) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));                
                for(int i=0,t=commands.size(); i < t; i++) {
                    try {
                        updateWhiteboard(flashId, commands.get(i).getCommand(), commands.get(i).getData());
                    }
                    catch(Exception e) {
                        Log.error("Error processing whitebaord command: " + e.getMessage());
                    }
                }
            }
            
            public void onFailure(Throwable caught) {
                Log.error("Error getting whiteboard data", caught);
                eventBus.fireEvent(new SystemIsBusyEvent(false));
            };
        });
            
        
        /** execute initialize only after HTML is loaded */
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                initializeWhiteboard();
            }
        });
    }
    
    static public void saveWhiteboard() {
        __lastInstance.saveWhiteboardToServer();
    }

    MultiActionRequestAction whiteboardActions = new MultiActionRequestAction();
    boolean eatNextWhiteboardOut=false;
    protected void whiteboardOut_Gwt(String json, boolean boo) {
        Log.debug("whiteboardOut: " + json);
        
        if (eatNextWhiteboardOut) {
            eatNextWhiteboardOut = false;
            return;
        }
        /**
         * If json is simple string 'clear', then force a full clear and
         * remove all chart data for this user/pid. Otherwise, it is a
         * normal draw command.
         */
        CommandType commandType = json.equals("clear") ? CommandType.CLEAR : CommandType.DRAW;
        if(commandType == CommandType.CLEAR) {
            whiteboardActions.getActions().clear();
        }
        int runId = pid.startsWith("quiz") ? 0 : CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo().getRunId();
        SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(CatchupMathMobileShared.getUser().getUserId(),runId, pid, commandType, json);
        whiteboardActions.getActions().add(action);
    }
    
    private void saveWhiteboardToServer() {
        
        if(whiteboardActions.getActions().size() == 0)
            return;
        
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        CatchupMathMobileShared.getCmService().execute(whiteboardActions,new AsyncCallback<CmList<Response>>() {
            public void onSuccess(hotmath.gwt.cm_rpc.client.rpc.CmList<Response> result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                whiteboardActions.getActions().clear();
            }
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                Log.error("Error saving whiteboard", caught);
                MessageBox.showError(caught.getMessage());
            }
        });
        
    }
    
    protected void whiteboardSave_Gwt() {
        saveWhiteboardToServer();
    }
    

    /** send an array of commands.  Each element in array
        is a command and an array of data.  For example, one
        draw', but a bunch of draw requests.
    */
    static public native void updateWhiteboard(String flashId, String command, String commandData) /*-{

       var cmdArray = [];
       if(command == 'draw') {
           cmdArray = [['draw',[commandData]]];
       }
       else if(command == 'clear') {
           cmdArray = [['clear',[]]];
       }
        
        var realArray = [];
        for (var i = 0, t = cmdArray.length; i < t; i++) {
            var ele = [];
            ele[0] = cmdArray[i][0];
            ele[1] = cmdArray[i][1];
            realArray[i] = ele;
        }
       $wnd.Whiteboard.updateWhiteboard(realArray);
    }-*/;
    
    
    private native void initializeWhiteboard()/*-{
        $wnd.Whiteboard.initWhiteboard($doc);
    }-*/;
    
    static public native void disconnectWhiteboard()/*-{
        $wnd.Whiteboard.disconnectWhiteboard($doc);
    }-*/;
    
    
    private native void setExternalJsniHooks(ShowWorkActivity x) /*-{
        $wnd.Whiteboard.whiteboardOut = function(data, boo) {
            x.@hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity::whiteboardOut_Gwt(Ljava/lang/String;Z)(data, boo);
        }
        
        $wnd.Whiteboard.saveWhiteboard = function() {
            x.@hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity::whiteboardSave_Gwt()();
        }
    }-*/;
    
    
}
