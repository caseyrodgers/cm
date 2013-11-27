package hotmath.gwt.cm_admin.client.ui.assignment;



import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.rpc.ActivateAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.CloseAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.CopyAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.DeleteAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsCreatedAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUngradedWhiteboardProblemsForAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.ReleaseAssignmentGradesAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.assignment.ExportGradebooksDialog;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageForAssignmentWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;


/** Provide GUI to list assignments and any related meta data
 * 
 * @author casey
 *
 */
public class AssignmentsContentPanel extends ContentPanel {
    
    Grid<Assignment> _grid;
    
    GroupDto _currentGroup;

    int _adminId;

    public interface Callback {
        void showAssignmentStatus(Assignment assignment);
    }

    public interface GradebookButtonCallback {
        void enable(boolean enable);
    }

    Callback _callBack;
    GradebookButtonCallback _gbCallback;
    
    /** Contains grid of assignments
     * 
     * @param gradeBookPanel
     */
    public AssignmentsContentPanel(Callback callback, GradebookButtonCallback gbCallback) {
        super.setHeadingText("Assignments");
        
        _callBack = callback;
        _gbCallback = gbCallback;

        _adminId = UserInfoBase.getInstance().getUid();
    
        
        addTool(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                refreshData();
            }
        }));
        addTool(createNewButton());
        addTool(createEditButton());
        // getHeader().addTool(createCloseButton());
        addTool(createDelButton());
        addTool(createCopyButton());
        addTool(new HTML("&nbsp;&nbsp;"));
        addTool(createActivateButton());
        addTool(createScoreButton());
        addTool(createCCSSButton());
        setCollapsible(false);

        AssignmentProperties props = GWT.create(AssignmentProperties.class);

        List<ColumnConfig<Assignment, ?>> l = new ArrayList<ColumnConfig<Assignment, ?>>();
        l.add(new ColumnConfig<Assignment, Date>(props.dueDate(), 120, "Due Date"));
        l.add(new ColumnConfig<Assignment, String>(props.status(), 75, "Status"));
        l.add(new ColumnConfig<Assignment, String>(props.gradedStatus(), 75, "Graded"));
        l.get(l.size()-1).setToolTip(SafeHtmlUtils.fromTrustedString("How many students have been graded for this assignment"));
        l.add(new ColumnConfig<Assignment, Integer>(props.problemCount(), 75, "Problems"));
        l.get(l.size()-1).setToolTip(SafeHtmlUtils.fromTrustedString("The number of problems in the assignment"));
        l.add(new ColumnConfig<Assignment, String>(props.comments(),50, "Comments"));
        ColumnModel<Assignment> cm = new ColumnModel<Assignment>(l);        

        // Create the store that the contains the data to display in the grid
        ListStore<Assignment> store = new ListStore<Assignment>(props.key());
                
        _grid = new Grid<Assignment>(store, cm);
        
        _grid.getView().setAutoExpandColumn(l.get(l.size()-1));
        _grid.getView().setStripeRows(true);
        _grid.getView().setColumnLines(true);

        _grid.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                editCurrentAssignment();
            }
        },DoubleClickEvent.getType());
        setWidget(new DefaultGxtLoadingPanel("Select a group to see its assignments"));
    }

    private Widget createActivateButton() {
        TextButton changeStatus =  new TextButton("Action");
        Menu menu = new Menu();
        MenuItem btnActive = new MenuItem("Activate", new SelectionHandler<MenuItem>() {
            
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                Assignment a = _grid.getSelectionModel().getSelectedItem();
                if(a == null) {
                    CmMessageBox.showAlert("An assignment needs to be selected first.");
                    return;
                }
                activateAssignment(a);
            }
        });
        btnActive.setToolTip("Activate the selected assignment.");
        menu.add(btnActive);
        MenuItem btnClose = new MenuItem("Close", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                closeSelectedAssignment();
            }
        });
        btnClose.setToolTip("Close the selected assignment disallowing future student changes.");
        menu.add(btnClose);
        
        MenuItem btnRelease = new MenuItem("Release Grade", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                releaseSelectedAssignmentGrades();
            }
        });
        btnClose.setToolTip("Close and allow students to see the selected assignment's grades.");
        menu.add(btnRelease);
        
        

        changeStatus.setMenu(menu);
        return changeStatus;
    }
    
    

    protected void releaseSelectedAssignmentGrades() {
        
        final Assignment assignment = _grid.getSelectionModel().getSelectedItem();
        if(assignment == null) {
            CmMessageBox.showAlert("You need to select an assignment first.");
            return;
        }
        
        if(assignment.isDraft()) {
            CmMessageBox.showAlert("You cannot release the grades of a Draft assignment");
            return;
        }
        

        CmBusyManager.setBusy(true);

        /** Ask server how many ungraded students there are */
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                GetUngradedWhiteboardProblemsForAssignmentAction action = new GetUngradedWhiteboardProblemsForAssignmentAction(assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData rpcData) {
                CmBusyManager.setBusy(false);
                releaseGradesDirect(assignment,  rpcData.getDataAsInt("count"));
            }
        }.register();                        
    }
    
    private void releaseGradesDirect(final Assignment assignment, int countUngradedStuWhiteboardProbs) {
        String msg = "Are you sure you want to close and release this assignment's grades?";
        String wbMsg = "with submitted whiteboard answers that you have not yet graded.<br/><br/>";
        if(countUngradedStuWhiteboardProbs == 1) {
            wbMsg = "There is 1 student " + wbMsg;
        }
        else if(countUngradedStuWhiteboardProbs > 1) {
            wbMsg = "There are " + countUngradedStuWhiteboardProbs + " students " + wbMsg;
        }
        else {
            wbMsg = "";
        }
        
        msg = wbMsg + msg;
        final ConfirmMessageBox cm = new ConfirmMessageBox("Report Grades", msg);
        cm.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (cm.getHideButton() == cm.getButtonById(PredefinedButton.YES.name())) {
                    releaseGradesForAssignment(assignment);
                }
            }
        });
        cm.setVisible(true);
    }

    protected void releaseGradesForAssignment(final Assignment assignment) {

        CmBusyManager.setBusy(true);
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                ReleaseAssignmentGradesAction action = new ReleaseAssignmentGradesAction(assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData saList) {
                CmBusyManager.setBusy(false);                
                Info.display("Infomation", "Grades Release");
                CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.ASSIGNMENTS));
            }
        }.register();                        
    }

    
    
    protected void activateAssignment(final Assignment assignment) {
        if(assignment.getStatus().equals("Open")) {
            CmMessageBox.showAlert("This assignment is already open.");
            return;
        }
        
        final ConfirmMessageBox cm = new ConfirmMessageBox("Activate Assignment", "Once activated an assignment's problems and assigned students cannot be changed.  Are you sure you want to activate this assignment?");
        cm.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (cm.getHideButton() == cm.getButtonById(PredefinedButton.YES.name())) {
                    activateAssignmentRPC(assignment);
                }
            }
        });
        cm.setWidth(300);
        cm.show();
    }

    private void activateAssignmentRPC(final Assignment assignment) {
        CatchupMathTools.setBusy(true);
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                ActivateAssignmentAction action = new ActivateAssignmentAction(assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                CatchupMathTools.setBusy(false);
                refreshData();
            }

        }.register();        
    }
    public void refreshData() {
        loadAssignmentsFor(_currentGroup);
    }

    public void loadAssignmentsFor(GroupDto group) {
        setWidget(_grid);
        _currentGroup = group;
        readAssignmentData(group);
    }
    
    private void readAssignmentData(final GroupDto group) {
        
        CatchupMathTools.setBusy(true);
        new RetryAction<CmList<Assignment>>() {
            @Override
            public void attempt() {
                GetAssignmentsCreatedAction action = new GetAssignmentsCreatedAction(UserInfoBase.getInstance().getUid(), group.getGroupId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<Assignment> assignments) {
                CatchupMathTools.setBusy(false);
                
                if(assignments.size() == 0) {
                    showNoAssignedProblemsMessage();
                    _gbCallback.enable(false);
                }
                else {
                    setWidget(_grid);
                    _grid.getStore().clear();
                    _grid.getStore().addAll(assignments);
                    _gbCallback.enable(true);
                    
                    forceLayout();
    //                if(assignments.size() > 0) {
    //                    List<Assignment> selectedList = new ArrayList<Assignment>();
    //                    selectedList.add(assignments.get(0));
    //                    _grid.getSelectionModel().setSelection(selectedList);
    //                    showGradeBookForSelectedAssignment();
    //                }
                }
            }

        }.register();        
    }

    private void showNoAssignedProblemsMessage() {
        CenterLayoutContainer cc = new CenterLayoutContainer();
        cc.add(new HTML("<h1>No assignments have been defined for this group.</h1>"));
        setWidget(cc);
        
        forceLayout();
    }

    private void exportAssignmentGradebooks() {
        
        if(_currentGroup == null) {
            CmMessageBox.showAlert("You need to select a group first.");
            return;
        }

        List<Assignment> list = _grid.getStore().getAll();
        if (list.isEmpty()) {
            CmMessageBox.showAlert("Selected group has no assignments.");
            return;
        }

        //TODO: add student count to GroupDto
        String name = _currentGroup.getName();
        String grpName = name.substring(0, name.indexOf("[")).trim();
        String stuCount = name.substring(name.indexOf("[")+1, name.indexOf(","));
        int count = (stuCount != null) ? Integer.parseInt(stuCount) : 0;
        if (count == 0) {
            CmMessageBox.showAlert("Selected group has no students.");
            return;
        }
        
        new ExportGradebooksDialog(_adminId, _currentGroup.getGroupId(),
        		grpName);
    }
    

    private void createNewAssignment() {
      
        
        Assignment newAss = new Assignment();
        newAss.setStatus("Draft");
        newAss.setAssignmentName("My New Assignment: " + new Date());
        newAss.setGroupId(_currentGroup.getGroupId());
        newAss.setAdminId(UserInfoBase.getInstance().getUid());
        
        Date defaultDate = new Date();
        CalendarUtil.addDaysToDate(defaultDate, 1);
        newAss.setDueDate(defaultDate);
        
        new EditAssignmentDialog(newAss);        
    }
    
    private void deleteSelectedAssignment() {
        if(_currentGroup == null) {
            CmMessageBox.showAlert("You need to select a group first.");
            return;
        }
        
        final Assignment ass = getSelectedAssigmment();
        if(ass != null) {
            final ConfirmMessageBox cm = new ConfirmMessageBox("Delete Assignment", "Deleting an assignment will prevent students from working on the assignment or reviewing it.");
            cm.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
            cm.addHideHandler(new HideHandler() {
                @Override
                public void onHide(HideEvent event) {
                    if (cm.getHideButton() != cm.getButtonById(PredefinedButton.CANCEL.name())) {
                        deleteAssignment(ass);
                    }
                }
            });
            cm.setWidth(300);
            cm.show();
        }
        else {
            CmMessageBox.showAlert("No assignment selected.");
        }
    }

    private void deleteAssignment(final Assignment ass) {
        if(_currentGroup == null) {
            return;
        }
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                DeleteAssignmentAction action = new DeleteAssignmentAction(ass.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                List<Assignment> assList = _grid.getStore().getAll();
                
                Assignment assToSelect=null;
                int cnt=0;
                for(Assignment a: assList) {
                    if(a.getAssignKey() == ass.getAssignKey()) {
                        if(assList.size() > cnt+1) {
                            assToSelect = assList.get(cnt);
                            List<Assignment> nextSelect = new ArrayList<Assignment>();
                            nextSelect.add(assToSelect);
                            _grid.getSelectionModel().setSelection(nextSelect);
                        }
                    }
                    cnt++;
                }
                
                CmBusyManager.setBusy(false);
                _grid.getStore().remove(ass);
                
                forceLayout();
            }
        }.register();        
    }
    
    private void editCurrentAssignment() {
        if(_currentGroup == null) {
            CmMessageBox.showAlert("You need to select a group first.");
            return;
        }       
        Assignment data = getSelectedAssigmment();
        if(data != null) {
            new EditAssignmentDialog(data);        
        }
        else {
            CmMessageBox.showAlert("No assignment selected.");
        }
        
    }
    
    private Assignment getSelectedAssigmment() {
        return _grid.getSelectionModel().getSelectedItem();
    }
    
    private void copySelectedAssignment() {
        if(_currentGroup == null) {
            CmMessageBox.showAlert("You need to select a group first.");
            return;
        }        
        final Assignment data = getSelectedAssigmment();
        if(data != null) {
            final ConfirmMessageBox cm = new ConfirmMessageBox("Copy Assignment", "Are you sure you want to copy this assignment?");
            cm.addHideHandler(new HideHandler() {
                @Override
                public void onHide(HideEvent event) {
                    if (cm.getHideButton() == cm.getButtonById(PredefinedButton.YES.name())) {
                        copyAssignemnt(data);
                    }
                }
            });
            cm.setVisible(true);
        }
        else {
            CmMessageBox.showAlert("No assignment selected.");
        }
    }
    
    
    private void copyAssignemnt(final Assignment ass) {
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CopyAssignmentAction action = new CopyAssignmentAction(ass.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                CmBusyManager.setBusy(false);

                String newAssignemntName = data.getDataAsString("new_name");
                Log.debug("Assignment copied successfully: " + newAssignemntName);
                readAssignmentData(_currentGroup);
            }
        }.register();        
    }


    private void closeSelectedAssignment() {
        if(_currentGroup == null) {
            CmMessageBox.showAlert("You need to select a group first.");
            return;
        }        
        
        final Assignment data = _grid.getSelectionModel().getSelectedItem();
        if(data == null) {
            CmMessageBox.showAlert("You need to select an assignment first.");
            return;
        }
        
        if(data.isClosed()) {
            CmMessageBox.showAlert("This assignment is already closed.");
            return;
        }
            
        final ConfirmMessageBox cm = new ConfirmMessageBox("Close Assignment", "Are you sure you want to close this assignment?");
        cm.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (cm.getHideButton() == cm.getButtonById(PredefinedButton.YES.name())) {
                    closeAssignment(data);
                }
            }
        });
        cm.setVisible(true);
    }
    
    private void closeAssignment(final Assignment ass) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CloseAssignmentAction action = new CloseAssignmentAction(UserInfoBase.getInstance().getUid(), ass.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                CmBusyManager.setBusy(false);
                Log.debug("Assignment closed successfully: " + data);
                ass.setStatus("Closed");
                _grid.getStore().update(ass);
            }
        }.register();        
    }
    
    private Widget createDelButton() {
        TextButton btn = new TextButton("Delete");
        btn.setToolTip("Delete selected assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                deleteSelectedAssignment();
            }

        });
        return btn;
    }
    
    private Widget createEditButton() {
        TextButton btn = new TextButton("View/Edit");
        btn.setToolTip("View/Edit selected assignment and see realtime statistics.");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                editCurrentAssignment();
            }

        });
        return btn;
    }
        
    private Widget createScoreButton() {
        TextButton btn = new TextButton("Students");
        btn.setToolTip("Show student assignment details");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                selectedAssignmentStatus();
            }

        });
        return btn;
    }
    
    private void selectedAssignmentStatus() {
        Assignment asgn = _grid.getSelectionModel().getSelectedItem();
        if(asgn == null) {
            CmMessageBox.showAlert("Select an Assignment first");
            return;
        }
        _callBack.showAssignmentStatus(asgn);        
    }

    private Widget createCCSSButton() {
        TextButton btn = new TextButton("CCSS");
        btn.setToolTip("Show CCSS details for selected Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                selectedAssignmentCCSS();
            }

        });
        return btn;
    }
    
    private void selectedAssignmentCCSS() {
        Assignment asgn = _grid.getSelectionModel().getSelectedItem();
        if(asgn == null) {
            CmMessageBox.showAlert("Select an Assignment first");
            return;
        }
        new CCSSCoverageForAssignmentWindow(asgn);
    }

    private Widget createNewButton() {
        TextButton btn = new TextButton("Create");
        btn.setToolTip("Create new assignment or course test");
        
        Menu menu = new Menu();
        btn.setMenu(menu);
        
        
        
        MenuItem btnC = new MenuItem("New Assignment", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                if(_currentGroup == null) {
                    CmMessageBox.showAlert("You need to select a group first.");
                    return;
                }                
                createNewAssignment();
            }
        });
        menu.add(btnC);
        
        MenuItem btnF = new MenuItem("Course Test", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                if(_currentGroup == null) {
                    CmMessageBox.showAlert("You need to select a group first.");
                    return;
                }
                new FinalExamCreationManager(_adminId, _currentGroup);
            }
        });
        btnF.setToolTip("Create a Course Test");
        menu.add(btnF);

        
        return btn;
    }
    
    private Widget createCopyButton() {
        TextButton btn = new TextButton("Copy");
        btn.setToolTip("Copy or import assignments");
        
        Menu menu = new Menu();
        menu.add(new MenuItem("Copy selected assignment",new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                copySelectedAssignment();    
            }
        }));
        menu.add(new MenuItem("Import assignments from other groups",new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                if(_currentGroup == null) {
                    CmMessageBox.showAlert("Select a group first");
                    return;
                }
                AssignmentCopyDialog.getInstance().showAndSetCurrentGroup(_currentGroup);    
            }
        }));
        btn.setMenu(menu);
        
        return btn;
    }

}

