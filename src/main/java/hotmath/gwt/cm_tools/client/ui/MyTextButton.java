package hotmath.gwt.cm_tools.client.ui;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class MyTextButton extends TextButton {
    
    public MyTextButton(String name, SelectHandler handler, String tip) {
        super(name, handler);
        setToolTip(tip);
    }
}
