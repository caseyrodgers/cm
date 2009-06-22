package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.ArrayList;

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
	
	/** Display the Show Work Flash component in an IFRAME
	 * 
	 */
	public ShowWorkPanel() {
		super("show_work_panel.html");
	    setWidth("550px");
        setStyleName("show-work-panel");

		DOM.setElementPropertyInt(this.getElement(), "frameBorder", 0); // disable border
	    DOM.setElementProperty(this.getElement(), "scrolling", "auto"); // disable border
		__lastInstance = this;
		id = "show_work_" + System.currentTimeMillis();
		flashId = id + "_flash";
		
		// CmMainPanel.__lastInstance._mainContent.setScrollMode(Scroll.NONE);
	}
	
	/** Called from flash component
	 *  Save in DB.
	 *  
	 * @param json
	 */
	public void handleFlashWhiteboardOut(String json) {
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		int uid = UserInfo.getInstance().getUid();
		int runId = UserInfo.getInstance().getRunId();
		
		EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_WHITEBOARDUPDATED, this.pid));
		s.saveWhiteboardData(uid,runId,pid,"draw",json, new AsyncCallback() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				// CatchupMathTools.showAlert("Whiteboard save failed: " + caught);
			}

			public void onSuccess(Object result) {
				// CatchupMathTools.showAlert("Whiteboard save success");
			}
		});
	}

	public void handleFlashWhiteboardIsReady() {
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getWhiteboardData(UserInfo.getInstance().getUid(),pid,new AsyncCallback() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				CatchupMathTools.showAlert("Whiteboard read failed: " + caught);
			}

			public void onSuccess(Object result) {
				ArrayList<RpcData> rdata = (ArrayList<RpcData>)result;
				// CatchupMathTools.showAlert("Whiteboard read success: " + rdata.size());
				setWhiteboardBackground(backgroundHtml);
				for(int i=0;i<rdata.size();i++) {
					// swfWidget.root().invokeMethod("updateWhiteboard",params.getParameter());
					updateFlashWhiteboard(flashId, rdata.get(i).getDataAsString("command"),rdata.get(i).getDataAsString("command_data"));
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
		// call for the solution HTML
		this.pid = pid;
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getSolutionProblemStatementHtml(pid, new AsyncCallback() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				String html = (String) result;
				backgroundHtml = html;
			}
		});
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
