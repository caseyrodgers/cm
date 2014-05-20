package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_core.client.util.CmAlertify;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmAlertify.PromptCallback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class CmMessageBox {
	static CmAlertify __alertify = CmAlertify.getSharedInstance();

	
    public static void showAlert(String message) {
        showAlert("Important Message", message);
    }

    public static void showAlert(String title, String message) {
        showAlert(title, message, null);
    }

    public static void showAlert(String title,String msg, final CallbackOnComplete onComplete) {
    	
    	__alertify.alert(msg, onComplete);
    	if(true) {
    		return;
    	}
    	
        AlertMessageBox d = new AlertMessageBox(title, msg);
        d.setWidth(300);
        HideHandler hideHandler = new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (onComplete != null) {
                    onComplete.isComplete();
                }
            }
        };
        d.addHideHandler(hideHandler);
        d.show();
        
        d.toFront();
    }

    public static void showMessage(String title,String msg) {
    	showMessage(title, msg, null);
    }

    public static void showMessage(String title,String msg, final CallbackOnComplete onComplete) {
        MessageBox msgBox = new MessageBox(title, msg);
        msgBox.setWidth(300);
        HideHandler hideHandler = new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (onComplete != null) {
                    onComplete.isComplete();
                }
            }
        };
        msgBox.addHideHandler(hideHandler);
        msgBox.show();
        
        msgBox.toFront();
    }

    public static void confirm(String title, String msg, final ConfirmCallback onComplete) {
    	
    	
    	__alertify.confirm(title, msg, onComplete);
    	if(true) {
    		return;
    	}
    	
    	

        final ConfirmMessageBox confirm = new ConfirmMessageBox(title, msg);
        HideHandler hideHandler = new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (onComplete != null) {
                    boolean isConfirmed = confirm.getHideButton() == confirm.getButtonById(PredefinedButton.YES.name());
                    onComplete.confirmed(isConfirmed);
                }
            }
        };
        confirm.addHideHandler(hideHandler);
        confirm.setVisible(true);
        
        confirm.toFront();
    }



	public static void prompt(String title, String message,String defaultValue, PromptCallback callback) {
		__alertify.prompt(title, message,defaultValue, callback);
    	if(true) {
    		return;
    	}
	}
}
