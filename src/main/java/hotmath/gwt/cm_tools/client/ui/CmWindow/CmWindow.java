package hotmath.gwt.cm_tools.client.ui.CmWindow;

import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.event.logical.shared.ResizeHandler;

/** GXT m3  removed close from Window .. ?
 * 
 * @TODO: remove this when the future of close is found ...
 * 
 * @author casey
 *
 */
public class CmWindow extends GWindow {
    
    public CmWindow() {
        super(false);

        addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(com.google.gwt.event.logical.shared.ResizeEvent event) {
                CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        });
    }

}