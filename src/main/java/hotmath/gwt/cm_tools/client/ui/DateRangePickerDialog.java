package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;

public class DateRangePickerDialog extends Window {
    
    static private DateRangePickerDialog __instance;
    static public DateRangePickerDialog showSharedInstance(Date from, Date to, Callback callback) {
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
        setSize(400,350);
        setHeading("Choose Date Range");
        setModal(true);
        setLayout(new RowLayout(Orientation.HORIZONTAL));
        
        RowData data = new RowData(.5, 1);
        data.setMargins(new Margins(5));
        
        add(createFromPicker(), data);
        add(createToPicker(), data);

        _defaultStartDate = CatchupMathAdmin.getInstance().getAccountInfoPanel().getModel().getAccountCreateDate();

        addButton(new Button("No Date Range", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                
                Date from = _defaultStartDate;
                Date to = new Date();
                
                DateRangePickerDialog.this.callback.datePicked(from, to);
                hide();
            }
        }));

        addButton(new Button("Select", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                
                Date from = _fromPicker.getValue();
                Date to = _toPicker.getValue();
                
                DateRangePickerDialog.this.callback.datePicked(from, to);
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
    
    public interface Callback {
        void datePicked(Date from, Date to);
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
        form.setLabelWidth(50);
        
        _selected = new TextField<String>();
        _selected.setFieldLabel(title);
        _selected.setReadOnly(true);
        _selected.setValue(asDateString(this.picker.getValue()));
        
        form.add(_selected);
        add(form, new BorderLayoutData(LayoutRegion.NORTH,35));
        add(picker, new BorderLayoutData(LayoutRegion.CENTER));
    }
    
    @SuppressWarnings("deprecation")
    private String asDateString(Date d) {
        return DateTimeFormat.getShortDateFormat().format(d);
    }
}