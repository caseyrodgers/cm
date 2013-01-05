package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_tools.client.ui.DateRangePickerDialog.FilterOptions;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author bob
 *
 */
public class DateRangePanel extends HorizontalPanel  {
	
	static private DateRangePanel _instance;

	Date fromDate, toDate;
	Date defaultFromDate;
	static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
	TextField dateRangeFilter;
	
	DateRangeCallback dateRangeCallback;

	TextButton dateRangeButton;
	TextButton clearButton;

	public DateRangePanel(DateRangeCallback dateRangeCallback) {
		this.dateRangeCallback = dateRangeCallback;
		init();
		_instance = this;
	}

	public static DateRangePanel getInstance() {
		return _instance;
	}

	public void setDefaultFromDate(Date date) {
		this.defaultFromDate = date;
	}

	public boolean isDefault() {
		return (StudentGridPanel.instance.__searchInfo.getFilterOptions() == null);
	}

	public FilterOptions getFilterOptions() {
		return StudentGridPanel.instance.__searchInfo.getFilterOptions();
	}

	public Date getFromDate() {
		if (fromDate == null)
			fromDate = defaultFromDate;
		return fromDate;
	}

	public Date getToDate() {
		if (toDate == null) {
			toDate = new Date();
			addDaysToDate(toDate, 1);
		}
		return toDate;
	}

	public TextField getDateRangeFilter() {
		return dateRangeFilter;
	}

	@SuppressWarnings("deprecation") // GWT requires Date
	public void addDaysToDate(Date date, int days) {
		date.setDate(date.getDate() + days);
	}

	public String formatDateRange() {
		return dateFormat.format(getFromDate()) + " - " + dateFormat.format(getToDate());
	}

	void init() {

		dateRangeFilter = new TextField();
		dateRangeFilter.setEmptyText(" Use \"set\" for Date Range");
		dateRangeFilter.setWidth("160px");
		dateRangeFilter.setReadOnly(true);
		dateRangeFilter.setToolTip("No date range filter applied");
		dateRangeFilter.addHandler(
				new ClickHandler() {
			        @Override
			        public void onClick(ClickEvent event) {
			    		getFromDate();
						showDatePicker();
				    }
				}, ClickEvent.getType());
		
		getToDate();

		add(new MyFieldLabel(dateRangeFilter,"Date Range", 75));
		

		dateRangeButton = new TextButton("set", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if (fromDate == null)
					fromDate = defaultFromDate;
				showDatePicker();
			}
		});
		
		
		dateRangeButton.setToolTip("Set Date range filter");
		add(dateRangeButton);

		clearButton = new TextButton("clear", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				clearDateRange();
			}
		});
		clearButton.setToolTip("Set date range to maximum");
		add(clearButton);

	}

	private void showDatePicker() {
		DateRangePickerDialog.showSharedInstance(fromDate, toDate, new DateRangePickerDialog.Callback() {
			@Override
			public void datePicked(Date from, Date to, FilterOptions filterOptions) {
				fromDate = (from != null) ? from : fromDate;
				toDate = (to != null) ? to : toDate;

				dateRangeFilter.setValue(formatDateRange(from, to));
				dateRangeFilter.setToolTip("Date range filter applied to Student activity");
				StudentGridPanel.instance.__searchInfo.setFilterOptions(filterOptions);

				applyDateRange();
			}
		});
	}

	private void clearDateRange() {

		dateRangeFilter.clear();
		dateRangeFilter.setToolTip("No date range filter applied");

		fromDate = defaultFromDate;
		toDate = new Date();
		addDaysToDate(toDate, 1);

		StudentGridPanel.instance.__searchInfo.setFilterOptions(null);

		applyDateRange();
	}

	private void applyDateRange() {
		dateRangeCallback.applyDateRange();
	}

	private String formatDateRange(Date from, Date to) {
		if (from != null && to != null)
			return dateFormat.format(from) + " - " + dateFormat.format(to);
		else
			return " ";
	}

}
