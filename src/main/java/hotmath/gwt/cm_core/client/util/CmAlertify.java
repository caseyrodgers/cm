package hotmath.gwt.cm_core.client.util;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;


/** Wrapper around external JS alertify.js
 * 
 * @author casey
 *
 */
public class CmAlertify {

    
    static public interface ConfirmCallback {
        void confirmed(boolean yesNo);
    }
    
    static public interface PromptCallback {
    	void promptValue(String value);
    }
    

	CallbackOnComplete _callbackComplete;
	PromptCallback _promptCallback;
	ConfirmCallback _confirmCallback;
	
	public void alert(String message) {
		jsni_alert(message);
	}
	
	public void alert(String message, CallbackOnComplete callback) {
		this._callbackComplete = callback;
		jsni_alert(message);
	}
	
	private void afterComplete() {
		if(_callbackComplete != null) {
			_callbackComplete.isComplete();
		}
	}
	
	private void afterConfirm(boolean yesNo) {
		if(_confirmCallback != null) {
			_confirmCallback.confirmed(yesNo);
		}
	}
	
	native private void jsni_alert(String message) /*-{
	    var that=this;

	    if(typeof $wnd.alertify == 'undefined') {
	        alert('Alertify not defined');
	        return;
	    }
	    	
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
	
	private void afterPrompt(String value) {
		if(_promptCallback != null) {
			_promptCallback.promptValue(value);
		}
	}
	
	native private void jsni_prompt(String title, String message, String defaultValue) /*-{
        var that=this;
        $wnd.alertify.prompt(title, function (e, str) {
            that.@hotmath.gwt.cm_core.client.util.CmAlertify::afterPrompt(Ljava/lang/String;)(str);
        }, defaultValue);
    }-*/;
	
	
	public void confirm(String title, String msg, ConfirmCallback onComplete) {
		this._confirmCallback = onComplete;
		jsni_confirm(msg);
	}

	public void prompt(String title, String message,String defaultMessage, PromptCallback callback) {
		this._promptCallback = callback;
		jsni_prompt(title, message,defaultMessage);
	}
	
	
	static private CmAlertify __theInstance; 
	static public CmAlertify getSharedInstance() {
		if(__theInstance == null) {
			__theInstance = new CmAlertify();
		}
		return __theInstance;
	}
}

