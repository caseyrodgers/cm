package hotmath.gwt.cm_mobile_assignments.client.util;

import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.AlertDialog;

public class AssAlertBox extends AlertDialog {
    public AssAlertBox(String msg) {
        this("Error", msg, null);
        
    }
    public AssAlertBox(String title, String msg, TapHandler handler) {
        super(title, msg);
        if(handler != null) {
            addTapHandler(handler);
        }
        
        show();
    }
    public static void showAlert(String msg) {
        new AssAlertBox(msg);
    }
    
    public static void showAlert(String title, String msg) {
        new AssAlertBox(title, msg, null);
    }  
}
