package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

abstract public class HighlightImplDetailsPanelBase extends LayoutContainer {
    
    HighlightImplBase base;
    Grid<HighlightReportModel> _grid;
    HighlightImplDetailsPanelBase __instance;
    public HighlightImplDetailsPanelBase(HighlightImplBase base) {
        __instance = this;
        this.base = base;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        getDataFromServer();
    }

    abstract public HighlightsGetReportAction.ReportType getReportType();
    
    protected List<ColumnConfig> getColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Students with one or more logins");
        column.setWidth(300);
        column.setSortable(false);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("data1");
        column.setHeader("Lessons Viewed");
        column.setWidth(130);
        column.setSortable(false);
        configs.add(column);
        
        return configs;
    }
    
    
    public String[][] getReportValues() {
        
        ListStore<HighlightReportModel> reportStore = _grid.getStore();
        
        List<String[]> rows = new ArrayList<String[]>();
        for(int r=0, t=reportStore.getCount();r<t;r++) {
            HighlightReportModel rowM = reportStore.getAt(r);
            
            List<String> row = new ArrayList<String>();
            for(int c=0, ct=_grid.getColumnModel().getColumnCount();c<ct;c++) {
                ColumnConfig config = _grid.getColumnModel().getColumn(c);
                row.add( rowM.get(config.getDataIndex()).toString());
            }
            
            rows.add(row.toArray(new String[row.size()]));
        }
        return rows.toArray(new String[rows.size()][]);
    }
    
    
    protected void getDataFromServer() {
        new RetryAction<CmList<HighlightReportData>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                HighlightsGetReportAction action = new HighlightsGetReportAction(StudentGridPanel.instance._pageAction, getReportType(),
                        StudentGridPanel.instance._cmAdminMdl.getId(), HighlightsDataWindow._from,
                        HighlightsDataWindow._to);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<HighlightReportData> allLessons) {
                CmBusyManager.setBusy(false);
                drawTable(allLessons);
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
                drawTable(null);
            }
        }.register();
    }

    static public CmList<HighlightReportData> __lastReportData;
    private void drawTable(CmList<HighlightReportData> data) {
        __lastReportData = data;
        
        removeAll();
        
        setLayout(new FitLayout());
        ListStore<HighlightReportModel> store = new ListStore<HighlightReportModel>();
        _grid = defineGrid(store, new ColumnModel(getColumns()));
        if(data == null || data.size() == 0) {
            add(new NoRowsFoundPanel());
        }
        else {
            
            List<HighlightReportModel> reportList = new ArrayList<HighlightReportModel>();
            for (int i = 0, t = data.size(); i < t; i++) {
                reportList.add(createTableModel(data.get(i)));
            }
            
            store.add(reportList);
            add(_grid);
            
            _grid.setToolTip(getGridToolTip());
            _grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>(){
                public void handleEvent(BaseEvent be) {
                    showSelectStudentDetail();
                }
            });
        }
        
        layout();
    }
    
    protected String getGridToolTip() {
        return "Double click for detailed history";
    }
    
    protected HighlightReportModel createTableModel(HighlightReportData data) {
        if(data.getData() != null)
            return new HighlightReportModel(data.getUid(), data.getName(), data.getData(),data.getQuizzesTaken());
        else 
            return new HighlightReportModel(data.getName(), data.getGroupCount(), data.getSchoolCount(), data.getDbCount());
        
    }

    private Grid<HighlightReportModel> defineGrid(final ListStore<HighlightReportModel> store, ColumnModel cm) {
        final Grid<HighlightReportModel> grid = new Grid<HighlightReportModel>(store, cm);
        grid.setAutoExpandColumn("name");
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        
        /** set to default to allow IE to render table correctly
         * 
         */
        grid.setWidth("500");
        grid.setHeight("100%");
        grid.setLoadMask(true);
        return grid;
    }
    
    protected void showSelectStudentDetail() {

        final HighlightReportModel model = _grid.getSelectionModel().getSelectedItem();
        if(model.getUid() == 0)
            return;
        
        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetStudentModelAction action = new GetStudentModelAction(model.getUid());
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }            
            
            @Override
            public void oncapture(StudentModelI result) {
                CmBusyManager.setBusy(false);
                StudentModelExt sm = new StudentModelExt(result);
                new StudentDetailsWindow(sm);
            }
        }.register();
    }

}



/** Shown when no students meet criteria
 * 
 * @author casey
 *
 */
class NoRowsFoundPanel extends LayoutContainer {
    public NoRowsFoundPanel() {
        setLayout(new CenterLayout());
        add(new Html("<h1 style='font-size: 1.2em;margin: 10px;padding: 10px'>No students meet the criteria for this display.<br/>Please 'mouseover' the menu item for details.</h1>"));
    }
}