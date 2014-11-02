package hotmath.gwt.cm_core.client.util;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GwtTester {
	
	public GwtTester(final TestWidget widget) {
		GWindow tester = new GWindow(false);
		tester.setHeadingText(widget.getClass().getName());
		tester.setWidget(new TextButton("Go", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				widget.runTest();
			}
		}));
		
		tester.setVisible(true);		
	}
	
	
	static public interface TestWidget {
		void runTest();
	}

}
