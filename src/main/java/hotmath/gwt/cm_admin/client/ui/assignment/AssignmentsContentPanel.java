package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.CloseAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CopyAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.DeleteAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsCreatedAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.assignment.ExportGradebooksDialog;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
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
    
    Callback _callBack;
    
    /** Contains grid of assignments
     * 
     * @param gradeBookPanel
     */
    public AssignmentsContentPanel(Callback callback) {
        super.setHeadingText("Assignments");
        
        _callBack = callback;

        _adminId = UserInfoBase.getInstance().getUid();
        
        getHeader().addTool(createNewButton());
        getHeader().addTool(createEditButton());
        // getHeader().addTool(createCloseButton());
        getHeader().addTool(createDelButton());
        getHeader().addTool(createCopyButton());
        getHeader().addTool(new HTML("&nbsp;&nbsp;"));
        getHeader().addTool(createScoreButton());
        setCollapsible(false);

        AssignmentProperties props = GWT.create(AssignmentProperties.class);

        ColumnConfig<Assignment, Date> nameCol = new ColumnConfig<Assignment, Date>(props.dueDate(), 75, "Due Date");
        ColumnConfig<Assignment, String> statusCol = new ColumnConfig<Assignment, String>(props.status(), 75, "Status");
        ColumnConfig<Assignment, Integer> lessonCountCol = new ColumnConfig<Assignment, Integer>(props.problemCount(), 75, "Problems");
        ColumnConfig<Assignment, String> commentsCol = new ColumnConfig<Assignment, String>(props.comments(),50, "Comments");
        List<ColumnConfig<Assignment, ?>> l = new ArrayList<ColumnConfig<Assignment, ?>>();
        l.add(nameCol);
        l.add(statusCol);
        l.add(lessonCountCol);
        l.add(commentsCol);
        ColumnModel<Assignment> cm = new ColumnModel<Assignment>(l);        

        // Create the store that the contains the data to display in the grid
        ListStore<Assignment> store = new ListStore<Assignment>(props.key());
                
        _grid = new Grid<Assignment>(store, cm);
        
        _grid.getView().setAutoExpandColumn(commentsCol);
        _grid.getView().setStripeRows(true);
        _grid.getView().setColumnLines(true);

        _grid.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                editCurrentAssignment();
            }
        },DoubleClickEvent.getType());
        
        CenterLayoutContainer defaultMessageContainer = new CenterLayoutContainer();
        defaultMessageContainer.setWidget(new HTML("Select a group to see its assignments"));
        setWidget(defaultMessageContainer);
    }

    public void refreshData() {
        loadAssignentsFor(_currentGroup);
    }

    public void loadAssignentsFor(GroupDto group) {
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
                }
                else {
                    setWidget(_grid);
                    _grid.getStore().clear();
                    _grid.getStore().addAll(assignments);
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
        
        if(_currentGroup == null) {
            CmMessageBox.showAlert("You need to select a group first.");
            return;
        }
        
        Assignment newAss = new Assignment();
        newAss.setStatus("Draft");
        newAss.setAssignmentName("My New Assignment: " + new Date());
        newAss.setGroupId(_currentGroup.getGroupId());
        
        Date defaultDate = new Date();
        newAss.setDueDate(defaultDate);
        
        new EditAssignmentDialog(newAss);        
    }
    
    private void deleteSelectedAssignment() {
        if(_currentGroup == null) {
            CmMessageBox.showAlert("You need to select a group first.");
            return;
        }
        
        final Assignment ass = _grid.getSelectionModel().getSelectedItem();
        if(ass == null) {
            return;
        }

        
        final ConfirmMessageBox cm = new ConfirmMessageBox("Delete Assignment", "Are you sure you want to delete this assignment?");
        cm.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (cm.getHideButton() == cm.getButtonById(PredefinedButton.YES.name())) {
                    deleteAssignment(ass);
                }
            }
        });
        cm.setWidth(300);
        cm.show();
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
        Assignment data = _grid.getSelectionModel().getSelectedItem();
        if(data != null) {
            new EditAssignmentDialog(data);        
        }
        
    }
    
    private void copySelectedAssignment() {
        if(_currentGroup == null) {
            CmMessageBox.showAlert("You need to select a group first.");
            return;
        }        
        final Assignment data = _grid.getSelectionModel().getSelectedItem();
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
    }
    
    
    private void copyAssignemnt(final Assignment ass) {
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CopyAssignmentAction action = new CopyAssignmentAction(_adminId, ass.getAssignKey());
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
        if(data != null) {
            
            if(!data.getStatus().equals("Close")) {
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
        }
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
        TextButton btn = new TextButton("Edit");
        btn.setToolTip("Edit selected assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                editCurrentAssignment();
            }

        });
        return btn;
    }
        
    private Widget createScoreButton() {
        TextButton btn = new TextButton("Status");
        btn.setToolTip("Show assignment status");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                selectedAssignmentStatus();
            }

        });
        return btn;
    }
    
    protected void selectedAssignmentStatus() {
        Assignment asgn = _grid.getSelectionModel().getSelectedItem();
        if(asgn == null) {
            CmMessageBox.showAlert("Select an Assignment first");
            return;
        }
        _callBack.showAssignmentStatus(asgn);        
    }


    private Widget createNewButton() {
        TextButton btn = new TextButton("Create");
        btn.setToolTip("Create new assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                createNewAssignment();
            }

        });
        return btn;
    }
    
    private Widget createCopyButton() {
        TextButton btn = new TextButton("Copy");
        
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

