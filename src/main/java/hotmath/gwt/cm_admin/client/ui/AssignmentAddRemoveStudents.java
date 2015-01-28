package hotmath.gwt.cm_admin.client.ui;


import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.rpc.AssignStudentsToAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStudentsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStudentsAction.TYPE;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;

public class AssignmentAddRemoveStudents extends GWindow {

    private Assignment assignment;
    private ContentPanel _leftPanel;
    private ContentPanel _rightPanel;
    private ListView<StudentDto, String> _listLeft;
    private ListView<StudentDto, String> _listRight;
    private CmList<StudentDto> _allStudents;
    private CallbackOnComplete callBack;
    
    public AssignmentAddRemoveStudents(Assignment assignment, CallbackOnComplete callBack) {
        super(false);

        this.callBack = callBack;
        setHeadingHtml("Add and Remove Students from Assignment");
        this.assignment = assignment;
        drawGui();
        loadData();
        setPixelSize(640, 480);

        addButton(new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(_listLeft.getStore().size() == 0) {
                    CmMessageBox.showAlert("Please specify one or more students to be assigned.");
                    return;
                }
                saveToServer();
            }
        }));

        addCloseButton();

        setVisible(true);
    }

    protected void saveToServer() {
        
        CmBusyManager.setBusy(true);
        new RetryAction<AssignmentInfo>() {
            @Override
            public void attempt() {
                CmList<StudentDto> students= new CmArrayList<StudentDto>();
                if(_allStudents.size() == _listLeft.getStore().size()) {
                    /** if all students are assigned, then put assignment
                     *  in default/live population mode, meaning 'all students'
                     *  currently in group at time of assignment use. 
                     */
                    students.clear(); // leave empty 
                }
                else {
                    students.addAll(_listLeft.getStore().getAll());
                }
                AssignStudentsToAssignmentAction action = new AssignStudentsToAssignmentAction(assignment.getAssignKey(), students);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(AssignmentInfo info) {
                CmBusyManager.setBusy(false);
                if(info.getErrors()==0) {
                    Info.display("Students Assigned", info.getAssigned() + " student(s) saved.");
                }
                else {
                    MessageBox errorInfo = new MessageBox("Errors Assigning", "There were " + info.getErrors() + " error(s): \n\n" + info.getMessage());
                    errorInfo.setPixelSize(400, 300);
                    errorInfo.show();
                }
                hide();
                CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.ASSIGNMENTS));
                
                callBack.isComplete();
            }

        }.register();                 
    }

    private void loadData() {
        new RetryAction<CmList<Response>>() {

            private CmList<StudentDto> _assignedStudents;

            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                MultiActionRequestAction mAction = new MultiActionRequestAction();
                mAction.getActions().add(new GetAssignmentStudentsAction(assignment.getAssignKey(),TYPE.ALL_IN_GROUP));
                mAction.getActions().add(new GetAssignmentStudentsAction(assignment.getAssignKey(),TYPE.ASSIGNED));
                setAction(mAction);
                CmRpcCore.getCmService().execute(mAction, this);
            }

            @Override
            public void oncapture(CmList<Response> respones) {
                
                CmBusyManager.setBusy(false);
                
               _allStudents = (CmList<StudentDto>)respones.get(0);
               _assignedStudents = (CmList<StudentDto>)respones.get(1);

               List<StudentDto> unassignedStudents = new ArrayList<StudentDto>();
               
               if(_assignedStudents.size() == 0) {
                   _assignedStudents.addAll(_allStudents);
                   unassignedStudents.clear();
               }
               else {
                   // there are specified students
                   
                   for(StudentDto s: _allStudents) {
                       boolean found=false;
                       for(StudentDto sa: _assignedStudents) {
                           if(sa.getUid() == s.getUid()) {
                               found=true;
                               break;
                           }
                       }
                       if(!found) {
                           unassignedStudents.add(s);
                       }
                   }
               }
               
               
                _listLeft.getStore().clear();
                _listLeft.getStore().addAll(_assignedStudents);
                
                _listRight.getStore().clear();
                
                _listRight.getStore().addAll(unassignedStudents);
            }
        }.register();
    }

    interface StudentAssignmentProperties extends PropertyAccess<String> {
        ModelKeyProvider<StudentDto> uid();
        ValueProvider<StudentDto, String> name();
    }

    public void drawGui() {
        HorizontalLayoutContainer con = new HorizontalLayoutContainer();

        StudentAssignmentProperties props = GWT.create(StudentAssignmentProperties.class);

        ListStore<StudentDto> store = new ListStore<StudentDto>(props.uid());

        store.addSortInfo(new StoreSortInfo<StudentDto>(props.name(), SortDir.ASC));

        _listLeft = new ListView<StudentDto, String>(store, props.name());

        store = new ListStore<StudentDto>(props.uid());
        store.addSortInfo(new StoreSortInfo<StudentDto>(props.name(), SortDir.ASC));

        _listRight = new ListView<StudentDto, String>(store, props.name());
        _listRight.getSelectionModel().setSelectionMode(SelectionMode.MULTI);

        ListViewDragSource<StudentDto> ds = new ListViewDragSource<StudentDto>(_listLeft);
        ds.addDragStartHandler(new DndDragStartHandler() {
            @Override
            public void onDragStart(DndDragStartEvent event) {
                if (alreadyExists((List<StudentDto>)event.getData())) {
                    event.isCancelled();
                }
            }
        });
        new ListViewDragSource<StudentDto>(_listRight);

        new ListViewDropTarget<StudentDto>(_listLeft);
        new ListViewDropTarget<StudentDto>(_listRight);

        _leftPanel = new ContentPanel();
        _leftPanel.addTool(new TextButton("Unassign All", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                unassignAllStudents();
            }
        }));
        _rightPanel = new ContentPanel();
        _rightPanel.addTool(new TextButton("Assign All", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                assignAllStudents();
            }
        }));

        _leftPanel.add(_listLeft);
        _leftPanel.setHeadingHtml("Students Assigned");

        _rightPanel.add(_listRight);
        _rightPanel.setHeadingHtml("Students Not Assigned");

        con.add(_leftPanel, new HorizontalLayoutData(.5, 1, new Margins(5)));
        con.add(_rightPanel, new HorizontalLayoutData(.5, 1, new Margins(5, 5, 5, 0)));

        setWidget(con);
    }

    protected boolean alreadyExists(List<StudentDto> data) {
        if (data != null) {
            for (StudentDto a : data) {
                for (StudentDto a2 : _listRight.getStore().getAll()) {
                    if (a2.getUid() == a.getUid()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void unassignAllStudents() {
        _listRight.getStore().clear();
        _listLeft.getStore().clear();
        _listRight.getStore().addAll(_allStudents);
    }

    protected void assignAllStudents() {
        _listRight.getStore().clear();
        _listLeft.getStore().clear();
        _listLeft.getStore().addAll(_allStudents);
    }

}
