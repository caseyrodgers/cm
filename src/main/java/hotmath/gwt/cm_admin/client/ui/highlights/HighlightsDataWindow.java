package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.Date;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Provide a window to display trending data
 *
 * 
 * http://gridpapr.com/edit/974c01b2bde8d85ca580f5dd8e97fb4f
 * 
 * @author casey
 * 
 */
public class HighlightsDataWindow extends CmWindow {
    
    
    private static HighlightsDataWindow __instance;
    public static HighlightsDataWindow getSharedInstance(Integer aid) {
        if(__instance == null) {
            __instance = new HighlightsDataWindow();
        }
        else {
            __instance.reloadAllReports();
        }
        __instance.setAdminId(aid);
        __instance.setVisible(true);
        
        return __instance;
    }
    
    Integer adminId;

    final String TITLE="Student Usage Highlights";
    public static Date _from, _to;
    
    Button _dateRangeButton;
    Label _dateRange = new Label();
    
    private HighlightsDataWindow() {
        __instance = this;
        addStyleName("highlights-data-window");
        setHeading(TITLE);
        setWidth(750);
        setHeight(500);

        setLayout(new FitLayout());
        add(new HighlightsIndividualPanel());
        
        
        _from = CatchupMathAdmin.getInstance().getAccountInfoPanel().getModel().getAccountCreateDate();
        _to = new Date();

        _dateRangeButton = new Button("Set Date Range", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                showDatePicker();
            }
        });
        _dateRange.setText(formatDateRangeLabel(_from, _to));
        
        
        getHeader().addTool(_dateRangeButton);


        getHeader().addTool(new Button("Refresh", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                reloadAllReports();
            }
        }));

        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));

        
        
        getHeader().addTool(new Button("Print Report", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                reportButton();
            }
        }));
        
        /** Position button at left margin on button bar
         * 
         */
        getButtonBar().setStyleAttribute("position", "relative");
        _dateRange.setStyleAttribute("position", "absolute");
        _dateRange.setStyleAttribute("left", "0");
        _dateRange.setStyleAttribute("top", "0");
        _dateRange.setStyleAttribute("color", "blue");
        _dateRange.setStyleAttribute("padding", "5px 10px 0 0");
        _dateRange.addListener(Events.OnClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                showDatePicker();
            }
        });
        getButtonBar().add(_dateRange);                  
        
        /**
         * turn on after data retrieved
         * 
         */
        setVisible(true);
    }
    
    static final DateTimeFormat _dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
    private String formatDateRangeLabel(Date from, Date to) {
        return "Date range: " + _dateFormat.format(from) + " - " + _dateFormat.format(to);
    }

    private void showDatePicker() {
        DateRangePickerDialog.showSharedInstance(new DateRangePickerDialog.Callback() {
            @Override
            public void datePicked(Date from, Date to) {
                _from = (from != null)?from:_from;
                _to = (to != null)?to:_to;

                _dateRange.setText(formatDateRangeLabel(from, to));
                reloadAllReports();
            }
        });
    }

    int _currentSelection;
    private void reloadAllReports() {
        removeAll();
        add(new HighlightsIndividualPanel());
        layout();
    }
    
    private void setAdminId(Integer aid) {
        this.adminId = aid;
    }
    
    private void resetGui() {
        removeAll();
        setLayout(new FitLayout());
    }

    private void reportButton() {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_PRINT_HIGHLIGHT_REPORT));
    }


    static {
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_STUDENT_GRID_FILTERED) {
                if(__instance != null && __instance.isVisible()) {
                    __instance.reloadAllReports();
                }
            }
        }});
    }
}
