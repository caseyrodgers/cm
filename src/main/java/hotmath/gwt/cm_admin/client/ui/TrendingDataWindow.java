package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAssessmentReportAction;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

//import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;


/**
 * Provide a window to display trending data
 * 
 * @author bob
 * 
 */
public class TrendingDataWindow extends GWindow {
    
    static TrendingDataWindow2 __instance;
    
    Integer adminId;
    TrendingDataWindowBarChart _barChart;
    FlowLayoutContainer container;

    TabPanel _tabPanel;
    CheckBox _onlyActiveCheckBox;

    public TrendingDataWindow(Integer adminId) {
        super(true);

        __instance = this;
        
        this.adminId = adminId;

        setHeadingText("Overview of Student Progress");
        setWidth(600);
        setHeight(600);

        container = new FlowLayoutContainer();

        getHeader().addTool(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                loadTrendDataAsync();
            }
        }));

        getHeader().addTool(new TextButton("Print Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                assessmentReportButton();
            }
        }));

        loadTrendDataAsync();

        
        /** Position button at left margin on button bar
         * 
         */
        //getButtonBar().setStyleAttribute("position", "relative");
        _onlyActiveCheckBox = new CheckBox();
        //_onlyActiveCheckBox.setStyleName("trending-data-checkbox");
        //_onlyActiveCheckBox.setStyleAttribute("position", "absolute");
        //_onlyActiveCheckBox.setStyleAttribute("left", "0");
        //_onlyActiveCheckBox.setStyleAttribute("top", "0");
        _onlyActiveCheckBox.setBoxLabel("Restrict data to current programs.");
        _onlyActiveCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                loadTrendDataAsync();
            }

        });

        getButtonBar().add(_onlyActiveCheckBox);

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

        container.add(_tabPanel);
    }

    private void assessmentReportButton() {
        GWT.runAsync(new CmRunAsyncCallback() {

            @Override
            public void onSuccess() {
            	GeneratePdfAssessmentReportAction action = new GeneratePdfAssessmentReportAction(adminId,StudentGridPanel.instance._pageAction);
            	action.setFilterMap(StudentGridPanel.instance._pageAction.getFilterMap());
                new PdfWindow(adminId, "Catchup Math Group Assessment Report", action);
            }
        });

    }

    CmAdminTrendingDataI _trendingData;

    private void loadTrendDataAsync() {

        if(StudentGridPanel.instance.getCurrentStudentCount() == 0) {
            showNoStudents();
            return;
        }
        
        new RetryAction<CmAdminTrendingDataI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetAdminTrendingDataAction action = new GetAdminTrendingDataAction(onlyActiveOrFullHistory(), adminId, StudentGridPanel.instance._pageAction);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            
            public void oncapture(CmAdminTrendingDataI trendingData) {
                _trendingData = trendingData;
                CmBusyManager.setBusy(false);
                
                drawGui();
                addTrendingChart(trendingData);
                //addProgramCharts(trendingData.getProgramData());
                setVisible(true);
            }
        }.register();
    }
    
    private void showNoStudents() {
    	container.clear();
        //removeAll();
    	CenterLayoutContainer clContainer = new CenterLayoutContainer();
        clContainer.add(new HTML("<h2>No students found</h2>"));
    	container.add(clContainer);
    }

    private GetAdminTrendingDataAction.DataType onlyActiveOrFullHistory() {
        if (_onlyActiveCheckBox != null && _onlyActiveCheckBox.getValue())
            return GetAdminTrendingDataAction.DataType.ONLY_ACTIVE;
        else 
            return GetAdminTrendingDataAction.DataType.FULL_HISTORY;
    }

    Widget _barChartWidget;

    private void addTrendingChart(CmAdminTrendingDataI trendingData) {
    	if (_barChartWidget != null) {
    		_barChartWidget.setVisible(false);
    		_barChartWidget.removeFromParent();
    		boolean removed = _tabPanel.remove(_barChartWidget);
    		Window.alert("removed barChartWidget: " + removed);
    	}
        TrendingDataWindowBarChart _barChart = new TrendingDataWindowBarChart("Most Prescribed Lessons", trendingData.getTrendingData());
        _barChartWidget = _barChart.asWidget();
        _tabPanel.add(_barChartWidget, "Most Prescribed Lessons");
    }

/*
    private void addProgramCharts(CmList<ProgramData> programData) {
        for (int i = 0, t = programData.size(); i < t; i++) {
            ProgramData pd = programData.get(i);
            TabItem ti = new TabItem(pd.getProgramName());

            final TrendingDataWindowChartBar2 bar = new TrendingDataWindowChartBar2();
            bar.setBarModelData(pd.getProgramName(), pd);
            ti.add(bar);

            _tabPanel.add(ti);
        }
    }
*/
    static {
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_STUDENT_GRID_FILTERED) {
                if(__instance != null && __instance.isVisible()) {
                    __instance.loadTrendDataAsync();
                }
            }
        }});
    }
}
