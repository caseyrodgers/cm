package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.DeleteAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsCreatedAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Widget;

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


/** Provide GUI to list assignments and any related meta data
 * 
 * @author casey
 *
 */
public class AssignmentsContentPanel extends ContentPanel {
    
    Grid<Assignment> _grid;
    
    GradeBookPanel _gradeBookPanel;
    GroupDto _currentGroup;
    
    public AssignmentsContentPanel(GradeBookPanel gradeBookPanel) {
        
        this._gradeBookPanel = gradeBookPanel;

        super.setHeadingText("Assignments");

        getHeader().addTool(createNewButton());
        getHeader().addTool(createEditButton());
        getHeader().addTool(createDelButton());
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
        
        _grid.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				showGradeBookForSelectedAssignment();
			}
        }, ClickEvent.getType());
        
        add(_grid);
    }
    

    public void loadAssignentsFor(GroupDto group) {
        _currentGroup = group;
        readAssignmentData(group);
    }

    private void showGradeBookForSelectedAssignment() {
        if(_gradeBookPanel == null) {
            return;
        }
        
        Assignment asgn = _grid.getSelectionModel().getSelectedItem();
        if(asgn == null) {
            return;
        }
        _gradeBookPanel.showGradeBookFor(asgn);
    }
    
    private void readAssignmentData(final GroupDto group) {
        new RetryAction<CmList<Assignment>>() {
            @Override
            public void attempt() {
                GetAssignmentsCreatedAction action = new GetAssignmentsCreatedAction(UserInfoBase.getInstance().getUid(), group.getGroupId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<Assignment> assignments) {
                _grid.getStore().clear();
                _grid.getStore().addAll(assignments);
                
                if(assignments.size() > 0) {
                    List<Assignment> selectedList = new ArrayList<Assignment>();
                    selectedList.add(assignments.get(0));
                    _grid.getSelectionModel().setSelection(selectedList);
                    showGradeBookForSelectedAssignment();
                }
            }
        }.register();        
    }

    private void createNewAssignment() {
        
        Assignment newAss = new Assignment();
        newAss.setAssignmentName("My New Assignment: " + new Date());
        newAss.setGroupId(_currentGroup.getGroupId());
        
        Date defaultDate = new Date();
        defaultDate.setHours(defaultDate.getHours() + 24);        
        newAss.setDueDate(defaultDate);
        
        new EditAssignmentDialog(newAss, new CallbackOnComplete() {
            @Override
            public void isComplete() {
                loadAssignentsFor(_currentGroup);
            }
        });        
    }
    
    private void deleteSelectedAssignment() {
        
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
            }
        }.register();        
    }
    
    private void editCurrentAssignment() {
        Assignment data = _grid.getSelectionModel().getSelectedItem();
        if(data != null) {
            new EditAssignmentDialog(data, new CallbackOnComplete() {
                @Override
                public void isComplete() {
                    readAssignmentData(_currentGroup);
                }
            });        
        }
        
    }
    
    private Widget createDelButton() {
        TextButton btn = new TextButton("Del");
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
        
    private Widget createNewButton() {
        TextButton btn = new TextButton("New");
        btn.setToolTip("Create new assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                createNewAssignment();
            }

        });
        return btn;
    }

}

