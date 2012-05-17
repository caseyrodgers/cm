package hotmath.gwt.cm_tools.client.ui;

import java.util.Date;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.i18n.client.DateTimeFormat;

import hotmath.gwt.cm_tools.client.ui.DateRangePickerDialog.FilterOptions;

/**
 * 
 * @author bob
 *
 */
public class DateRangePanel extends HorizontalPanel{
	
	static private DateRangePanel _instance;

	Date fromDate, toDate;
	Date defaultFromDate;
	static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
	TextField<String> dateRangeFilter;
	FilterOptions _filterOptions;
	DateRangeCallback dateRangeCallback;

	Button dateRangeButton;
	Button clearButton;

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
		return (_filterOptions == null);
	}

	public FilterOptions getFilterOptions() {
		return _filterOptions;
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

	public TextField<String> getDateRangeFilter() {
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

		dateRangeFilter = new TextField<String>();
		dateRangeFilter.setEmptyText(" Use \"set\" for Date Range");
		dateRangeFilter.setFieldLabel("Date Range");
		dateRangeFilter.setWidth("160px");
		dateRangeFilter.setReadOnly(true);
		dateRangeFilter.setToolTip("No date range filter applied");
		dateRangeFilter.addListener(Events.OnMouseUp, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				getFromDate();
				showDatePicker();
			}
		});

		getToDate();

		add(dateRangeFilter);

		dateRangeButton = new Button("set", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (fromDate == null)
					fromDate = defaultFromDate;
				showDatePicker();
			}
		});
		dateRangeButton.setToolTip("Set Date range filter");
		add(dateRangeButton);

		clearButton = new Button("clear", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
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
				_filterOptions = filterOptions;

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

		_filterOptions = null;

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
