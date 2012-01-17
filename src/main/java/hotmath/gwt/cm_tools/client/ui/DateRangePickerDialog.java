package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;

public class DateRangePickerDialog extends Window {

	static private DateRangePickerDialog __instance;

	static public DateRangePickerDialog showSharedInstance(Date from, Date to,
			Callback callback) {

		// __instance = null; // force new each time.
		if (__instance == null) {
			__instance = new DateRangePickerDialog();
		}
		__instance.setCallback(callback, from, to);
		__instance.setVisible(true);
		return __instance;
	}

	Date _defaultStartDate;
	Callback callback;

	private DateRangePickerDialog() {
		addStyleName("date-range-picker-dialog");
		setSize(420, 435);
		setHeading("Choose Date Range");
		setModal(true);
		setResizable(false);

		LayoutContainer mainPanel = new LayoutContainer(new BorderLayout());
		mainPanel.add(createFromPicker(), new BorderLayoutData(
				LayoutRegion.WEST));
		mainPanel
				.add(createToPicker(), new BorderLayoutData(LayoutRegion.EAST));
		mainPanel.setHeight(280);
		add(mainPanel);
		add(createOptionsPanel());

		_defaultStartDate = CatchupMathAdmin.getInstance()
				.getAccountInfoPanel().getModel().getAccountCreateDate();
		addButton(new Button("Maximum Range",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {

						Date from = _defaultStartDate;
						Date to = new Date();
						HighlightsDataWindow.addDaysToDate(to, 1);

						DateRangePickerDialog.this.callback.datePicked(from,
								to, getFilterOptions());
						hide();
					}
				}));

