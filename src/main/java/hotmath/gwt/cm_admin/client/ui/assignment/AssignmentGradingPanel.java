package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class AssignmentGradingPanel extends ContentPanel {

    private StudentProblemProperties spProps = GWT.create(StudentProblemProperties.class);

    StudentAssignment _studentAssignment;
    Grid<StudentProblemDto> _gradingGrid;
    List<ColumnConfig<StudentProblemDto, ?>> colConfList;
    ColumnModel<StudentProblemDto> colMdl;
    ColumnConfig<StudentProblemDto, String> problemCol;
    ColumnConfig<StudentProblemDto, String> statusCol;
    ListStore<StudentProblemDto> _store;

    public AssignmentGradingPanel(StudentAssignment studentAssignment){
        super.setHeadingText("Problems for Student/Assignment");
        super.getHeader().setHeight("30px");

        problemCol = new ColumnConfig<StudentProblemDto, String>(spProps.pidLabel(), 120, "Problem");
        problemCol.setRowHeader(true);

        statusCol = new ColumnConfig<StudentProblemDto, String>(spProps.status(), 100, "Status");
        statusCol.setRowHeader(true);

        colConfList = new ArrayList<ColumnConfig<StudentProblemDto, ?>>();
        colConfList.add(problemCol);
        colConfList.add(statusCol);
        colMdl = new ColumnModel<StudentProblemDto>(colConfList);

        _studentAssignment = studentAssignment;
        List<StudentProblemDto> problems = _studentAssignment.getAssigmentStatuses();
        _store = new ListStore<StudentProblemDto>(spProps.pid());
        _store.addAll(problems);

        _gradingGrid = new Grid<StudentProblemDto>(_store, colMdl);
        _gradingGrid.setWidth(380);
        _gradingGrid.getView().setStripeRows(true);
        _gradingGrid.getView().setColumnLines(true);
        _gradingGrid.getView().setAutoExpandColumn(problemCol);
        
        setWidget(_gradingGrid);

    }

}
