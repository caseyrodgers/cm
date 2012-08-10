package hotmath.gwt.cm_admin.client.ui;

import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GWindow extends Window {

    public GWindow(boolean addCloseButton) {
        setPixelSize(500, 300);
        setModal(true);
        setBlinkModal(true);
        
        if(addCloseButton) {
            addCloseButton();
        }
    }

    public void setFocusObject() {
        setFocusWidget(getButtonBar().getWidget(0));
    }
    
    public void addCloseButton() {
        TextButton b = new TextButton("Close");
        b.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });
        addButton(b);        
    }

}
