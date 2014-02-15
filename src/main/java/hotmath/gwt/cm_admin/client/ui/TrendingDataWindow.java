package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
 * Provide a window to display trending data
 * 
 * @author casey
 * @author bob
 * 
 */
public class TrendingDataWindow extends GWindow {
    
    static TrendingDataWindow __instance;
    
    Integer adminId;
    TrendingDataWindowBarChart _barChart;
    BorderLayoutContainer container;

    TabPanel _tabPanel;
    CheckBox _onlyActiveCheckBox;

    public TrendingDataWindow(Integer adminId) {
        super(false);

        __instance = this;
        
        this.adminId = adminId;

        setHeadingText("Overview of Student Progress");
        setWidth(600);
        setHeight(600);

        container = new BorderLayoutContainer();
        BorderLayoutData blData = new BorderLayoutData();
        container.setLayoutData(blData);

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
        _onlyActiveCheckBox = new CheckBox();
		_onlyActiveCheckBox.getElement().getStyle().setMarginLeft(10, Unit.PX);
        _onlyActiveCheckBox.setBoxLabel("Restrict data to current programs.");
        _onlyActiveCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                loadTrendDataAsync();
            }

        });

        getButtonBar().add(_onlyActiveCheckBox);
        
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
                //addAreaChart();
                addLessonsChart(trendingData);
                addProgramCharts(trendingData.getProgramData());
                setVisible(true);
            }
        }.register();
    }
    
    private void showNoStudents() {
    	container.clear();
        //removeAll();
    	CenterLayoutContainer clContainer = new CenterLayoutContainer();
    	FramedPanel fp = new FramedPanel();
    	fp.setHeaderVisible(false);
    	fp.add(new HTML("<h2 style='color:red'>No students found</h2>"));
        clContainer.add(fp);
    	container.add(clContainer);
    }

    private GetAdminTrendingDataAction.DataType onlyActiveOrFullHistory() {
        if (_onlyActiveCheckBox != null && _onlyActiveCheckBox.getValue())
            return GetAdminTrendingDataAction.DataType.ONLY_ACTIVE;
        else 
            return GetAdminTrendingDataAction.DataType.FULL_HISTORY;
    }

    Widget _lessonsChartWidget;

    private void addLessonsChart(CmAdminTrendingDataI trendingData) {
    	if (_lessonsChartWidget != null) {
    		_lessonsChartWidget.setVisible(false);
    		_lessonsChartWidget.removeFromParent();
    		_tabPanel.remove(_lessonsChartWidget);
    	}
    	boolean currentProgramOnly = (onlyActiveOrFullHistory() == GetAdminTrendingDataAction.DataType.ONLY_ACTIVE);
        TrendingDataWindowBarChart _trendingChart = new TrendingDataWindowBarChart("Most Prescribed Lessons", trendingData.getTrendingData(), currentProgramOnly);
        _lessonsChartWidget = _trendingChart.asWidget();
        _tabPanel.add(_lessonsChartWidget, "Most Prescribed Lessons");
    }

    List<Widget> _programChartWidgetList = new ArrayList<Widget>();
    
    private void addProgramCharts(CmList<ProgramData> programData) {
    	if (_programChartWidgetList.size() > 0) {
    		for (Widget w : _programChartWidgetList) {
        		w.setVisible(false);
        		w.removeFromParent();
        		_tabPanel.remove(w);
    		}
    	}
    	_programChartWidgetList.clear();

    	for (int i = 0, t = programData.size(); i < t; i++) {
            ProgramData pd = programData.get(i);

            ProgramBarChart _programChart = new ProgramBarChart(pd, pd.getSegments());
            Widget w = _programChart.asWidget();
            _programChartWidgetList.add(w);
            _tabPanel.add(w, pd.getProgramName());
        }
    }
/*
    private void addAreaChart() {
    	AreaChartDemo demo = new AreaChartDemo();
    	Widget w = demo.asWidget();
    	_tabPanel.add(w, "Area Chart Demo");
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
