package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.model.AdvancedOptionsModel;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GWindow extends Window {

	TextButton closeBtn;
	
    public GWindow(boolean addCloseButton) {
        setPixelSize(500, 300);
        setModal(true);
        setBlinkModal(true);
        
        if(addCloseButton) {
            addCloseButton();
        }
        
        addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        });
    }
    
    public void close() {
        hide();
    }

    public void setFocusObject() {
        setFocusWidget(getButtonBar().getWidget(0));
    }
    
    public TextButton addCloseButton() {
        closeBtn = new TextButton("Close");
        closeBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });
        addButton(closeBtn);
        
        return closeBtn;
    }
    
    
    public interface DateRangeAdvOptCallback {
        void setAdvancedOptions(AdvancedOptionsModel options);
    }

}
