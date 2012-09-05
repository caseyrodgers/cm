package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.UnassignStudentsFromAssignmentAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;

public class GradeBookPanel extends ContentPanel {

    Grid<StudentAssignment> _studentGrid;
    
    public GradeBookPanel(){
        setHeadingText("Students Assigned to this Assignment");
        
        
        
        ValueProvider<StudentAssignment, String> v = new ValueProvider<StudentAssignment, String>() {

            @Override
            public String getValue(StudentAssignment object) {
                return object.getStudentName();
            }

            @Override
            public void setValue(StudentAssignment object, String value) {
                object.setStudentName(value);
            }

            @Override
            public String getPath() {
                return "/";
            }
            
        };        
        ColumnConfig<StudentAssignment, String> nameCol = new ColumnConfig<StudentAssignment, String>(v, 200, "Student");
     
        List<ColumnConfig<StudentAssignment, ?>> l = new ArrayList<ColumnConfig<StudentAssignment, ?>>();
        l.add(nameCol);
        ColumnModel<StudentAssignment> cm = new ColumnModel<StudentAssignment>(l);
     
        
        ModelKeyProvider<? super StudentAssignment> kp = new ModelKeyProvider<StudentAssignment>() {
            @Override
            public String getKey(StudentAssignment item) {
                return String.valueOf(item.getUid());
            }
        };
        
        ListStore<StudentAssignment> store = new ListStore<StudentAssignment>(kp);
        
        _studentGrid = new Grid<StudentAssignment>(store, cm);
        _studentGrid.setWidth(300);
        
        setWidget(_studentGrid);
        
        getHeader().addTool(createAssignButton());
        getHeader().addTool(createUnassignButton());
    }
    
    
    private Widget createAssignButton() {
        TextButton btn = new TextButton("Assign");
        btn.setToolTip("Assign students to the selected Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                AssignStudentsToAssignmentDialog.getInstance().showDialog(GradeBookPanel.this,_lastUsedAssignment);
            }
        });
        return btn;
    }
    
    private Widget createUnassignButton() {
        TextButton btn = new TextButton("Unassign");
        btn.setToolTip("Unassign students from the selected Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                unassignSelectedStudents();
            }
        });
        return btn;
    }
    
    private void unassignSelectedStudents() {
        final List<StudentAssignment> selected = _studentGrid.getSelectionModel().getSelectedItems();
        if(selected.size() == 0) {
            return;
        }

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                UnassignStudentsFromAssignmentAction action = new UnassignStudentsFromAssignmentAction(_lastUsedAssignment.getAssignKey());
                action.getStudents().addAll(selected);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData result) {
                if(!result.getDataAsString("status").equals("OK")) {
                    CatchupMathTools.showAlert("Error unassigning students");
                }
                else {
                    showGradeBookFor(_lastUsedAssignment);
                    Info.display("Students Unassigned", selected.size() + " student(s) unassigned.");
                }
            }

        }.register();                

        
    }
    
    
    
    Assignment _lastUsedAssignment;
    public void showGradeBookFor(Assignment assignment) {
        _lastUsedAssignment = assignment;
        readData(_lastUsedAssignment);
    }
    
    private void readData(final Assignment assignment) {
        new RetryAction<CmList<StudentAssignment>>() {
            @Override
            public void attempt() {
                GetAssignmentGradeBookAction action = new GetAssignmentGradeBookAction(assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<StudentAssignment> students) {
                _studentGrid.getStore().clear();
                _studentGrid.getStore().addAll(students);
            }

        }.register();                
    }

}
