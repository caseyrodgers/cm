package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;

public class ShowWorkPanel extends Frame {

	String id;
	String flashId;
	static {
		publishNative();
	}
	
	static ShowWorkPanel __lastInstance;
	String pid;
	public ShowWorkPanel() {
		
		super("show_work_panel.html");


		DOM.setElementPropertyInt(this.getElement(), "frameBorder", 0); // disable border
		
		__lastInstance = this;
		setWidth("100%");
		setHeight("400px");
		// setupGui();
		setStyleName("show-work-panel");
		id = "show_work_" + System.currentTimeMillis();
		flashId = id + "_flash";
		HTML html = new HTML("Loading ...");
		html.getElement().setId(id);
		html.setStyleName("show-work-panel-bg");
	}
	
	/** Called from flash component
	 *  Save in DB.
	 *  
	 * @param json
	 */
	public void handleFlashWhiteboardOut(String json) {
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.saveWhiteboardData(UserInfo.getInstance().getUid(),pid,"draw",json, new AsyncCallback() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				// CatchupMath.showAlert("Whiteboard save failed: " + caught);
			}

			public void onSuccess(Object result) {
				// CatchupMath.showAlert("Whiteboard save success");
			}
		});
	}

	public void handleFlashWhiteboardIsReady() {
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getWhiteboardData(UserInfo.getInstance().getUid(),pid,new AsyncCallback() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				CatchupMath.showAlert("Whiteboard read failed: " + caught);
			}

			public void onSuccess(Object result) {
				ArrayList<RpcData> rdata = (ArrayList<RpcData>)result;
				// CatchupMath.showAlert("Whiteboard read success: " + rdata.size());
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
                                   $wnd.flashWhiteboardIsReady = @hotmath.gwt.cm.client.ui.viewer.ShowWorkPanel::flashWhiteboardIsReady();
                                   $wnd.flashWhiteboardOut = @hotmath.gwt.cm.client.ui.viewer.ShowWorkPanel::flashWhiteboardOut(Ljava/lang/String;);
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
