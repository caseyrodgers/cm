package hotmath.gwt.cm_mobile_shared.client.util;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


public class PopupMessageBox {
    
    static public void showMessage(String msg) {
        showMessage("Important Information", new HTML(msg),null);
    }
    static public PopupPanel showMessage(String title, Widget widget,final CallbackOnComplete callback) {
        final PopupPanel popup = new PopupPanel();
        
        popup.setStyleName("popup-message");
        popup.setAutoHideEnabled(true);
        popup.setModal(true);
        
        FlowPanel mainPanel = new FlowPanel();
        mainPanel.add(new HTML("<div class='popup-title'>" + title + "</div>"));
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
        popup.setWidget(mainPanel);

        popup.setWidth("300px");
        
        popup.center();
        
        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                if(callback != null) {
                    callback.isComplete();
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
        showMessage("Error", new HTML(msg), null);
    }
}
