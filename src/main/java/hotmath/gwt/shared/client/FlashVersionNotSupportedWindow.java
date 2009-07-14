package hotmath.gwt.shared.client;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;

/** Display window indicating the current Flash version is not supported
 * 
 * @author casey
 *
 */
public class FlashVersionNotSupportedWindow extends CmWindow {
    
    public FlashVersionNotSupportedWindow() {
        setSize(300,150);
        setHeading("Please download Flash");
        
        Html html = new Html(CmShared.FLASH_ALT_CONTENT);
        add(html);
        
        Button closeButton = new Button("Close");
        closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        addButton(closeButton);

        setModal(true);
        setVisible(true);
    }

}
