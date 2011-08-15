package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ShowWorkActivity implements ShowWorkView.Presenter {
    
    EventBus eventBus;
    
    public ShowWorkActivity(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void prepareShowWorkView(ShowWorkView view) {
        setExternalJsniHooks(this);
        
        String pid = "quiz:quiz";
        int runId = 0;
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
            
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                initializeWhiteboard();
            }
        });
    }
    
    protected void flashWhiteboardOut_Gwt(String data, boolean boo) {
        MessageBox.showMessage(data);
    }
    
    protected void whitebaordSave_Gwt(String command) {
        MessageBox.showMessage("Show Work: " + command);
    }


    /** send an array of commands.  Each element in array
        is a command and an array of data.  For example, one
        draw', but a bunch of draw requests.
    */
    static public native void updateWhiteboard(String flashId, String command, String commandData) /*-{
       if(command == 'draw') {
           $wnd.updateWhiteboard([['draw',[commandData]]]);
       }
       else if(command == 'clear') {
           $wnd.updateWhiteboard([['clear',[]]]);
       }
    }-*/;
    
    
    private native void initializeWhiteboard()/*-{
        $wnd.initWhiteboard($doc);
    }-*/;
    
    static public native void disconnectWhiteboard()/*-{
        $wnd.disconnectWhiteboard($doc);
    }-*/;
    
    
    private native void setExternalJsniHooks(ShowWorkActivity x) /*-{
        $wnd.flashWhiteboardOut = function(data, boo) {
            x.@hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity::flashWhiteboardOut_Gwt(Ljava/lang/String;Z)(data, boo);
        }
    }-*/;
    
    
}
