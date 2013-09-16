package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.GetUserActivityLogAction;
import hotmath.gwt.cm_tools.client.model.ActivityLogRecord;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class UserActivityLogDialog extends GWindow {
    
    private StudentModelI student;

    Grid<ActivityLogRecord> grid;
    GridProperties props;
    public UserActivityLogDialog(StudentModelI student) {
        super(true);
        this.student = student;
        setHeadingText("User Activity Log for: " + student.getName());
        setPixelSize(250,  300);
        setVisible(true);
        createGrid();
        readActivityLogFromServer();
    }
    
    private void readActivityLogFromServer() {
        new RetryAction<CmList<ActivityLogRecord>>() {
            @Override
            public void attempt() {
                GetUserActivityLogAction action = new GetUserActivityLogAction(student.getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<ActivityLogRecord> value) {
                grid.getStore().clear();
                grid.getStore().addAll(value);
                grid.setLoadMask(false);
            }
        }.attempt();
    }

    private void createGrid() {
        
        props = GWT.create(GridProperties.class);
        ArrayList<ColumnConfig<ActivityLogRecord, ?>> columns = new ArrayList<ColumnConfig<ActivityLogRecord, ?>>();
        columns.add(new ColumnConfig<ActivityLogRecord, String>(props.activityDay(), 75, "Date"));
        columns.add(new ColumnConfig<ActivityLogRecord, Integer>(props.activityTime(), 100, "Active Minutes"));
        ColumnModel<ActivityLogRecord> cols = new ColumnModel<ActivityLogRecord>(columns);
        ListStore<ActivityLogRecord> store = new ListStore<ActivityLogRecord>(props.key());
        grid = new Grid<ActivityLogRecord>(store, cols);
        grid.setLoadMask(true);
        setWidget(grid);
    }
    
    
    
    interface GridProperties extends PropertyAccess<String> {
        @Path("id")
        ModelKeyProvider<ActivityLogRecord> key();
        ValueProvider<ActivityLogRecord, Integer> activityTime();
        ValueProvider<ActivityLogRecord, String> activityDay();
    }



    public static void startTest() {
        int uid = 28001;
        StudentModelI sm = new StudentModel();
        sm.setUid(uid);
        sm.setName("Test User");
        new UserActivityLogDialog(sm);
    }
    
}
