package hotmath.gwt.cm_tools.client.util;

import com.sencha.gxt.widget.core.client.box.MessageBox;

public class CmMessageBox {
    public static void showAlert(String message) {
        MessageBox box = new MessageBox(message);
        box.setVisible(true);
    }
}
