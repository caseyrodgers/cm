package hotmath.gwt.cm_tools.client.ui;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class MyOptionButton extends TextButton {

    public MyOptionButton(String name, String tooltip, SelectHandler selHandler) {
        super(name,selHandler);
        setToolTip(tooltip);
        setWidth(110);
    }
}

