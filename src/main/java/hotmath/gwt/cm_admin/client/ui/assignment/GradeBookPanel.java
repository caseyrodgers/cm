package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.ui.CmCellRendererBoolean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/** Shows grid of all students in assignment 
 * and the summary of the selected assignment.
 *  
 * @author casey
 *
 */
public class GradeBookPanel extends ContentPanel {

    private StudentAssignmentProperties saProps = GWT.create(StudentAssignmentProperties.class);

    Grid<StudentAssignment> _gradebookGrid;
    List<ColumnConfig<StudentAssignment, ?>> colConfList;
    ColumnModel<StudentAssignment> colMdl;
    ColumnConfig<StudentAssignment, String> detailStatus;    
    ListStore<StudentAssignment> _store;
    
    public GradeBookPanel(){
        super.setHeadingText("Status");

        colConfList = new ArrayList<ColumnConfig<StudentAssignment, ?>>();
        initColumns();
        colMdl = new ColumnModel<StudentAssignment>(colConfList);
        
        _store = new ListStore<StudentAssignment>(saProps.uid());
        
        addRefreshButton();
        addGradeButton();
        
        if(CmShared.getQueryParameter("debug") != null) {
            addLoadCmStudentButton();
        }
        
        //showDefaultMessage();
        //addAcceptAllButton();
    }

    private void addRefreshButton() {
        addTool(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showGradeBookFor(_lastUsedAssignment);
            }
        }));
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
                
                FlowLayoutContainer scrollWrapper = new FlowLayoutContainer();
                scrollWrapper.setScrollMode(ScrollMode.AUTO);
                scrollWrapper.add(_gradebookGrid);
                setWidget(_gradebookGrid);
                forceLayout();
            }
        }.register();                
    }

    private void showAssignmentGrading() {
        if(_gradebookGrid == null) {
            CmMessageBox.showAlert("Select an assignment first");
        }
        else {
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
    }


	private void initColumns() {
        colConfList.add(new ColumnConfig<StudentAssignment, String>(saProps.studentName(), 200, "Student"));
        
        ColumnConfig<StudentAssignment, Boolean> colGraded = new ColumnConfig<StudentAssignment, Boolean>(saProps.graded(), 50, "Graded");
        colGraded.setCell(new CmCellRendererBoolean());
        colGraded.setToolTip(SafeHtmlUtils.fromTrustedString("Has this user's grades been released"));
        colConfList.add(colGraded);
        
        colConfList.add(new ColumnConfig<StudentAssignment, String>(saProps.homeworkGrade(), 50, "Score"));
        
        colConfList.add(new ColumnConfig<StudentAssignment, Date>(saProps.turnInDate(), 75, "Turned In"));
        colConfList.get(colConfList.size()-1).setToolTip(SafeHtmlUtils.fromTrustedString("The date this user clicked the 'Turn In Assignment' button"));
        
        detailStatus = new ColumnConfig<StudentAssignment, String>(saProps.studentDetailStatus(), 150, "Details");
        colConfList.add(detailStatus);
	}

    
    private void addLoadCmStudentButton() {

        TextButton btn = new TextButton("Login as Student");
        btn.setToolTip("Create new window and login to CM Student");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(_gradebookGrid != null) {
                    StudentAssignment st = _gradebookGrid.getSelectionModel().getSelectedItem();
                    if(st != null) {
                        Window.open("/loginService?uid="+st.getUid() + "&debug=true", "_blank","menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
                    }
                }
            }
        });

        addTool(btn);
    }
    
    private void addGradeButton() {
    	TextButton grade = new TextButton("Grade");
    	grade.setToolTip("View and Grade the selected student's Assignment");
    	grade.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showAssignmentGrading();                
            }
        });
    	addTool(grade);
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


interface StudentAssignmentProperties extends PropertyAccess<String> {
    ModelKeyProvider<StudentAssignment> uid();
    ValueProvider<StudentAssignment, Date> turnInDate();
    ValueProvider<StudentAssignment, String> studentName();
    ValueProvider<StudentAssignment, String> homeworkStatus();
    ValueProvider<StudentAssignment, String> homeworkGrade();
    ValueProvider<StudentAssignment, String> studentDetailStatus();
    ValueProvider<StudentAssignment, Boolean> graded();
}