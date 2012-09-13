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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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

    private StudentAssignmentProperties saProps = GWT.create(StudentAssignmentProperties.class);

    Grid<StudentAssignment> _gradebookGrid;
    List<ColumnConfig<StudentAssignment, ?>> colConfList;
    ColumnModel<StudentAssignment> colMdl;
    ColumnConfig<StudentAssignment, String> nameCol;
    ListStore<StudentAssignment> _store;
    
    public GradeBookPanel(){
        setHeadingText("Gradebook for selected Assignment");

        nameCol = new ColumnConfig<StudentAssignment, String>(saProps.studentName(), 100, "Student");
        nameCol.setRowHeader(true);

        colConfList = new ArrayList<ColumnConfig<StudentAssignment, ?>>();
        colConfList.add(nameCol);
        colMdl = new ColumnModel<StudentAssignment>(colConfList);
        
        _store = new ListStore<StudentAssignment>(saProps.uid());
        
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

                _store = new ListStore<StudentAssignment>(saProps.uid());
                _store.addAll(saList);
                
            	configureColumns(saList);

                _gradebookGrid = new Grid<StudentAssignment>(_store, colMdl);
                _gradebookGrid.setWidth(480);
                _gradebookGrid.getView().setStripeRows(true);
                _gradebookGrid.getView().setColumnLines(true);
                _gradebookGrid.getView().setAutoExpandColumn(nameCol);
                
                setWidget(_gradebookGrid);
            }

            
            
        }.register();                
    }

    /**
     * configure the grid based on current assignment
     *
     * @param saList
     */
    private void configureColumns(CmList<StudentAssignment> saList) {
        colConfList = new ArrayList<ColumnConfig<StudentAssignment, ?>>();

        if (saList != null && saList.size() > 0) {
    		StudentAssignment sa = saList.get(0);
    		List<StudentProblemDto> asList = sa.getAssigmentStatuses();

            nameCol = new ColumnConfig<StudentAssignment, String>(saProps.studentName(), 120, "Student");
            nameCol.setRowHeader(true);

            colConfList.add(nameCol);

            int idx = 0;
            for (StudentProblemDto as : asList) {
                ColumnConfig<StudentAssignment, String> statusCol =
                		new ColumnConfig<StudentAssignment, String>(new StudentAssignmentStatusValueProvider(idx), 60,
                				"Prob " + (idx+1));
                statusCol.setToolTip(new SafeHtmlBuilder().appendEscaped(as.getPidLabel()).toSafeHtml());
                colConfList.add(statusCol);
                idx++;
            }

    	}
        colMdl = new ColumnModel<StudentAssignment>(colConfList);
    	
    }

    private class StudentAssignmentStatusValueProvider extends Object implements ValueProvider<StudentAssignment, String> {

    	private int idx;

    	StudentAssignmentStatusValueProvider(int idx) {
    	    super();
    	    this.idx = idx;
    	}
    	
		@Override
		public String getValue(StudentAssignment stuAssignment) {
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
