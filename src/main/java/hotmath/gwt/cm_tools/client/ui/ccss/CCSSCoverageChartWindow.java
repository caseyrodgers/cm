package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
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

    public CCSSCoverageChartWindow(int adminId, int uid, boolean isGroup) {
        super(false);

        __instance = this;
        
        this._adminId = adminId;
        this._uid = uid;
        this._isGroup = isGroup;

        setHeadingText("CCSS Coverage Bar Chart");
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
                addChart(allData);
                setVisible(true);
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
                drawGui();
                addChart(null);
                setVisible(true);
            }
        }.register();
    }

    CmAdminTrendingDataI _trendingData;

    private ReportType getReportType() {
		return (_isGroup == true) ? ReportType.GROUP_WEEKLY_CHART : ReportType.STUDENT_WEEKLY_CHART;
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
    }

    Widget _chartWidget;

    private void addChart(CmList<CCSSCoverageBar> data) {
    	if (_chartWidget != null) {
    		_chartWidget.setVisible(false);
    		_chartWidget.removeFromParent();
    		_tabPanel.remove(_chartWidget);
    	}
    	//CCSSCoverageChartData ccssData = new CCSSCoverageChartData(data);
    	//List<CCSSCoverageBar> list = ccssData.getData();
        CCSSCoverageBarChart _barChart = new CCSSCoverageBarChart("CCSS Coverage", data);
        _chartWidget = _barChart.asWidget();
        _tabPanel.add(_chartWidget, "CCSS Coverage by Week");
    }

    List<Widget> _programChartWidgetList = new ArrayList<Widget>();
    
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
