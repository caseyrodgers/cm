package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.UnassignStudentsFromAssignmentAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
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

    private static final StudentAssignmentProperties saProps = GWT.create(StudentAssignmentProperties.class);

    Grid<StudentAssignment> _gradebookGrid;
    List<ColumnConfig<StudentAssignment, ?>> colConfList;
    ColumnModel<StudentAssignment> colMdl;
    ColumnConfig<StudentAssignment, String> nameCol;
    private static GradeBookPanel _instance;
    
    public GradeBookPanel(){
        setHeadingText("Gradebook for selected Assignment");

        nameCol = new ColumnConfig<StudentAssignment, String>(saProps.studentName(), 30, "Student");

        colConfList = new ArrayList<ColumnConfig<StudentAssignment, ?>>();
        colConfList.add(nameCol);
        colMdl = new ColumnModel<StudentAssignment>(colConfList);
        
        ListStore<StudentAssignment> store = new ListStore<StudentAssignment>(saProps.uid());
        
        _gradebookGrid = new Grid<StudentAssignment>(store, colMdl);
        _gradebookGrid.setWidth(480);
        
        setWidget(_gradebookGrid);
        
        getHeader().addTool(createAssignButton());
        getHeader().addTool(createUnassignButton());
        
        _instance = this;
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
        final List<StudentAssignment> selected = _gradebookGrid.getSelectionModel().getSelectedItems();
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
        Info.display("GradeBookPanel", "readData(): assignKey: " + assignment.getAssignKey());
        new RetryAction<CmList<StudentAssignment>>() {
            @Override
            public void attempt() {
                GetAssignmentGradeBookAction action = new GetAssignmentGradeBookAction(assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<StudentAssignment> saList) {
            	reconfigure(saList);
                _gradebookGrid.getStore().clear();
                _gradebookGrid.getStore().addAll(saList);
                if (_instance.isEnabled() == false)
                    _instance.enable();
                if (_instance.hidden)
                    _instance.show();
            }

        }.register();                
    }

    /**
     * reconfigure the grid based on current assignment
     *
     * @param saList
     */
    private void reconfigure(CmList<StudentAssignment> saList) {
    	if (saList != null && saList.size() > 0) {
    		StudentAssignment sa = saList.get(0);
    		List<StudentProblemDto> asList = sa.getAssigmentStatuses();

    		colConfList.clear();
            colConfList.add(nameCol);

            int idx = 0;
            for (StudentProblemDto as : asList) {
                ColumnConfig<StudentAssignment, String> statusCol =
                		new ColumnConfig<StudentAssignment, String>(new StudentAssignmentStatusValueProvider(idx), 20,
                				as.getPidLabel());
                colConfList.add(statusCol);
                idx++;
            }
            colMdl = new ColumnModel<StudentAssignment>(colConfList);
            //Info.display("GradeBookPanel", "reconfigure() END: saList.size(): " + saList.size());
    	}
    	
    }

    private class StudentAssignmentStatusValueProvider extends Object implements ValueProvider<StudentAssignment, String> {

    	private StudentAssignment stuAssignment;
    	private int idx;

    	StudentAssignmentStatusValueProvider(int idx) {
    	    super();
    	    this.idx = idx;
    	}
    	
		@Override
		public String getValue(StudentAssignment object) {
        	return stuAssignment.getAssigmentStatuses().get(idx).getStatus();
		}

		@Override
		public void setValue(StudentAssignment object, String value) {
		}

		@Override
		public String getPath() {
			return null;
		}

    }

}
