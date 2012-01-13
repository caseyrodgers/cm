package hotmath.gwt.cm_mobile_shared.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


public class MessageBox {
    
    static public void showMessage(String msg) {
        showMessage(new HTML(msg),null);
    }
    static public PopupPanel showMessage(Widget widget,final Callback callback) {
        final PopupPanel popup = new PopupPanel();
        popup.setStyleName("popup-message");
        popup.setAutoHideEnabled(true);
        popup.setModal(true);
        
        FlowPanel mainPanel = new FlowPanel();
        Button btn = new Button("Close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                popup.hide();
            }
        });
        btn.getElement().setInnerHTML("<span><span>Close</span></span>");
        btn.getElement().addClassName("sexy_cm_silver sexybutton");
        mainPanel.add(btn);
        
        mainPanel.add(widget);

        popup.add(mainPanel);
        

        popup.center();
        popup.setWidth("300px");
        
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if(callback != null) {
                    callback.messageClosed();
                }
            }
        });
        
        
        
        //int left=0;
        //int top=50 + Window.getScrollTop();
        //popup.setPopupPosition(left, top);
        popup.show();
        
        return popup;
    }
    
    static public void showError(String msg) {
        showMessage(msg);
    }
    
    static public interface Callback {
        void messageClosed();
    }
}
