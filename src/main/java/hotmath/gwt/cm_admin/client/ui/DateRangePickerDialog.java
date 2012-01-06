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
    static public DateRangePickerDialog showSharedInstance(Date from, Date to, Callback callback) {
        
        __instance = null;
        if(__instance == null) {
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
        setSize(420,390);
        setHeading("Choose Date Range");
        setModal(true);
        setResizable(false);

        LayoutContainer mainPanel = new LayoutContainer(new BorderLayout());
        mainPanel.add(createFromPicker(), new BorderLayoutData(LayoutRegion.WEST));
        mainPanel.add(createToPicker(), new BorderLayoutData(LayoutRegion.EAST));
        mainPanel.setHeight(280);
        add(mainPanel);
        add(createOptionsPanel());


        _defaultStartDate = CatchupMathAdmin.getInstance().getAccountInfoPanel().getModel().getAccountCreateDate();
        addButton(new Button("Maximum Range", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                
                Date from = _defaultStartDate;
                Date to = new Date();
                HighlightsDataWindow.addDaysToDate(to, 1);
                
                DateRangePickerDialog.this.callback.datePicked(from, to, getFilterOptions());
                hide();
            }
        }));

        addButton(new Button("Select", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                
                Date from = _fromPicker.getValue();
                Date to = _toPicker.getValue();
                
                DateRangePickerDialog.this.callback.datePicked(from, to, getFilterOptions());
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
        return new FilterOptions(hasLoggedIn.getValue(), hasViewedTest.getValue(), hasTakenQuiz.getValue(),
        		hasViewedResources.getValue(), hasViewedLessons.getValue());
    }
    
    CheckBox hasLoggedIn = new CheckBox();
    CheckBox hasViewedTest = new CheckBox();
    CheckBox hasTakenQuiz = new CheckBox();
    CheckBox hasViewedResources = new CheckBox();
    CheckBox hasViewedLessons = new CheckBox();
        
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

        CheckBoxGroup group = new CheckBoxGroup();
        group.setFieldLabel("Check for");
        
        hasLoggedIn.setBoxLabel("Login");
        hasLoggedIn.setValue(true);
        group.add(hasLoggedIn);
        
        hasViewedTest.setBoxLabel("Viewed Quiz");
        hasViewedTest.setValue(true);
        group.add(hasViewedTest);
        
        hasTakenQuiz.setBoxLabel("Taken Quiz");
        hasTakenQuiz.setValue(true);
        group.add(hasTakenQuiz);
        
        hasViewedLessons.setBoxLabel("Lessons");
        hasViewedLessons.setValue(true);
        //group.add(hasViewedLessons);
        
        hasViewedResources.setBoxLabel("Resources");
        hasViewedResources.setValue(true);
        //group.add(hasViewedResources);
        
        fp.add(group);
        
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
    
    /* customizable options to modify the date range filter
     * 
     */
    public class FilterOptions {
        boolean logins;
        boolean quizView;
        boolean quizCheck;
        boolean lessons;
        boolean resources;
        
        public FilterOptions(boolean logins, boolean quizView, boolean quizCheck, boolean lessons, boolean resources) {
            this.logins = logins;
            this.quizView = quizView;
            this.quizCheck = quizCheck;
            this.lessons = lessons;
            this.resources = resources;
        }

        public boolean isLogins() {
            return logins;
        }

        public void setLogins(boolean logins) {
            this.logins = logins;
        }

        public boolean isQuizView() {
            return quizView;
        }

        public void setQuizView(boolean quizView) {
            this.quizView = quizView;
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

		public String toParsableString() {
            return logins + "|" + quizView + "|" + quizCheck + "|" + lessons + "|" + resources;
        }

        @Override
        public String toString() {
            return "FilterOptions [logins=" + (logins?1:0) + ", quizView=" + (quizView?1:0) + ", quizCheck=" + (quizCheck?1:0) +
                   ", lessons=" + (lessons?1:0) + ", resources=" + (resources?1:0) + "]";
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
              _selected.setValue(asDateString(DatePickerWrapper.this.picker.getValue()));
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
        add(form, new BorderLayoutData(LayoutRegion.NORTH,55));
        add(picker, new BorderLayoutData(LayoutRegion.CENTER));
    }
    
    @SuppressWarnings("deprecation")
    private String asDateString(Date d) {
        return DateTimeFormat.getShortDateFormat().format(d);
    }
}