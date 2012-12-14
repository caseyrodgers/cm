package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.ui.HTML;

/** Display window indicating the current Flash version is not supported
 * 
 * @author casey
 *
 */
public class FlashVersionNotSupportedWindow extends GWindow {
    
    public FlashVersionNotSupportedWindow() {
        super(true);
        setPixelSize(300,150);
        setHeadingText("Please download Flash");
        
        HTML html = new HTML(CmShared.FLASH_ALT_CONTENT);
        add(html);
        setModal(true);
        setVisible(true);
    }

}
