package hotmath.gwt.cm_tools.client.ui;

import com.google.gwt.user.client.ui.Label;

import hotmath.gwt.cm_tools.client.ui.DateRangePanel;

/**
 * 
 * @author bob
 *
 */
public class DateRangeWidget extends Label {

	private static DateRangeWidget _instance;

	private DateRangeWidget() {
		addStyleName("date-range-label");
	}
	
	public static DateRangeWidget getInstance() {
		if (_instance == null) {
			_instance = new DateRangeWidget();
		}
		return _instance;
	}

	public void refresh() {
		setText("Date range: " + DateRangePanel.getInstance().formatDateRange());
	}

}
