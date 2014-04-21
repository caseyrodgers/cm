package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_core.client.util.CmAlertify;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class CmMessageBox {
	static CmAlertify __alertify = new CmAlertify();
	
    public static void showAlert(String message) {
        showAlert("Important Message", message);
    }

    public static void showAlert(String title, String message) {
        showAlert(title, message, null);
    }

    public static void showAlert(String title,String msg, final CallbackOnComplete onComplete) {
    	
    	__alertify.alert(msg, onComplete);
    	
//        AlertMessageBox d = new AlertMessageBox(title, msg);
//        d.setWidth(300);
//        HideHandler hideHandler = new HideHandler() {
//            @Override
//            public void onHide(HideEvent event) {
//                if (onComplete != null) {
//                    onComplete.isComplete();
//                }
//            }
//        };
//        d.addHideHandler(hideHandler);
//        d.show();
//        
//        d.toFront();
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

        final ConfirmMessageBox confirm = new ConfirmMessageBox(title, msg);
        HideHandler hideHandler = new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (onComplete != null) {
                    boolean isConfirmed = confirm.getHideButton() == confirm.getButtonById(PredefinedButton.YES.name());
                    onComplete.confirmed( isConfirmed);
                }
            }
        };
        confirm.addHideHandler(hideHandler);
        confirm.setVisible(true);
        
        confirm.toFront();
    }
    
    static public interface ConfirmCallback {
        void confirmed(boolean yesNo);
    }
}
