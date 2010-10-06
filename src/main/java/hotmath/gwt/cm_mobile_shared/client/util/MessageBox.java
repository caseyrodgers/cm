package hotmath.gwt.cm_mobile_shared.client.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class MessageBox {
    
    static public void showMessage(String msg) {
        final PopupPanel popup = new PopupPanel();
        popup.setStyleName("popup-message");
        popup.setAutoHideEnabled(true);
        popup.setModal(true);
        popup.add(new HTML(msg));
        popup.center();
        
        int left=0;
        int top=50 + Window.getScrollTop();
        popup.setPopupPosition(left, top);
        popup.show();        
    }
    
    static public void showError(String msg) {
        final PopupPanel popup = new PopupPanel();
        popup.setStyleName("popup-message");
        popup.addStyleName("error");
        popup.setAutoHideEnabled(true);
        popup.setModal(true);
        popup.add(new HTML(msg));
        popup.center();
        popup.show();        
    }

}
