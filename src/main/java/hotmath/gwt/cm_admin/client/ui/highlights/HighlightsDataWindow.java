package hotmath.gwt.cm_admin.client.ui.highlights;


import hotmath.gwt.cm_admin.client.ui.HighlightsIndividualPanel;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author casey
 * @author bob
 * 
 */
public class HighlightsDataWindow extends GWindow {

    private static HighlightsDataWindow __instance;

    BorderLayoutContainer _container;

    public static HighlightsDataWindow getSharedInstance(Integer aid) {
        //TODO: reuse __instance
        //if(__instance == null) {
            __instance = new HighlightsDataWindow();
        //}
        //else {
        //    __instance.reloadAllReports();
        //}
        __instance.setAdminId(aid);
        __instance.setVisible(true);
        
        return __instance;
    }

    Integer adminId;

    final String TITLE="Student Usage HighlightS";

    Label _dateRange = new Label();

    private HighlightsDataWindow() {
    	super(false);
        __instance = this;
        //addStyleName("highlights-data-window");
        setHeadingText(TITLE);
        setWidth(650);
        setHeight(500);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        BorderLayoutData westData = new BorderLayoutData();
        westData.setSize(210);
        westData.setCollapsible(true);
        westData.setFloatable(true);

        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setSize(400);
        _container.setWestWidget(new HighlightsListPanel(_container, centerData), westData);

        refreshDateRangeLabel();

        getHeader().addTool(new TextButton("Refresh", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
                reloadAllReports();
			}
        }));

        getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent ce) {
                reportButton();
            }
        }));

        //_dateRange.addStyleName("date-range-label");
        getButtonBar().add(_dateRange);
        super.addCloseButton();

        showDefaultMsg();

        /**
         * turn on after data retrieved
         * 
         */
        setWidget(_container);

        setVisible(true);
    }

	private void showDefaultMsg() {
		CenterLayoutContainer clc = new CenterLayoutContainer();
        HTML defaultMsg = new HTML("Select a report");
        clc.add(defaultMsg);
        _container.setCenterWidget(clc);
        _container.forceLayout();
	}

	private void refreshDateRangeLabel() {
		_dateRange.setText("Date range: " + DateRangePanel.getInstance().formatDateRange());
	}

    int _currentSelection;
    private void reloadAllReports() {
        super.clear();
        _container.setCenterWidget(new HighlightsIndividualPanel());
        super.forceLayout();
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
