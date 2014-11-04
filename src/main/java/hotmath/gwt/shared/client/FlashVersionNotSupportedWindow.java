package hotmath.gwt.shared.client;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Display window indicating the current Flash version is not supported
 * 
 * @author casey
 *
 */
public class FlashVersionNotSupportedWindow extends GWindow {
    
    public FlashVersionNotSupportedWindow() {
        super(false);
        setPixelSize(300,150);
        setHeadingText("Please download Flash");
        
        HTML html = new HTML(CmShared.FLASH_ALT_CONTENT);
        add(html);
        
        TextButton closeButton = new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();
            }
        });
        addButton(closeButton);

        setModal(true);
        setVisible(true);
    }

}