		addButton(new Button("Select", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {

				Date from = _fromPicker.getValue();
				Date to = _toPicker.getValue();

				DateRangePickerDialog.this.callback.datePicked(from, to,
						getFilterOptions());
				hide();
			}
		}));

		addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		}));
		setVisible(true);
	}

	private FilterOptions getFilterOptions() {
		return new FilterOptions(hasLoggedIn.getValue(),
				hasViewedTest.getValue(), hasTakenQuiz.getValue(),
				hasViewedResources.getValue(), hasViewedLessons.getValue(),
				hasRegistered.getValue());
	}

	CheckBox hasLoggedIn = new CheckBox();
	CheckBox hasViewedTest = new CheckBox();
	CheckBox hasTakenQuiz = new CheckBox();
	CheckBox hasViewedResources = new CheckBox();
	CheckBox hasViewedLessons = new CheckBox();
	CheckBox hasRegistered = new CheckBox();

	private Widget createOptionsPanel() {
		FormPanel fp = new FormPanel();
		fp.setFooter(false);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.setFieldWidth(60);
		fp.setLayout(new FormLayout());

		CheckBoxGroup groupOne = new CheckBoxGroup();
		groupOne.setFieldLabel("Select");
		groupOne.setLabelSeparator("");

		hasLoggedIn.setBoxLabel("Logged In");
		hasLoggedIn.setValue(true);
		hasLoggedIn.setToolTip("Select students that have logged in");
		groupOne.add(hasLoggedIn);

		hasViewedTest.setBoxLabel("Started Quiz");
		hasViewedTest.setValue(true);
		hasViewedTest.setToolTip("Select students that have started a quiz");
		groupOne.add(hasViewedTest);

		hasTakenQuiz.setBoxLabel("Took Quiz");
		hasTakenQuiz.setValue(true);
		hasTakenQuiz.setToolTip("Select students that have completed a quiz");
		groupOne.add(hasTakenQuiz);

		CheckBoxGroup groupTwo = new CheckBoxGroup();
		groupTwo.setFieldLabel(" ");
		groupTwo.setLabelSeparator("");

		hasViewedLessons.setBoxLabel("Lessons");
		hasViewedLessons.setValue(true);
		hasViewedLessons.setToolTip("Select students that have viewed lessons");
		groupTwo.add(hasViewedLessons);

		hasViewedResources.setBoxLabel("Resources");
		hasViewedResources.setValue(true);
		hasViewedResources
				.setToolTip("Select students that have used resources");
		groupTwo.add(hasViewedResources);

		hasRegistered.setBoxLabel("Registered");
		hasRegistered.setValue(true);
		hasRegistered.setToolTip("Select students that have registered");
		groupTwo.add(hasRegistered);

		fp.add(groupOne);
		fp.add(groupTwo);

		Text text = new Text(
				"Include students with selected activities in date range");
		text.setStyleName("date-picker-legend");

		fp.add(text);
		return fp;
	}

	public void setCallback(Callback callback, Date from, Date to) {
		this.callback = callback;

		_fromPicker.setValue(from);
		_toPicker.setValue(to);
	}

	DatePicker _fromPicker, _toPicker;

	private Widget createFromPicker() {
		_fromPicker = new DatePicker();
		return new DatePickerWrapper(_fromPicker, "From");
	}

	private Widget createToPicker() {
		_toPicker = new DatePicker();
		return new DatePickerWrapper(_toPicker, "To");
	}

	/*
	 * customizable options to modify the date range filter
	 */
	public class FilterOptions {
		boolean logins;
		boolean quizStart;
		boolean quizCheck;
		boolean lessons;
		boolean resources;
		boolean registered;

		public FilterOptions(boolean logins, boolean quizStart,
				boolean quizCheck, boolean lessons, boolean resources,
				boolean registered) {
			this.logins = logins;
			this.quizStart = quizStart;
			this.quizCheck = quizCheck;
			this.lessons = lessons;
			this.resources = resources;
			this.registered = registered;
		}

		public boolean isLogins() {
			return logins;
		}

		public void setLogins(boolean logins) {
			this.logins = logins;
		}

		public boolean isQuizStart() {
			return quizStart;
		}

		public void setQuizStart(boolean quizStart) {
			this.quizStart = quizStart;
		}

		public boolean isQuizCheck() {
			return quizCheck;
		}

		public void setQuizCheck(boolean quizCheck) {
			this.quizCheck = quizCheck;
		}

		public boolean isLessons() {
			return lessons;
		}

		public void setLessons(boolean lessons) {
			this.lessons = lessons;
		}

		public boolean isResources() {
			return resources;
		}

		public void setResources(boolean resources) {
			this.resources = resources;
		}

		public boolean isRegistered() {
			return registered;
		}

		public void setRegistered(boolean registered) {
			this.registered = registered;
		}

		public String toParsableString() {
			return logins + ":" + quizStart + ":" + quizCheck + ":" + lessons
					+ ":" + resources + ":" + registered;
		}

		@Override
		public String toString() {
			return "FilterOptions [logins=" + (logins ? 1 : 0) + ", quizView="
					+ (quizStart ? 1 : 0) + ", quizCheck=" + (quizCheck ? 1 : 0)
					+ ", lessons=" + (lessons ? 1 : 0) + ", resources="
					+ (resources ? 1 : 0) + ", registered="
					+ (registered ? 1 : 0) + "]";
		}

		@Override
		public boolean equals(Object anObject) {
			if (this == anObject)
				return true;

			if (anObject == null)
				return false;

			if (anObject instanceof FilterOptions) {
				FilterOptions filterOptions = (FilterOptions) anObject;

				return (this.lessons == filterOptions.lessons
						&& this.logins == filterOptions.logins
						&& this.quizCheck == filterOptions.quizCheck
						&& this.quizStart == filterOptions.quizStart
						&& this.resources == filterOptions.resources && this.registered == filterOptions.registered);
			}
			return false;
		}

		@Override
		public int hashCode() {
			String parseable = this.toParsableString();
			return parseable.hashCode();
		}

	}

	public interface Callback {
		void datePicked(Date from, Date to, FilterOptions options);
	}
}

class DatePickerWrapper extends LayoutContainer {
	DatePicker picker;
	TextField<String> _selected;

	public DatePickerWrapper(DatePicker picker, String title) {
		this.picker = picker;

		picker.setValue(new Date());
		picker.addListener(Events.Select, new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent be) {
				_selected.setValue(asDateString(DatePickerWrapper.this.picker
						.getValue()));
				layout();
			}

		});

		setLayout(new BorderLayout());

		FormPanel form = new FormPanel();
		form.setBodyBorder(false);
		form.setHeaderVisible(false);
		form.setLabelWidth(40);
		form.setFieldWidth(100);

		_selected = new TextField<String>();
		_selected.setFieldLabel(title);
		_selected.setReadOnly(true);
		_selected.setValue(asDateString(this.picker.getValue()));

		form.add(_selected);
		add(form, new BorderLayoutData(LayoutRegion.NORTH, 55));
		add(picker, new BorderLayoutData(LayoutRegion.CENTER));
	}

	@SuppressWarnings("deprecation")
	private String asDateString(Date d) {
		return DateTimeFormat.getShortDateFormat().format(d);
	}
}