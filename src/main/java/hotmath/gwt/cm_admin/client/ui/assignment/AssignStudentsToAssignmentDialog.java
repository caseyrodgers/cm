package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.rpc.AssignStudentsToAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;

public class AssignStudentsToAssignmentDialog extends GWindow {
    static AssignStudentsToAssignmentDialog __instance;
    
    
    ListView<StudentDto, String> _listView;
    private AssignStudentsToAssignmentDialog() {
        super(false);
        
        
        AssignStudentsProperties dp = GWT.create(AssignStudentsProperties.class);

        // Create the store that the contains the data to display in the grid
        ListStore<StudentDto> s = new ListStore<StudentDto>(dp.uid());
        
        // Create the tree using the store and value provider for the name field
        _listView = new ListView<StudentDto, String>(s, dp.name());
        
        setWidget(_listView);
        

        getHeader().addTool(createSelectAll());
        getHeader().addTool(createUnSelectAll());
        addButton(createAssignStudents());
        addCloseButton();

        setPixelSize(400,440);
        __instance = this;
    }
    
    
    
    private Widget createAssignStudents() {
        TextButton btn = new TextButton("Assign");
        btn.setToolTip("Assign selected students to current Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                List<StudentDto> selected = _listView.getSelectionModel().getSelectedItems();
                if(selected.size()>0) {
                    assignStudents(selected);
                }
            }
        });
        return btn;
    }

    private Widget createSelectAll() {
        TextButton btn = new TextButton("Select All");
        btn.setToolTip("Select all students in list.");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _listView.getSelectionModel().selectAll();
            }
        });
        return btn;        
    }
    
    private Widget createUnSelectAll() {
        TextButton btn = new TextButton("Unselect All");
        btn.setToolTip("Unselect all students in list.");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _listView.getSelectionModel().deselectAll();
            }
        });
        return btn;        
    }

    
    private void assignStudents(final List<StudentDto> students) {
        new RetryAction<AssignmentInfo>() {
            @Override
            public void attempt() {
                CmList<StudentDto> students2 = new CmArrayList<StudentDto>();
                students2.addAll(students);
                AssignStudentsToAssignmentAction action = new AssignStudentsToAssignmentAction(_assignment.getAssignKey(), students2);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(AssignmentInfo info) {
                if(info.getErrors()==0) {
                    Info.display("Students Assigned", info.getAssigned() + " student(s) saved.");
                }
                else {
                    MessageBox errorInfo = new MessageBox("Errors Assigning", "There were " + info.getErrors() + " error(s): \n\n" + info.getMessage());
                    errorInfo.setPixelSize(400, 300);
                    errorInfo.show();
                }
                
                _gradeBookPanel.showGradeBookFor(_assignment);
                
                
                hide();
            }

        }.register();         
    }


    GradeBookPanel _gradeBookPanel;
    Assignment _assignment;
    public void showDialog(GradeBookPanel gradeBookPanel, Assignment assignment) {
        _gradeBookPanel = gradeBookPanel;
        _assignment = assignment;
        show();
        
        List<StudentModelExt> studentsInGrid = StudentGridPanel.instance.getStudentsInGrid();
        _listView.getStore().clear();
        for(StudentModelExt se: studentsInGrid) {
            StudentDto student = new StudentDto(se.getUid(), se.getName());
            _listView.getStore().add(student);
        }
    }

    public static AssignStudentsToAssignmentDialog getInstance() {
        if(__instance == null) {
            __instance = new AssignStudentsToAssignmentDialog();
        }
        return __instance;
    }

}
