package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.Element;


public class HighlightImplGreatestEffortDetailsPanel extends LayoutContainer {
    HighlightImplBase base;
    Grid<GreatestEffortReportModel> _grid;
    public HighlightImplGreatestEffortDetailsPanel(HighlightImplBase base) {
        this.base = base;
    }
    
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        
        add(new Label("Loading data from server ..."));
        getDataFromServer();
    }
    
    protected void getDataFromServer() {
        new RetryAction<CmList<HighlightReportData>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                HighlightsGetReportAction action = new HighlightsGetReportAction(HighlightsGetReportAction.ReportType.GREATEST_EFFORT, 
                        StudentGridPanel.instance._cmAdminMdl.getId(), 
                        HighlightsDataWindow._from,
                        HighlightsDataWindow._to
                        );
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<HighlightReportData> allLessons) {
                drawTable(allLessons);
                CmBusyManager.setBusy(false);                
            }
        }.register();        
    }
    
    private void drawTable(CmList<HighlightReportData> data) {
         removeAll();
         
         ListStore<GreatestEffortReportModel> store = new ListStore<GreatestEffortReportModel>();
         _grid = defineGrid(store,defineColumns());

         List<GreatestEffortReportModel> reportList = new ArrayList<GreatestEffortReportModel>();
         for(int i=0,t=data.size();i<t;i++) {
             reportList.add(new GreatestEffortReportModel(data.get(0).getName(), data.get(0).getData()));
         }
         store.add(reportList);
         add(_grid);
         layout();
    }
    
    private Grid<GreatestEffortReportModel> defineGrid(final ListStore<GreatestEffortReportModel> store, ColumnModel cm) {
        final Grid<GreatestEffortReportModel> grid = new Grid<GreatestEffortReportModel>(store, cm);
        grid.setAutoExpandColumn("text");
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModelExt>>() {
            public void handleEvent(final SelectionEvent<StudentModelExt> se) {
                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    //
                }
            }
        });
        grid.setWidth("100%");
        grid.setHeight("300px");
        grid.setLoadMask(true);
        return grid;
    }
    
    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Student");
        column.setWidth(140);
        column.setSortable(true);
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("data");
        column.setHeader("Data");
        column.setWidth(140);
        column.setSortable(true);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }    
}

class GreatestEffortReportModel extends BaseModelData {
    public GreatestEffortReportModel(String name, String data) {
        set("name", name);
        set("data", data);
    }
    
    public String getName() {
        return get("name");
    }
    
    public String getData() {
        return get("data");
    }
}