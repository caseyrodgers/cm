package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CCSSCoverageBar;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageChartDataAction;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageChartDataAction.ReportType;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAssessmentReportAction;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;


/**
 * Window to display a CCSS Coverage Bar Chart
 * 
 * @author bob
 * 
 */
public class CCSSCoverageChartWindow extends GWindow {
    
    static CCSSCoverageChartWindow __instance;
    
    int _adminId;
    int _uid;
    boolean _isGroup;
    CCSSCoverageBarChart _barChart;
    BorderLayoutContainer container;

    TabPanel _tabPanel;
    CheckBox _onlyActiveCheckBox;

    public CCSSCoverageChartWindow(int adminId, int uid, boolean isGroup, String name) {
        super(false);

        __instance = this;
        
        this._adminId = adminId;
        this._uid = uid;
        this._isGroup = isGroup;

        setHeadingText("CCSS Charts" + ((name!=null)?" for " + name:""));
        setWidth(600);
        setHeight(600);

        container = new BorderLayoutContainer();
        BorderLayoutData blData = new BorderLayoutData();
        container.setLayoutData(blData);

        getHeader().addTool(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                getDataFromServer();
            }
        }));

        if(CmGwtUtils.getQueryParameter("debug") != null)
            getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    reportButton();
                }
            }));

        getDataFromServer();
        
        super.addCloseButton();

        /**
         * turn on after data retrieved
         * 
         */
        setWidget(container);
        setVisible(true);
    }

    private void drawGui() {
    	container.clear();
        _tabPanel = new TabPanel();
        _tabPanel.setAnimScroll(true);
        _tabPanel.setTabScroll(true);
        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				container.forceLayout();
			}
        	
        });
        container.setCenterWidget(_tabPanel);
    }

    private void reportButton() {
    	/*
        GWT.runAsync(new CmRunAsyncCallback() {

            @Override
            public void onSuccess() {
            	GeneratePdfAssessmentReportAction action = new GeneratePdfAssessmentReportAction(_adminId,StudentGridPanel.instance.getPageAction());
            	action.setFilterMap(StudentGridPanel.instance.getPageAction().getFilterMap());
                new PdfWindow(_adminId, "Catchup Math Group Assessment Report", action);
            }
        });
        */

    }

	protected void getDataFromServer() {
        new RetryAction<CmList<CCSSCoverageBar>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                Date fromDate = DateRangePanel.getInstance()!=null?DateRangePanel.getInstance().getFromDate():null;
                Date toDate = DateRangePanel.getInstance()!=null?DateRangePanel.getInstance().getToDate():null;
                CCSSCoverageChartDataAction action = new CCSSCoverageChartDataAction(getReportType(), _uid, fromDate, toDate);
                action.setAdminId(_adminId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

			@Override
            public void oncapture(CmList<CCSSCoverageBar> allData) {
                CmBusyManager.setBusy(false);
                drawGui();
                if (allData != null && allData.size() > 0)
                    addCharts(allData);
                else
                	showNoData();
                setVisible(true);
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
                drawGui();
                showNoData();
                setVisible(true);
            }
        }.register();
    }

    CmAdminTrendingDataI _trendingData;

    private ReportType getReportType() {
		return (_isGroup == true) ? ReportType.GROUP_CUMULATIVE_CHART : ReportType.STUDENT_CUMULATIVE_CHART;
	}

    private void showNoData() {
    	container.clear();
        //removeAll();
    	CenterLayoutContainer clContainer = new CenterLayoutContainer();
    	FramedPanel fp = new FramedPanel();
    	fp.setHeaderVisible(false);
    	fp.add(new HTML("<h2 style='color:red'>No data found</h2>"));
        clContainer.add(fp);
    	container.add(clContainer);
    	container.forceLayout();
    }

    Widget _chartWidget;

    private void addCharts(List<CCSSCoverageBar> data) {
    	if (_chartWidget != null) {
    		_chartWidget.setVisible(false);
    		_chartWidget.removeFromParent();
    		_tabPanel.remove(_chartWidget);
    	}

    	String title = createTitle(data);
    	
        CCSSCoverageBarChart _barChart = new CCSSCoverageBarChart(title, data);
        _chartWidget = _barChart.asWidget();
        _tabPanel.add(_chartWidget, "Activity");

        CCSSCoverageUniqueBarChart _uniqueBarChart = new CCSSCoverageUniqueBarChart(title, data);
        _chartWidget = _uniqueBarChart.asWidget();
        _tabPanel.add(_chartWidget, "Unique");

    }

    private String createTitle(List<CCSSCoverageBar> data) {
    	String title = "";
    	if (data != null && data.size() > 0) {
    		CCSSCoverageBar bar = data.get(0);
    		title = bar.getLabel();
    		if (data.size() > 1) {
    			title += " to " + data.get(data.size()-1).getLabel();
    		}
    	}
    	return title;

    }
    static {
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_STUDENT_GRID_FILTERED) {
                if(__instance != null && __instance.isVisible()) {
                    __instance.getDataFromServer();
                }
            }
        }});
    }
}
