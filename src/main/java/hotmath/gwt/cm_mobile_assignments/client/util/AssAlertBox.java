package hotmath.gwt.cm_mobile_assignments.client.util;

import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.AlertDialog;

public class AssAlertBox extends AlertDialog {
    public AssAlertBox(String msg) {
        this(msg, null);
        
    }
    public AssAlertBox(String msg, TapHandler handler) {
        super("Error", msg);
        if(handler != null) {
            addTapHandler(handler);
        }
        
        show();
    }
    public static void showAlert(String msg) {
        new AssAlertBox(msg);
    }    
}
