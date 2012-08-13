package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
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

public class GradeBookPanel extends ContentPanel {

    Grid<StudentDto> _studentGrid;
    
    public GradeBookPanel(){
        setHeadingText("Students Assigned to this Assignment");
        
        
        
        ValueProvider<StudentDto, String> v = new ValueProvider<StudentDto, String>() {

            @Override
            public String getValue(StudentDto object) {
                return object.getName();
            }

            @Override
            public void setValue(StudentDto object, String value) {
                object.setName(value);
            }

            @Override
            public String getPath() {
                return "/";
            }
            
        };        
        ColumnConfig<StudentDto, String> nameCol = new ColumnConfig<StudentDto, String>(v, 200, "Student");
     
        List<ColumnConfig<StudentDto, ?>> l = new ArrayList<ColumnConfig<StudentDto, ?>>();
        l.add(nameCol);
        ColumnModel<StudentDto> cm = new ColumnModel<StudentDto>(l);
     
        
        ModelKeyProvider<StudentDto> kp = new ModelKeyProvider<StudentDto>() {
            @Override
            public String getKey(StudentDto item) {
                return "" + item.getUid();
            }
        };
        
        ListStore<StudentDto> store = new ListStore<StudentDto>(kp);
        
        _studentGrid = new Grid<StudentDto>(store, cm);
        _studentGrid.setWidth(300);
        
        setWidget(_studentGrid);
        
        
        getHeader().addTool(createAssignButton());
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
    
    Assignment _lastUsedAssignment;
    public void showGradeBookFor(Assignment assignment) {
        _lastUsedAssignment = assignment;
        readData(_lastUsedAssignment);
    }
    
    private void readData(final Assignment assignment) {
        new RetryAction<CmList<StudentDto>>() {
            @Override
            public void attempt() {
                GetAssignmentGradeBookAction action = new GetAssignmentGradeBookAction(assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<StudentDto> students) {
                _studentGrid.getStore().clear();
                _studentGrid.getStore().addAll(students);
            }

        }.register();                
    }

}
