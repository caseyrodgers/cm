package hotmath.gwt.cm_tools.client.util;

import com.sencha.gxt.widget.core.client.box.MessageBox;

public class CmMessageBox {
    public static void showAlert(String message) {
        showAlert("Alert", message);
        
    }
    public static void showAlert(String title, String message) {
        String html = "<p style='padding: 5px;width: 350px;'>" + message + "</p>";
        MessageBox box = new MessageBox(title, html);
        box.setVisible(true);
    }
}
