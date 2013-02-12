package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_admin.client.ui.StudentGridProperties;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightReportLayout;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.core.client.Style.SelectionMode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

abstract public class HighlightsImplDetailsPanelBase extends SimpleContainer {
    
    HighlightsImplBase base;
    Grid<HighlightReportData> _grid;
    HighlightsImplDetailsPanelBase __instance;

    interface GridProperties extends PropertyAccess<HighlightReportData> {
    	@Path("uid")
        ModelKeyProvider<HighlightReportData> id();

        ValueProvider<HighlightReportData, Integer> activities();

        ValueProvider<HighlightReportData, Integer> active();

        ValueProvider<HighlightReportData, Integer> games();

        @Path("groupCount")
        ValueProvider<HighlightReportData, Integer> group();

        @Path("lessonsViewed")
        ValueProvider<HighlightReportData, Integer> lessons();

        @Path("loginCount")
        ValueProvider<HighlightReportData, Integer> logins();

        ValueProvider<HighlightReportData, String> name();

        @Path("dbCount")
        ValueProvider<HighlightReportData, Integer> national();

        ValueProvider<HighlightReportData, Integer> quizzesPassed();

        ValueProvider<HighlightReportData, Integer> quizzesTaken();

        @Path("schoolCount")
        ValueProvider<HighlightReportData, Integer> school();

        ValueProvider<HighlightReportData, Integer> videos();

    }
/*
 *     int groupCount;
    int schoolCount;
    int dbCount;
 */
    GridProperties _gridProps = GWT.create(GridProperties.class);
    
    public HighlightsImplDetailsPanelBase(HighlightsImplBase base) {
        __instance = this;
        this.base = base;
        getDataFromServer();
    }

//    @Override
//    protected void onRender(Element parent, int index) {
//        super.onRender(parent, index);
//        getDataFromServer();
//    }

    abstract public HighlightsGetReportAction.ReportType getReportType();
    
    protected ColumnModel<HighlightReportData> getColumns() {
        List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

        ColumnConfig<HighlightReportData, String> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Students with one or more logins");
        column.setWidth(300);
        column.setSortable(false);
        cols.add(column);

        column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 140, "Lessons Viewed");
        column.setWidth(130);
        column.setSortable(false);
        cols.add(column);
        
        return new ColumnModel<HighlightReportData>(cols);
    }
    
    
    public String[][] getReportValues() {
        
        try {
            ListStore<HighlightReportData> reportStore = _grid.getStore();
            
            List<String[]> rows = new ArrayList<String[]>();
            for(int r=0, t=reportStore.getAll().size();r<t;r++) {
                HighlightReportData rowM = reportStore.get(r);
                
                List<String> row = new ArrayList<String>();
                for(int c=0, ct=_grid.getColumnModel().getColumnCount();c<ct;c++) {
                    ColumnConfig<HighlightReportData, String> col = _grid.getColumnModel().getColumn(c);
                    try {
                    	// TODO: get actual value
                        Object data = null; //rowM.get(col.getDataIndex());
                        if(data == null) {
                            data = 0;
                        }
                        row.add(data.toString());
                    }
                    catch(Exception ee) {
                        ee.printStackTrace();
                    }
                }
                
                rows.add(row.toArray(new String[row.size()]));
            }
            return rows.toArray(new String[rows.size()][]);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    protected void getDataFromServer() {
        new RetryAction<CmList<HighlightReportData>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                HighlightsGetReportAction action = new HighlightsGetReportAction(StudentGridPanel.instance.getPageAction(), getReportType(),
                        StudentGridPanel.instance.getCmAdminMdl().getUid(),
                        DateRangePanel.getInstance().getFromDate(),
                        DateRangePanel.getInstance().getToDate());
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
        
        //removeAll();
        
        //setLayout(new FitLayout());
        ListStore<HighlightReportData> store = new ListStore<HighlightReportData>(_gridProps.id());
        _grid = defineGrid(store, getColumns());
        if(data == null || data.size() == 0) {
            add(new NoRowsFoundPanel());
        }
        else {
            
            for (int i = 0, t = data.size(); i < t; i++) {
                //reportList.add(createTableModel(data.get(i)));
                store.add(data.get(i));
            }
            
            add(_grid);
            
            String tip = getGridToolTip();
            if(tip != null)
                _grid.setToolTip(tip);
            //_grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>(){
            //    public void handleEvent(BaseEvent be) {
            //        showSelectStudentDetail();
            //    }
            //});
        }
        
        //layout();
    }
    
    protected String getGridToolTip() {
        return "Double click for detailed history";
    }
/*    
    protected HighlightsReportData createTableModel(HighlightReportData data) {
        if(data.getData() != null)
            return new HighlightsReportModel(data.getUid(), data.getName(), data.getData(),data.getQuizzesTaken());
        else 
            return new HighlightsReportModel(data.getName(), data.getGroupCount(), data.getSchoolCount(), data.getDbCount());
        
    }
*/
    private Grid<HighlightReportData> defineGrid(final ListStore<HighlightReportData> store, ColumnModel<HighlightReportData> cm) {
        final Grid<HighlightReportData> grid = new Grid<HighlightReportData>(store, cm);
        //grid.setAutoExpandColumn("name");
        grid.setBorders(true);
        //grid.setStripeRows(true);

        grid.getView().setAutoExpandColumn(cm.findColumnConfig("name"));
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //grid.getSelectionModel().setFiresEvents(true);
        
        /** set to default to allow IE to render table correctly
         * 
         */
        grid.setWidth("500");
        grid.setHeight("100%");
        grid.setLoadMask(true);
        return grid;
    }
    
    protected void showSelectStudentDetail() {

        final HighlightReportData model = _grid.getSelectionModel().getSelectedItem();
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
class NoRowsFoundPanel extends SimpleContainer {
    public NoRowsFoundPanel() {
        //setLayout(new CenterLayout());
        add(new HTML("<h1 style='font-size: 1.2em;margin: 10px;padding: 10px'>No students meet the criteria for this display.<br/>Please 'mouseover' the menu item for details.</h1>"));
    }
}