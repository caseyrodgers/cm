package hotmath.gwt.cm_core.client.util;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

/** Wrapper around external JS alertify.js
 * 
 * @author casey
 *
 */
public class CmAlertify {

	private CallbackOnComplete callback;

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
	
	native private void jsni_alert(String message) /*-{
	    var that=this;
		$wnd.alertify.alert(message,function(e) {
	        that.@hotmath.gwt.cm_core.client.util.CmAlertify::afterComplete()();
	    });		
	}-*/;
}
