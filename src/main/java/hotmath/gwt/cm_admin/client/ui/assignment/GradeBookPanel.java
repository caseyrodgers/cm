package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentLessonDto;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class GradeBookPanel extends ContentPanel {

    private StudentAssignmentProperties saProps = GWT.create(StudentAssignmentProperties.class);

    Grid<StudentAssignment> _gradebookGrid;
    List<ColumnConfig<StudentAssignment, ?>> colConfList;
    ColumnModel<StudentAssignment> colMdl;
    ColumnConfig<StudentAssignment, String> nameCol;
    ColumnConfig<StudentAssignment, String> statusCol;
    ColumnConfig<StudentAssignment, String> gradeCol;
    ColumnConfig<StudentAssignment, String> detailStatus;    
    ListStore<StudentAssignment> _store;
    
    public GradeBookPanel(){
        super.setHeadingText("Gradebook for selected Assignment");

        colConfList = new ArrayList<ColumnConfig<StudentAssignment, ?>>();
        initColumns();
        colMdl = new ColumnModel<StudentAssignment>(colConfList);
        
        _store = new ListStore<StudentAssignment>(saProps.uid());
        
        addGradeButton();
        //addAcceptAllButton();
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
                CmBusyManager.setBusy(true);

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
                _gradebookGrid.getView().setAutoExpandColumn(detailStatus);
                
                _gradebookGrid.addHandler(new DoubleClickHandler() {
                    @Override
                    public void onDoubleClick(DoubleClickEvent event) {
                        showAssignmentGrading();
                    }
                },DoubleClickEvent.getType());
                CmBusyManager.setBusy(false);
                setWidget(_gradebookGrid);
                forceLayout();
            }
        }.register();                
    }

    private void showAssignmentGrading() {
        final StudentAssignment studentAssignment = _gradebookGrid.getSelectionModel().getSelectedItem();
        if(studentAssignment != null) {
            new GradeBookDialog(studentAssignment, new CallbackOnComplete() {
                @Override
                public void isComplete() {
                	// reload all data
                	readData(_lastUsedAssignment);
                }
            });        
        }

    }

    /**
     * configure the grid based on current assignment
     *
     * @param saList
     */
    private void configureColumns(List<StudentAssignment> saList) {
        if(true)
            return;
        
        
        colConfList = new ArrayList<ColumnConfig<StudentAssignment, ?>>();

        if (saList != null && saList.size() > 0) {
    		StudentAssignment sa = saList.get(0);
    		List<StudentLessonDto> lessonList = sa.getLessonStatuses();

            initColumns();

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

	private void initColumns() {
		nameCol = new ColumnConfig<StudentAssignment, String>(saProps.studentName(), 120, "Student");
        nameCol.setRowHeader(true);

        statusCol = new ColumnConfig<StudentAssignment, String>(saProps.homeworkStatus(), 85, "Status");
        statusCol.setRowHeader(true);

        gradeCol = new ColumnConfig<StudentAssignment, String>(saProps.homeworkGrade(), 50, "Grade");
        gradeCol.setRowHeader(true);
        
        detailStatus = new ColumnConfig<StudentAssignment, String>(saProps.studentDetailStatus(), 150, "Details");
        detailStatus.setRowHeader(true);
        

        colConfList.add(nameCol);
        colConfList.add(statusCol);
        colConfList.add(gradeCol);
        colConfList.add(detailStatus);
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
    
    private void addGradeButton() {

    	TextButton btn = new TextButton("Grade");
    	btn.setToolTip("View and Grade the selected student's Assignment");
    	btn.addSelectHandler(new SelectHandler() {
    		@Override
    		public void onSelect(SelectEvent event) {
    			showAssignmentGrading();
    		}
    	});

    	addTool(btn);
    }

    private void addAcceptAllButton() {
        TextButton btn = new TextButton("Accept");
        btn.setToolTip("Accept the selected student's assignment.");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                closeSelectedAssignment();
            }
        });

        addTool(btn);
    }
    
    private void closeSelectedAssignment() {
        final Assignment data = _lastUsedAssignment;
        if(data != null) {
            
            if(!data.getStatus().equals("Close")) {
                final ConfirmMessageBox cm = new ConfirmMessageBox("Accept Assignment", "Are you sure you want to accept this assignment?");
                cm.addHideHandler(new HideHandler() {
                    @Override
                    public void onHide(HideEvent event) {
                        if (cm.getHideButton() == cm.getButtonById(PredefinedButton.YES.name())) {
                            acceptAssignment(data);
                        }
                    }
                });
                cm.setVisible(true);
                
            }
        }
    }

    private void acceptAssignment(final Assignment ass) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
//                CmBusyManager.setBusy(true);
//                CloseAssignmentAction action = new CloseAssignmentAction(UserInfoBase.getInstance().getUid(), ass.getAssignKey());
//                setAction(action);
//                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                Log.debug("Assignment accepted successfully: " + data);
            }
        }.register();        
    }
}
