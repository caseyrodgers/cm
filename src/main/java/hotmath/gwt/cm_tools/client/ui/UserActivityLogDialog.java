package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.GetUserActivityLogAction;
import hotmath.gwt.cm_tools.client.model.ActivityLogRecord;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class UserActivityLogDialog extends GWindow {
    
    private StudentModelI student;

    DateRangeWidget _dateRangeWidget = DateRangeWidget.getInstance();

    Grid<ActivityLogRecord> grid;
    GridProperties props;

    private FramedPanel _mainFrame = new FramedPanel();
    public UserActivityLogDialog(StudentModelI student) {
        super(true);
        this.student = student;
        setHeadingText("Active Time Log for: " + student.getName());
        setPixelSize(300, 300);
        setResizable(true);
        setDraggable(true);
        
        _mainFrame.setHeaderVisible(false);
        _mainFrame.setWidget(createGrid());
        
        readActivityLogFromServer();

        setWidget(_mainFrame);
        
        setVisible(true);
    }

    private void readActivityLogFromServer() {
        new RetryAction<CmList<ActivityLogRecord>>() {
            @Override
            public void attempt() {
            	Date fromDate = _dateRangeWidget.getFromDate();
            	Date toDate = _dateRangeWidget.getToDate();
                GetUserActivityLogAction action = new GetUserActivityLogAction(student.getUid(), fromDate, toDate);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<ActivityLogRecord> value) {
                grid.getStore().clear();
                grid.getStore().addAll(value);
                grid.setLoadMask(false);
                forceLayout();
            }
        }.attempt();
    }

    private Widget createGrid() {
        props = GWT.create(GridProperties.class);
        ArrayList<ColumnConfig<ActivityLogRecord, ?>> columns = new ArrayList<ColumnConfig<ActivityLogRecord, ?>>();
        
        ColumnConfig<ActivityLogRecord, String> cell = new ColumnConfig<ActivityLogRecord, String>(props.activityDayLabel(), 75, "Date");
        columns.add(cell);
        columns.add(new ColumnConfig<ActivityLogRecord, Integer>(props.activeMinutes(), 100, "Active Minutes"));
        ColumnModel<ActivityLogRecord> cols = new ColumnModel<ActivityLogRecord>(columns);
        ListStore<ActivityLogRecord> store = new ListStore<ActivityLogRecord>(props.key());
        grid = new Grid<ActivityLogRecord>(store, cols);
        grid.setLoadMask(true);
        grid.getView().setAutoExpandColumn(cols.getColumn(0));
        grid.getView().setAutoFill(true);
        
        return grid;
    }
    
    
    
    interface GridProperties extends PropertyAccess<String> {
        ModelKeyProvider<ActivityLogRecord> key();
        ValueProvider<ActivityLogRecord, String> activityDayLabel();
        ValueProvider<ActivityLogRecord, Integer> activeMinutes();
        ValueProvider<ActivityLogRecord, String> activityDay();
    }



    public static void startTest() {
        
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                int uid = 678546;
                StudentModelI sm = new StudentModel();
                sm.setUid(uid);
                sm.setName("Test User");
                new UserActivityLogDialog(sm);
            }
        });
    }
    
}
