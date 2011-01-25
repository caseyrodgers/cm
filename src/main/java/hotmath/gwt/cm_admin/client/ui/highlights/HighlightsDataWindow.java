package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.Date;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
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
    
    private HighlightsDataWindow() {
        __instance = this;
        addStyleName("highlights-data-window");
        setHeading(TITLE);
        setWidth(600);
        setHeight(500);

        setLayout(new FitLayout());
        add(new HighlightsIndividualPanel());

        getHeader().addTool(new Button("Set Date Range", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                DateRangePickerDialog.showSharedInstance(new DateRangePickerDialog.Callback() {
                    @Override
                    public void datePicked(Date from, Date to) {
                        _from = from;
                        _to = to;
                        if(from != null) {
                            DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd"); 
                            HighlightsDataWindow.this.setHeading(TITLE + ": " + format.format(from) + " - " + format.format(to));
                        }
                        else {
                            HighlightsDataWindow.this.setHeading(TITLE);
                        }
                    }
                });
            }
        }));


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
        
        /**
         * turn on after data retrieved
         * 
         */
        setVisible(true);
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
