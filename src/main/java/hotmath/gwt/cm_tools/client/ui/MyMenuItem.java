package hotmath.gwt.cm_tools.client.ui;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public final class MyMenuItem extends MenuItem {

    public MyMenuItem(String test, String tip, SelectionHandler<MenuItem> handler) {
        super(test, handler);
        setToolTip(tip);
    }
	
}
