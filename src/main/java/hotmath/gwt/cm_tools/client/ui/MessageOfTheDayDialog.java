package hotmath.gwt.cm_tools.client.ui;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class MessageOfTheDayDialog extends Window {
    
    interface MyUiBinder extends UiBinder<Widget, MessageOfTheDayDialog> { }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    CallbackGeneric callback;
    public MessageOfTheDayDialog(CallbackGeneric callbackIn) {
        this.callback = callbackIn;
        
        setHeading("Catchup Math Teacher Alert");
        setSize(350,240);
        setModal(true);
        add(uiBinder.createAndBindUi(this));
        
        
        Button okBtn = new Button("OK");
        okBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
                callback.callbackReady();
            }
        });
        addButton(okBtn);
        
        setVisible(true);
    }
}
