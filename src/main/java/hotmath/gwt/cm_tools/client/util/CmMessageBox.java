package hotmath.gwt.cm_tools.client.util;

import com.sencha.gxt.widget.core.client.box.MessageBox;

public class CmMessageBox {
    public static void showAlert(String message) {
        showAlert("Alert", message);
        
    }
    public static void showAlert(String title, String message) {
        MessageBox box = new MessageBox(title, message);
        box.setVisible(true);
    }
}
