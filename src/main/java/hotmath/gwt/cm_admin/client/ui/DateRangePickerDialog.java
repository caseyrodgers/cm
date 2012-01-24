package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
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
		setSize(420, 395);
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
						StudentGridPanel.instance.addDaysToDate(to, 1);

						DateRangePickerDialog.this.callback.datePicked(from,
								to, getFilterOptions());
						hide();
					}
				}));

		addButton(new Button("Apply", new SelectionListener<ButtonEvent>() {
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
		return new FilterOptions(loggedIn, startedQuiz, tookQuiz,
				usedResources, viewedLessons, registered);
	}

	Boolean loggedIn = new Boolean(true);
	Boolean startedQuiz = new Boolean(true);
	Boolean tookQuiz = new Boolean(true);
	Boolean viewedLessons = new Boolean(true);
	Boolean usedResources = new Boolean(true);
	Boolean registered = new Boolean(true);
	
	private Widget createOptionsPanel() {
		FormPanel fp = new FormPanel();
		fp.setFooter(false);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setButtonAlign(HorizontalAlignment.LEFT);
		fp.setFieldWidth(60);
		fp.setLayout(new FormLayout());
		
		fp.add(advancedOptionsBtn());
		
		return fp;
	}

	private Button advancedOptionsBtn() {
		Button btn = new Button("Advanced Options");
		btn.setToolTip("Select Activities: logged in, started quizzes, took quizzes, viewed lessons, used resources, registered");
		btn.setWidth("110px");
	    btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
	        public void componentSelected(ButtonEvent ce) {
	            AdvOptCallback callback = new AdvOptCallback() {
					@Override
					void setAdvancedOptions(Map<String, Boolean> optionMap) {
						loggedIn = optionMap.get(DateRangeAdvancedOptionsDialog.LOGGED_IN);
						startedQuiz = optionMap.get(DateRangeAdvancedOptionsDialog.STARTED_QUIZ);
						tookQuiz = optionMap.get(DateRangeAdvancedOptionsDialog.TOOK_QUIZ);
						viewedLessons = optionMap.get(DateRangeAdvancedOptionsDialog.VIEWED_LESSONS);
						usedResources = optionMap.get(DateRangeAdvancedOptionsDialog.USED_RESOURCES);
						registered = optionMap.get(DateRangeAdvancedOptionsDialog.REGISTERED);
					}
	            };
	            final Map<String,Boolean>advOptionsMap = new HashMap <String,Boolean> ();

	            advOptionsMap.put(DateRangeAdvancedOptionsDialog.LOGGED_IN, loggedIn);
	            advOptionsMap.put(DateRangeAdvancedOptionsDialog.STARTED_QUIZ, startedQuiz);
	            advOptionsMap.put(DateRangeAdvancedOptionsDialog.TOOK_QUIZ, tookQuiz);
	            advOptionsMap.put(DateRangeAdvancedOptionsDialog.VIEWED_LESSONS, viewedLessons);
	            advOptionsMap.put(DateRangeAdvancedOptionsDialog.USED_RESOURCES, usedResources);
	            advOptionsMap.put(DateRangeAdvancedOptionsDialog.REGISTERED, registered);

	            new DateRangeAdvancedOptionsDialog(callback, advOptionsMap).setVisible(true);              
	        }
	    });
		return btn;
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

abstract class AdvOptCallback {
	abstract void setAdvancedOptions(Map<String,Boolean> optionMap);
}
