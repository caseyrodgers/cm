package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
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

    Label _dateRange = new Label();

    private HighlightsDataWindow() {
        __instance = this;
        addStyleName("highlights-data-window");
        setHeading(TITLE);
        setWidth(650);
        setHeight(500);

        setLayout(new FitLayout());
        add(new HighlightsIndividualPanel());

        refreshDateRangeLabel();

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

        _dateRange.addStyleName("date-range-label");
        getButtonBar().add(_dateRange);                  

        /**
         * turn on after data retrieved
         * 
         */
        setVisible(true);
    }

	private void refreshDateRangeLabel() {
		_dateRange.setText("Date range: " + DateRangePanel.getInstance().formatDateRange());
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

    private void reportButton() {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_PRINT_HIGHLIGHT_REPORT));
    }


    static {
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_STUDENT_GRID_FILTERED) {
                    if(__instance != null && __instance.isVisible()) {
                        __instance.refreshDateRangeLabel();
                        __instance.reloadAllReports();
                    }
                }
            }
        });
    }
}
