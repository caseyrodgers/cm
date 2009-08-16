package hotmath.gwt.cm_tools.client.ui.CmWindow;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Window;

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
    }

    /** Add removed close method
     * 
     */
    public void close() {
        hide();
    }

}