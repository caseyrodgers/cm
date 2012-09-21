package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentLessonDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
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
    ColumnConfig<StudentAssignment, String> statusCol;
    ListStore<StudentAssignment> _store;
    
    public GradeBookPanel(){
        super.setHeadingText("Gradebook for selected Assignment");
        super.getHeader().setHeight("30px");

        nameCol = new ColumnConfig<StudentAssignment, String>(saProps.studentName(), 120, "Student");
        nameCol.setRowHeader(true);

        statusCol = new ColumnConfig<StudentAssignment, String>(saProps.homeworkStatus(), 100, "Status");
        statusCol.setRowHeader(true);

        colConfList = new ArrayList<ColumnConfig<StudentAssignment, ?>>();
        colConfList.add(nameCol);
        colConfList.add(statusCol);
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
    private void configureColumns(List<StudentAssignment> saList) {
        colConfList = new ArrayList<ColumnConfig<StudentAssignment, ?>>();

        if (saList != null && saList.size() > 0) {
    		StudentAssignment sa = saList.get(0);
    		List<StudentLessonDto> lessonList = sa.getLessonStatuses();

            nameCol = new ColumnConfig<StudentAssignment, String>(saProps.studentName(), 120, "Student");
            nameCol.setRowHeader(true);

            statusCol = new ColumnConfig<StudentAssignment, String>(saProps.homeworkStatus(), 100, "Status");
            statusCol.setRowHeader(true);

            colConfList.add(nameCol);
            colConfList.add(statusCol);

            int idx = 0;
            for (StudentLessonDto lesson : lessonList) {
                ColumnConfig<StudentAssignment, String> statusCol =
                		new ColumnConfig<StudentAssignment, String>(new StudentAssignmentLessonStatusValueProvider(idx), 120,
                				lesson.getLessonName());
                statusCol.setToolTip(new SafeHtmlBuilder().appendEscaped(lesson.getLessonName()).toSafeHtml());
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

    private class StudentAssignmentLessonStatusValueProvider extends Object implements ValueProvider<StudentAssignment, String> {

    	private int idx;

    	StudentAssignmentLessonStatusValueProvider(int idx) {
    	    super();
    	    this.idx = idx;
    	}
    	
		@Override
		public String getValue(StudentAssignment stuAssignment) {
        	return stuAssignment.getLessonStatuses().get(idx).getStatus();
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
