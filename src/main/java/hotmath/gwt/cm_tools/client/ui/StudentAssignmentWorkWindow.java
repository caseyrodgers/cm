package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWorkForUserAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.ui.CmCellRendererBoolean;

import java.util.ArrayList;
import java.util.Date;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class StudentAssignmentWorkWindow extends GWindow {
    
    private StudentModelI _student;

    DateRangeWidget _dateRangeWidget = DateRangeWidget.getInstance();

    Grid<StudentAssignment> _grid;
    GridProperties _props;

    public StudentAssignmentWorkWindow(StudentModelI student) {
        super(false);
        _student = student;
        setHeadingText("Assignment Work for: " + student.getName());
        setPixelSize(500, 300);
        setResizable(false);
        super.addCloseButton();
        setVisible(true);
        createGrid();
        readDataFromServer();
        forceLayout();
    }

    private void readDataFromServer() {

    	CmBusyManager.setBusy(true);

        new RetryAction<CmList<StudentAssignment>>() {
            @Override
            public void attempt() {
            	Date fromDate = _dateRangeWidget.getFromDate();
            	Date toDate = _dateRangeWidget.getToDate();
                GetAssignmentWorkForUserAction action = new GetAssignmentWorkForUserAction(_student.getUid());
                action.setFromDate(fromDate);
                action.setToDate(toDate);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<StudentAssignment> list) {
            	try {
                    _grid.getStore().clear();
                    _grid.getStore().addAll(list);
                    _grid.setLoadMask(false);
                    forceLayout();
                } catch (Exception e) {
                    Log.error("Error: " + list.size(), e);
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }
        }.attempt();
    }

    private void createGrid() {
        _props = GWT.create(GridProperties.class);
        ArrayList<ColumnConfig<StudentAssignment, ?>> columns = new ArrayList<ColumnConfig<StudentAssignment, ?>>();
        columns.add(new ColumnConfig<StudentAssignment, Date>(_props.dueDate(), 65, "Due Date"));
        ColumnConfig<StudentAssignment, Boolean> colGraded = new ColumnConfig<StudentAssignment, Boolean>(_props.graded(), 50, "Graded");
        colGraded.setCell(new CmCellRendererBoolean());
        columns.add(colGraded);
        columns.add(new ColumnConfig<StudentAssignment, String>(_props.homeworkGrade(), 50, "Score"));
        columns.add(new ColumnConfig<StudentAssignment, String>(_props.studentDetailStatus(), 100, "Details"));
        columns.add(new ColumnConfig<StudentAssignment, String>(_props.comments(), 150, "Comments"));
        ColumnModel<StudentAssignment> cols = new ColumnModel<StudentAssignment>(columns);
        ListStore<StudentAssignment> store = new ListStore<StudentAssignment>(_props.assignKey());
        _grid = new Grid<StudentAssignment>(store, cols);
        _grid.setLoadMask(true);
        _grid.getView().setAutoExpandColumn(cols.getColumn(0));
        _grid.getView().setAutoFill(true);
        setWidget(_grid);
    }
    
    interface GridProperties extends PropertyAccess<String> {
        ModelKeyProvider<StudentAssignment> assignKey();
        ValueProvider<StudentAssignment, Date> dueDate();
        ValueProvider<StudentAssignment, String> comments();
        ValueProvider<StudentAssignment, String> homeworkStatus();
        ValueProvider<StudentAssignment, String> homeworkGrade();
        ValueProvider<StudentAssignment, String> studentDetailStatus();
        ValueProvider<StudentAssignment, Boolean> graded();
    }

    public static void startTest() {
        int uid = 28001;
        StudentModelI sm = new StudentModel();
        sm.setUid(uid);
        sm.setName("Test User");
        new StudentAssignmentWorkWindow(sm);
    }
    
}
