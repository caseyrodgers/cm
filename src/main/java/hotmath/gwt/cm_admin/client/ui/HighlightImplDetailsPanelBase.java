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
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
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

    public HighlightImplDetailsPanelBase(HighlightImplBase base) {
        this.base = base;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        add(new Label("Loading data from server ..."));
        getDataFromServer();
    }

    abstract public HighlightsGetReportAction.ReportType getReportType();
    
    protected ColumnModel getColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Student");
        column.setWidth(140);
        column.setSortable(false);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("data1");
        column.setHeader("Lessons Completed");
        column.setWidth(140);
        column.setSortable(false);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
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
        _grid = defineGrid(store, getColumns());
        if(data == null) {
            add(new NoRowsFoundPanel());
        }
        else {
            
            List<HighlightReportModel> reportList = new ArrayList<HighlightReportModel>();
            for (int i = 0, t = data.size(); i < t; i++) {
                reportList.add(createTableModel(data.get(i)));
            }
            store.add(reportList);
            add(_grid);
            _grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>(){
                public void handleEvent(BaseEvent be) {
                    showSelectStudentDetail();
                }
            });
        }
        
        layout();
    }
    
    protected HighlightReportModel createTableModel(HighlightReportData data) {
        if(data.getData() != null)
            return new HighlightReportModel(data.getUid(), data.getName(), data.getData());
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
        grid.setWidth("100%");
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

/** Class to encapsulate table data for report output
 * 
 *  NOTE: Really two models... one for individual and one for groups.
 *  
 * @author casey
 *
 */
class HighlightReportModel extends BaseModelData {
    int uid;
    
    public HighlightReportModel(Integer uid, String name, String data) {
        this.uid = uid;
        set("name", name);
        
        /** allow up to four data variables to be
         * passed separated by |.  Each value 
         * will be in data1, data2, etc..
         * 
         */
        if(data != null) {
            String ds[] = data.split("\\|");
            for(int i=0;i<ds.length;i++) {
                set("data" + (i+1), ds[i]);    
            }
        }
    }
    
    public HighlightReportModel(String name, int groupCnt, int schoolCount, int nationalCount) {
        set("name", name);
        set("group", groupCnt);
        set("school", schoolCount);
        set("national", nationalCount);
    }

    public String getName() {
        return get("name");
    }

    public String getData1() {
        return get("data1");
    }
    
    public String getData2() {
        return get("data2");
    }
    
    public String getData3() {
        return get("data3");
    }
    
    public String getData4() {
        return get("data4");
    }
    
    public Integer getUid() {
        return uid;
    }
}


class NoRowsFoundPanel extends LayoutContainer {
    public NoRowsFoundPanel() {
        setLayout(new CenterLayout());
        add(new Html("<h1>No Students Found</h1>"));
    }
}