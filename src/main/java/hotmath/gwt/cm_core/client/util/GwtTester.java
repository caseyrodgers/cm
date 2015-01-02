package hotmath.gwt.cm_core.client.util;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GwtTester {
	
    GWindow _theWindow =null;
	public GwtTester(final TestWidget widget) {
		_theWindow = new GWindow(false);
		_theWindow.setHeadingText(widget.getClass().getName());
		_theWindow.setWidget(new TextButton("Go", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				widget.runTest();
			}
		}));
		
		_theWindow.setVisible(true);
	    widget.runTest();
	}
	
	
	public GWindow getWindow() {
	    return _theWindow;
	}
	
	static public interface TestWidget {
		void runTest();
	}

}
