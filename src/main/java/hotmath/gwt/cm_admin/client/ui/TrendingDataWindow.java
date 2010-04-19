package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAssessmentReportAction;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.GWT;

/**
 * Provide a window to display trending data
 * 
 * @author casey
 * 
 */
public class TrendingDataWindow extends CmWindow {
    
    static TrendingDataWindow __instance;
    
    Integer adminId;
    TrendingDataWindowChartBar2 _chartBar2;

    TabPanel _tabPanel;
    CheckBox _onlyActiveCheckBox;

    public TrendingDataWindow(Integer adminId) {
        __instance = this;
        
        this.adminId = adminId;

        setHeading("Overview of Student Progress");
        setWidth(600);
        setHeight(500);

        setLayout(new FillLayout());

        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));

        getHeader().addTool(new Button("Refresh", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                loadTrendDataAsync();
            }
        }));

        getHeader().addTool(new Button("Print Report", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                assessmentReportButton();
            }
        }));

        loadTrendDataAsync();

        
        /** Position button at left margin on button bar
         * 
         */
        getButtonBar().setStyleAttribute("position", "relative");
        _onlyActiveCheckBox = new CheckBox();
        _onlyActiveCheckBox.setStyleAttribute("position", "absolute");
        _onlyActiveCheckBox.setStyleAttribute("left", "0");
        _onlyActiveCheckBox.setStyleAttribute("top", "0");
        _onlyActiveCheckBox.setBoxLabel("Restrict data to current programs.");
        _onlyActiveCheckBox.addListener(Events.OnClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                loadTrendDataAsync();
            }
        });
        
        
        getButtonBar().add(_onlyActiveCheckBox);
        
        
        /**
         * turn on after data retrieved
         * 
         */
        setVisible(true);
    }
    
    private void drawGui() {
        removeAll();
        setLayout(new FillLayout());
        _tabPanel = new TabPanel();
        _tabPanel.setAnimScroll(true);
        _tabPanel.setTabScroll(true);

        add(_tabPanel);
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
                addProgramCharts(trendingData.getProgramData());
                setVisible(true);
                layout(true);
            }
        }.register();
    }

    private GetAdminTrendingDataAction.DataType onlyActiveOrFullHistory() {
        if(_onlyActiveCheckBox != null && _onlyActiveCheckBox.getValue())
          return GetAdminTrendingDataAction.DataType.ONLY_ACTIVE;
        else 
            return GetAdminTrendingDataAction.DataType.FULL_HISTORY;
    }

    
    private void addTrendingChart(CmAdminTrendingDataI trendingData) {
        TabItem ti = new TabItem("Most Prescribed Lessons");
        TrendingDataWindowChartBar chart = new TrendingDataWindowChartBar();
        ti.add(chart);
        chart.setModelData("Most Prescribed Lessons", trendingData.getTrendingData());
        _tabPanel.add(ti);
    }

    private void addProgramCharts(CmList<ProgramData> programData) {
        for (int i = 0, t = programData.size(); i < t; i++) {
            ProgramData pd = programData.get(i);
            TabItem ti = new TabItem(pd.getProgramName());

            final TrendingDataWindowChartBar2 bar = new TrendingDataWindowChartBar2();
            bar.setBarModelData(pd.getProgramName(), pd);
            ti.add(bar);

            _tabPanel.add(ti);
        }
        layout();
    }

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
