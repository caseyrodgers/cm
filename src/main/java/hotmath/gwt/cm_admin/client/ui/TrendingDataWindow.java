package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.BaseModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAssessmentReportAction;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
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

    public TrendingDataWindow(Integer adminId) {
        __instance = this;
        
        this.adminId = adminId;

        setHeading("Student Assessment");
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

        /**
         * turn on after data retieved
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
                List<Integer> studentUids = new ArrayList<Integer>();
                new PdfWindow(adminId, "Catchup Math Group Assessment Report", new GeneratePdfAssessmentReportAction(adminId,StudentGridPanel.instance._pageAction));
            }
        });

    }

    CmAdminTrendingDataI _trendingData;

    private void loadTrendDataAsync() {
        CmBusyManager.setBusy(true);

        CmServiceAsync service = (CmServiceAsync) Registry.get("cmService");
        service.execute(new GetAdminTrendingDataAction(adminId, StudentGridPanel.instance._pageAction),
                new CmAsyncCallback<CmAdminTrendingDataI>() {
                    public void onSuccess(CmAdminTrendingDataI trendingData) {
                        _trendingData = trendingData;
                        List<TrendingDataExt> dataExt = new ArrayList<TrendingDataExt>();
                        for (int i = 0, t = trendingData.getTrendingData().size(); i < t; i++) {
                            dataExt.add(new TrendingDataExt(trendingData.getTrendingData().get(i)));
                        }


                        drawGui();
                        
                        addTrendingChart(trendingData);
                        addProgramCharts(trendingData.getProgramData());
                        
                        setVisible(true);
                        layout(true);
                        CmBusyManager.setBusy(false);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        
                        removeAll();
                        setLayout(new CenterLayout());
                        caught.printStackTrace();
                        add(new Label(caught.getMessage()));
                        layout();
                        
                        CmBusyManager.setBusy(false);
                    }
                });
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

    private Grid<TrendingDataExt> defineGrid(final ListStore<TrendingDataExt> store, ColumnModel cm) {
        final Grid<TrendingDataExt> grid = new Grid<TrendingDataExt>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.setWidth("410px");
        grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);
        return grid;
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId(TrendingDataExt.LESSON_NAME_KEY);
        column.setHeader("Lesson Name");
        column.setWidth(235);
        column.setSortable(true);
        configs.add(column);

        ColumnConfig pass = new ColumnConfig();
        pass.setId(TrendingDataExt.LESSON_ASSIGN_KEY);
        pass.setHeader("Assigned Count");
        pass.setWidth(150);
        pass.setSortable(true);
        configs.add(pass);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
    
    private void showUsersWhoHaveGoneThroughProgram(String data) {
        CatchupMathTools.showAlert("Gone through program: " + data);
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

class TrendingDataExt extends BaseModel implements Serializable {
    static final String LESSON_NAME_KEY = "lesson-name";
    static final String LESSON_ASSIGN_KEY = "lesson-assign-count";

    public TrendingDataExt(TrendingData td) {
        set(LESSON_NAME_KEY, td.getLessonName());
        set(LESSON_ASSIGN_KEY, td.getCountAssigned());
    }
}