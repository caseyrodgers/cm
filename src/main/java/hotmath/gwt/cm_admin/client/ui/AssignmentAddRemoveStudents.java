package hotmath.gwt.cm_admin.client.ui;


import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStudentsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStudentsAction.TYPE;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

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
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AssignmentAddRemoveStudents extends GWindow {

    private Assignment assignment;
    private ContentPanel _leftPanel;
    private ContentPanel _rightPanel;
    private ListView<StudentDto, String> _listLeft;
    private ListView<StudentDto, String> _listRight;
    private CmList<StudentDto> _allStudents;
    
    public AssignmentAddRemoveStudents(Assignment assignment) {
        super(false);

        setHeadingHtml("Add and Remove Students from Assignment");
        this.assignment = assignment;
        drawGui();
        loadData();
        setPixelSize(640, 480);

        addButton(new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                saveToServer();
            }
        }));

        addCloseButton();

        setVisible(true);
    }

    protected void saveToServer() {
//        new RetryAction<RpcData>() {
//            @Override
//            public void attempt() {
//                CmBusyManager.setBusy(true);
//
//                GetAssignmentGradeBookAction action = new GetAssignmentGradeBookAction(assignment.getAssignKey());
//                setAction(action);
//                CmShared.getCmService().execute(action, this);
//            }
//
//            @Override
//            public void oncapture(CmList<StudentAssignment> saList) {
//                _allStudents = saList;
//                CmBusyManager.setBusy(false);
//
//                _listLeft.getStore().clear();
//                _listLeft.getStore().addAll(saList);
//            }
//        }.register();
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
                CmShared.getCmService().execute(mAction, this);
            }

            @Override
            public void oncapture(CmList<Response> respones) {
                
                CmBusyManager.setBusy(false);
                
               _allStudents = (CmList<StudentDto>)respones.get(0);
               _assignedStudents = (CmList<StudentDto>)respones.get(1);
                
                _listLeft.getStore().clear();
                _listLeft.getStore().addAll(_assignedStudents);
                
                _listRight.getStore().clear();
                _listRight.getStore().addAll(_allStudents);
                
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
