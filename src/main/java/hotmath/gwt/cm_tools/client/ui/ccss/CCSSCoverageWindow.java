package hotmath.gwt.cm_tools.client.ui.ccss;


import hotmath.gwt.cm_tools.client.model.StudentModelI;
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
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author bob
 * 
 */
public class CCSSCoverageWindow extends GWindow {

    private static CCSSCoverageWindow __instance;

    BorderLayoutContainer _container;

    Integer adminId;

    private static final String TITLE="CCSS Coverage for ";
    
    BorderLayoutData _westData;
    BorderLayoutData _centerData;
    CCSSCoverageListPanel _CCSSCoverageListPanel;
    int _groupId;
    StudentModelI _stuModel;

    Label _dateRange = new Label();

    public CCSSCoverageWindow(StudentModelI stuModel, int groupId) {
    	super(false);
        __instance = this;
        _stuModel = stuModel;
        _groupId = groupId;
        setHeadingText(TITLE + stuModel.getName());
        setWidth(535);
        setHeight(500);

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        _westData = new BorderLayoutData();
        _westData.setSize(210);
        _westData.setCollapsible(true);
        _westData.setFloatable(true);

        _centerData = new BorderLayoutData();
        _centerData.setSize(400);

        _CCSSCoverageListPanel = new CCSSCoverageListPanel(_container, _centerData, _stuModel.getUid(), 0);
        _container.setWestWidget(_CCSSCoverageListPanel, _westData);

        refreshDateRangeLabel();

        getHeader().addTool(new TextButton("Refresh", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
                reloadData();
			}
        }));
/*
        getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent ce) {
                reportButton();
            }
        }));
*/
        _dateRange.addStyleName("date-range-label");
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
        HTML defaultMsg = new HTML("<h1>Select a category</h1>");
        clc.add(defaultMsg);
        _container.setCenterWidget(clc);
        _container.forceLayout();
	}

	private void refreshDateRangeLabel() {
		_dateRange.setText("Date range: " + DateRangePanel.getInstance().formatDateRange());
	}

    int _currentSelection;
    private void reloadData() {
        _container.getCenterWidget().removeFromParent();
        _container.getWestWidget().removeFromParent();

        _CCSSCoverageListPanel = new CCSSCoverageListPanel(_container, _centerData, _stuModel.getUid(), 0);
        _container.setWestWidget(_CCSSCoverageListPanel, _westData);
        
        refreshDateRangeLabel();

        showDefaultMsg();
        _container.forceLayout();
    }

    private void setAdminId(Integer aid) {
        this.adminId = aid;
    }

    private void reportButton() {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_PRINT_CCSS_COVERAGE_REPORT));
    }


    static {
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_STUDENT_GRID_FILTERED) {
                    if(__instance != null && __instance.isVisible()) {
                        __instance.refreshDateRangeLabel();
                        __instance.reloadData();
                    }
                }
            }
        });
    }
}
