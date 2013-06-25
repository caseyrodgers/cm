package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.Date;
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
    private ListView<StudentAssignment, String> _listLeft;
    private ListView<StudentAssignment, String> _listRight;
    private CmList<StudentAssignment> _allStudents;
    
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
            }
        }));

        addCloseButton();

        setVisible(true);
    }

    private void loadData() {
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
                _allStudents = saList;
                CmBusyManager.setBusy(false);

                _listLeft.getStore().clear();
                _listLeft.getStore().addAll(saList);
            }
        }.register();
    }

    interface StudentAssignmentProperties extends PropertyAccess<String> {
        ModelKeyProvider<StudentAssignment> uid();

        ValueProvider<StudentAssignment, String> studentName();
    }

    public void drawGui() {
        HorizontalLayoutContainer con = new HorizontalLayoutContainer();

        StudentAssignmentProperties props = GWT.create(StudentAssignmentProperties.class);

        ListStore<StudentAssignment> store = new ListStore<StudentAssignment>(props.uid());

        store.addSortInfo(new StoreSortInfo<StudentAssignment>(props.studentName(), SortDir.ASC));
        store.add(new StudentAssignment(10, new Assignment(), new Date(), true));

        _listLeft = new ListView<StudentAssignment, String>(store, props.studentName());

        store = new ListStore<StudentAssignment>(props.uid());
        store.addSortInfo(new StoreSortInfo<StudentAssignment>(props.studentName(), SortDir.ASC));

        _listRight = new ListView<StudentAssignment, String>(store, props.studentName());
        _listRight.getSelectionModel().setSelectionMode(SelectionMode.MULTI);

        ListViewDragSource<StudentAssignment> ds = new ListViewDragSource<StudentAssignment>(_listLeft);
        ds.addDragStartHandler(new DndDragStartHandler() {
            @Override
            public void onDragStart(DndDragStartEvent event) {
                if (alreadyExists((List<StudentAssignment>)event.getData())) {
                    event.isCancelled();
                }
            }
        });
        new ListViewDragSource<StudentAssignment>(_listRight);

        new ListViewDropTarget<StudentAssignment>(_listLeft);
        new ListViewDropTarget<StudentAssignment>(_listRight);

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

    protected boolean alreadyExists(List<StudentAssignment> data) {
        if (data != null) {
            for (StudentAssignment a : data) {
                for (StudentAssignment a2 : _listRight.getStore().getAll()) {
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
