package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.model.AdvancedOptionsModel;

import java.util.Date;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.DatePicker;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent.ShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class DateRangePickerDialog extends GWindow {

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
        super(false);
        
        addStyleName("date-range-picker-dialog");
        setPixelSize(420, 395);
        setHeadingText("Choose Date Range");
        setModal(true);
        setResizable(false);

        BorderLayoutContainer mainPanel = new BorderLayoutContainer();
        
        mainPanel.setWestWidget(createFromPicker(), new BorderLayoutData(.50));
        mainPanel.setEastWidget(createToPicker(), new BorderLayoutData(.50));
        mainPanel.setHeight(280);
        setWidget(mainPanel);

        mainPanel.setSouthWidget(createOptionsPanel(), new BorderLayoutData(50));

        if(DateRangePanel.getInstance() != null) {
            _defaultStartDate = DateRangePanel.getInstance().getFromDate();
        }
        else {
            _defaultStartDate = new Date();
        }
        addButton(new TextButton("Maximum Range",
                new SelectHandler() {
                    @Override
                    public void onSelect(SelectEvent event) {
                        Date from = _defaultStartDate;
                        Date to = new Date();
                        DateRangePanel.getInstance().addDaysToDate(to, 1);

                        DateRangePickerDialog.this.callback.datePicked(from,
                                to, getFilterOptions());
                        hide();
                    }
                }));

        addButton(new TextButton("Apply", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                Date from = _fromPicker.getValue();
                Date to = _toPicker.getValue();

                DateRangePickerDialog.this.callback.datePicked(from, to,
                        getFilterOptions());
                hide();
            }
        }));

        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
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
        CenterLayoutContainer flow = new CenterLayoutContainer();
        flow.add(advancedOptionsBtn());

        return flow;
    }

    private TextButton advancedOptionsBtn() {
        TextButton btn = new TextButton("Advanced Options");
        btn.setToolTip("Select Activities: logged in, started quizzes, took quizzes, viewed lessons, used resources, registered");
        btn.setWidth("110px");
        btn.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                DateRangeAdvOptCallback callback = new DateRangeAdvOptCallback() {
                    @Override
                    public void setAdvancedOptions(AdvancedOptionsModel options) {
                        loggedIn = options.getLoggedIn();
                        startedQuiz = options.isStartedQuiz();
                        tookQuiz = options.isTookQuiz();
                        viewedLessons = options.isViewedLessons();
                        usedResources = options.isUsedResources();
                        registered = options.isRegistered();
                    }
                };
                AdvancedOptionsModel options = new AdvancedOptionsModel();

                options.setLoggedIn(loggedIn);
                options.setStartedQuiz(startedQuiz);
                options.setTookQuiz(tookQuiz);
                options.setViewedLessons(viewedLessons);
                options.setUsedResources(usedResources);
                options.setRegistered(registered);

                new DateRangeAdvancedOptionsDialog(callback, options).setVisible(true);
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

    class MyDatePicker extends DatePicker {
        @Override
        protected void updateMPMonth(int month) {
            super.updateMPMonth(month);
        }
        
        @Override
        protected void updateMPYear(int year) {
            super.updateMPYear(year);
        }
    }
    private Widget createFromPicker() {
        _fromPicker = new MyDatePicker ();
        _fromPicker.addHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                System.out.println("Mouse Down");
            }
        }, MouseDownEvent.getType());
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

    public static void doTest() {
        DateRangePickerDialog.showSharedInstance(new Date(), new Date(), new Callback() {
            @Override
            public void datePicked(Date from, Date to, FilterOptions options) {
                com.google.gwt.user.client.Window.alert("You Picked: " + from + ", " + to + ", " + options);
            }
        });
    }

}

class DatePickerWrapper extends BorderLayoutContainer {
    DatePicker picker;
    TextField _selected;

    public DatePickerWrapper(DatePicker picker, String title) {
        this.picker = picker;

        picker.addShowContextMenuHandler(new ShowContextMenuHandler() {
            
            @Override
            public void onShowContextMenu(ShowContextMenuEvent event) {
                System.out.println("Test");
            }
        });
        picker.setValue(new Date());
        
        picker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                _selected.setValue(asDateString(DatePickerWrapper.this.picker.getValue()));
                forceLayout();
            }

        });

        
        _selected = new TextField();
        _selected.setReadOnly(true);
        _selected.setValue(asDateString(this.picker.getValue()));

        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.getElement().setAttribute("style",  "padding: 10px");
        flow.add(new MyFieldLabel(_selected, title, 40, 100));
        setNorthWidget(flow, new BorderLayoutData(55));
        setCenterWidget(picker);
    }

    @SuppressWarnings("deprecation")
    private String asDateString(Date d) {
        return DateTimeFormat.getShortDateFormat().format(d);
    }

}
