package hotmath.gwt.cm_tools.client.ui.CmWindow;

import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;

/** GXT m3  removed close from Window .. ?
 * 
 * @TODO: remove this when the future of close is found ...
 * 
 * @author casey
 *
 */
public class CmWindow extends Window {
    
    public CmWindow() {
        getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
        
        
        getResizable().addResizeListener(new ResizeListener() {
            @Override
            public void handleEvent(ResizeEvent e) {
                CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        });
    }

    /** Add removed close method
     * 
     */
    public void close() {
        hide();
    }
    
    public void addCloseButton() {
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));
    }

}