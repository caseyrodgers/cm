package hotmath.gwt.cm_tools.client.ui;

import java.util.Date;

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
		DateRangePanel dateRangePanel = DateRangePanel.getInstance();
		setText((dateRangePanel != null) ? "Date range: " + DateRangePanel.getInstance().formatDateRange():"");
	}

	public Date getFromDate() {
		DateRangePanel dateRangePanel = DateRangePanel.getInstance();
		return (dateRangePanel != null) ? dateRangePanel.getFromDate() : null;
	}

	public Date getToDate() {
		DateRangePanel dateRangePanel = DateRangePanel.getInstance();
		return (dateRangePanel != null) ? dateRangePanel.getToDate() : null;
	}

}
