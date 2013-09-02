package hotmath.gwt.cm_admin.client.ui.highlights;


import hotmath.gwt.cm_tools.client.ui.DateRangeWidget;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
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
    TextButton printButton;

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

    final String TITLE="Student Usage Highlights";
    
    BorderLayoutData _westData;
    BorderLayoutData _centerData;
    HighlightsListPanel _highlightsListPanel;

    DateRangeWidget _dateRange = DateRangeWidget.getInstance();

    private HighlightsDataWindow() {
    	super(false);
        __instance = this;
        //addStyleName("highlights-data-window");
        setHeadingText(TITLE);
        setWidth(650);
        setHeight(500);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _westData = new BorderLayoutData();
        _westData.setSize(210);
        _westData.setCollapsible(true);
        _westData.setFloatable(true);

        _centerData = new BorderLayoutData();
        _centerData.setSize(400);

        _highlightsListPanel = new HighlightsListPanel(_container, _centerData);
        _container.setWestWidget(_highlightsListPanel, _westData);

        _dateRange.refresh();

        getHeader().addTool(new TextButton("Refresh", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
                reloadAllReports();
			}
        }));

        printButton = new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent ce) {
                reportButton();
            }
        });
        getHeader().addTool(printButton);

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

    private void enablePrint() {
        printButton.enable();
        printButton.removeToolTip();
    }

    private void disablePrint() {
        printButton.disable();
        printButton.setToolTip("A printable report is not defined for this highlight.");
    }

	private void showDefaultMsg() {
		CenterLayoutContainer clc = new CenterLayoutContainer();
        HTML defaultMsg = new HTML("<h1>Select a report</h1>");
        clc.add(defaultMsg);
        _container.setCenterWidget(clc);
        _container.forceLayout();
	}

    int _currentSelection;
    private void reloadAllReports() {
        _container.getCenterWidget().removeFromParent();
        _container.getWestWidget().removeFromParent();
    	_highlightsListPanel = new HighlightsListPanel(_container, _centerData);
    	
        _container.setWestWidget(_highlightsListPanel, _westData);
        _dateRange.refresh();
        
        showDefaultMsg();
        _container.forceLayout();
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
                        __instance.reloadAllReports();
                    }
                }
            }
        });
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_DISABLE_HIGHLIGHT_PRINT) {
                    if(__instance != null && __instance.isVisible()) {
                        __instance.disablePrint();
                    }
                }
            }
        });
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_ENABLE_HIGHLIGHT_PRINT) {
                    if(__instance != null && __instance.isVisible()) {
                        __instance.enablePrint();
                    }
                }
            }
        });
    }
}
