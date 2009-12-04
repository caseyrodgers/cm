package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.action.SaveWhiteboardDataAction;
import hotmath.gwt.shared.client.rpc.result.WhiteboardCommand;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;

public class ShowWorkPanel extends Frame {

	String id;
	String flashId;
	static {
		publishNative();
	}
	
	static ShowWorkPanel __lastInstance;
	String pid;
	
	public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    /** Display the Show Work Flash component in an IFRAME
	 * 
	 */
	public ShowWorkPanel() {
		super("show_work_panel_student.html");
        setStyleName("show-work-panel");

		DOM.setElementProperty(this.getElement(), "frameBorder", "no"); // disable border
		DOM.setElementPropertyInt(this.getElement(), "border", 0); // disable border
		DOM.setElementPropertyInt(this.getElement(), "frameSpacing", 0); // disable border
	    DOM.setElementProperty(this.getElement(), "scrolling", "no"); // disable border
		__lastInstance = this;
		id = "show_work_" + System.currentTimeMillis();
		flashId = id + "_flash";
		
		// CmMainPanel.__lastInstance._mainContent.setScrollMode(Scroll.NONE);
	}
	
	public void clearWhiteBoard() {
	    updateFlashWhiteboard(flashId, "clear","{}");
	}
	
	/** Called from flash component
	 *  Save in DB.
	 *  
	 * @param json
	 */
	public void handleFlashWhiteboardOut(String json) {
        if(UserInfo.getInstance().isShowWorkRequired())
            EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_WHITEBOARDUPDATED, this.pid));

        
		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		int uid = UserInfo.getInstance().getUid();
		int runId = UserInfo.getInstance().getRunId();
		
		/** If json is simple string 'clear', then force a full clear and
		 *  remove all chart data for this user/pid.  Otherwise, it is a 
		 *  normal draw command.
		 */
		SaveWhiteboardDataAction.CommandType commandType= json.equals("clear")?
		               SaveWhiteboardDataAction.CommandType.CLEAR
		               :SaveWhiteboardDataAction.CommandType.DRAW;
		
		SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(uid,runId,pid,commandType,json);
		s.execute(action, new AsyncCallback<RpcData>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				// CatchupMathTools.showAlert("Whiteboard save failed: " + caught);
			}

			public void onSuccess(RpcData result) {
			    Log.debug("ShowWorkPanel: Log message written: " + result);
			}
		});
	}

	public void handleFlashWhiteboardIsReady() {
		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		GetWhiteboardDataAction action = new GetWhiteboardDataAction(UserInfo.getInstance().getUid(),pid);
		s.execute(action,new AsyncCallback<CmList<WhiteboardCommand>>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				CatchupMathTools.showAlert("Whiteboard read failed: " + caught);
			}

			public void onSuccess(CmList<WhiteboardCommand> commands) {
				for(int i=0, t=commands.size();i<t;i++) {
					updateFlashWhiteboard(flashId, commands.get(i).getCommand(),commands.get(i).getData());
				}
			}
		});		
	}

	String backgroundHtml;
	/** Setup for pid, the panel is not loaded yet so
	 *  we cannot update the background image .. so we
	 *  store it until we know the flash component is ready.
	 *  
	 * @param pid
	 */
	public void setupForPid(String pid) {
	       this.pid = pid;
	}
	
	
    /** Push a GWT method onto the global space for the app window
     * 
     *   This wil be called from Flash whiteboard component after
     *   it initializes.
     *   
     *   which is called after each guess selection.
     *   
     */
    static private native void publishNative() /*-{
                                   $wnd.flashWhiteboardIsReady = @hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel::flashWhiteboardIsReady();
                                   $wnd.flashWhiteboardOut = @hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel::flashWhiteboardOut(Ljava/lang/String;);
                                    }-*/;	
    static boolean _flashIsReady;
    static public void flashWhiteboardIsReady() {
    	__lastInstance.handleFlashWhiteboardIsReady();
    }
    
    static public void flashWhiteboardOut(String json) {
    	__lastInstance.handleFlashWhiteboardOut(json);
    }
    
    static public native void updateFlashWhiteboard(String flashId, String command, String commandData) /*-{
        $wnd.updateWhiteboard(flashId, command, commandData);
    }-*/;
    
    static public native void setWhiteboardBackground(String html) /*-{
        $wnd.setWhiteboardBackground(html);
    }-*/;    
}
