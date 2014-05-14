package hotmath.gwt.cm_core.client.util;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;

/** Wrapper around external JS alertify.js
 * 
 * @author casey
 *
 */
public class CmAlertify {

	private CallbackOnComplete callback;
	private ConfirmCallback confirmCallback;

	public void alert(String message) {
		jsni_alert(message);
	}
	
	public void alert(String message, CallbackOnComplete callback) {
		this.callback = callback;
		jsni_alert(message);
	}
	
	private void afterComplete() {
		if(callback != null) {
			callback.isComplete();
		}
	}
	
	private void afterConfirm(boolean yesNo) {
		if(confirmCallback != null) {
			confirmCallback.confirmed(yesNo);
		}
	}
	
	native private void jsni_alert(String message) /*-{
	    var that=this;
		$wnd.alertify.alert(message,function(e) {
	        that.@hotmath.gwt.cm_core.client.util.CmAlertify::afterComplete()();
	    });		
	}-*/;
	
	native private void jsni_confirm(String message) /*-{
	    var that=this;
		$wnd.alertify.confirm(message,function(e, str) {
	        that.@hotmath.gwt.cm_core.client.util.CmAlertify::afterConfirm(Z)(e==true);
	    });		
    }-*/;

	
	
	public void confirm(String title, String msg, ConfirmCallback onComplete) {
		this.confirmCallback = onComplete;
		jsni_confirm(msg);
	}
}
