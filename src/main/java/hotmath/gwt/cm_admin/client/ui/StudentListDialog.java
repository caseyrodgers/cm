package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelsAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

/* Provide standard display of student lists
 * 
 */
public class StudentListDialog extends GWindow {

    Grid<StudentModelI> _grid;
    VerticalLayoutContainer _container = new VerticalLayoutContainer();
    //FlowLayoutContainer _container = new FlowLayoutContainer();
    ListStore<StudentModelI> _store = new ListStore<StudentModelI>(_dataAccess.nameKey());

    int _height = 400;
    int _width = 270;

    public StudentListDialog(String title, int height) {
    	super(false);
    	_height = height;
    }
    
    public StudentListDialog(String title) {
    	super(false);
    	buildUI(title);
    }

    public void loadStudents(final List<Integer> studentUids) {
        new RetryAction<CmList<StudentModelI>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);

                GetStudentModelsAction action = new GetStudentModelsAction(studentUids);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<StudentModelI> models) {
                CmBusyManager.setBusy(false);
                addStudents(models);
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
            }
        }.register();

    }

    protected void addStudents(List<StudentModelI> students) {
    	if (isVisible()) setVisible(false);
    	_store.clear();
        _store.addAll(students);
        setVisible(true);
        forceLayout();
    }

    protected void buildUI(String title) {
        setWidth(_width);
        setHeight(400);

        setHeadingText(title);

        _grid = defineGrid(_store, defineColumns());
        _container.setScrollMode(ScrollMode.AUTO);
        _container.add(_grid);  // new VerticalLayoutData(1, 1);
        add(_container);
        
        TextButton edit = new StudentPanelButton("Edit");
        edit.setToolTip("Edit selected student's registration.");
        edit.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                GridSelectionModel<StudentModelI> sel = _grid.getSelectionModel();
                List<StudentModelI> l = sel.getSelection();
                if (l.size() == 0) {
                    CmMessageBox.showAlert("Please select a student.");
                } else {
                    StudentModelI sm = l.get(0);
                    showStudentInfo(StudentEventType.REGISTER, sm);
                }
            }
        });
        getButtonBar().add(edit);
        
        TextButton details = new StudentPanelButton("Details");
        details.setToolTip("Show selected student's history detail.");
        details.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                GridSelectionModel<StudentModelI> sel = _grid.getSelectionModel();
                List<StudentModelI> l = sel.getSelection();
                if (l.size() == 0) {
                    CmMessageBox.showAlert("Please select a student.");
                } else {
                    StudentModelI sm = l.get(0);
                    showStudentInfo(StudentEventType.DETAILS, sm);
                }
            }
        });
        getButtonBar().add(details);

        super.addCloseButton();
        
        TextButton print = new StudentPanelButton("Print-3");
        print.setToolTip("Display a printable report.");
        print.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                List<Integer> uids = new ArrayList<Integer>();
                List<StudentModelI> students = _grid.getStore().getAll();
                int adminId = StudentGridPanel.instance._cmAdminMdl.getUid();
                for(int i=0,t=students.size();i<t;i++) {
                    uids.add(students.get(i).getUid());
                }
                GeneratePdfAction action = new GeneratePdfAction(PdfType.STUDENT_LIST,adminId,uids);
                action.setTitle(header.getText());
                action.setFilterMap(StudentGridPanel.instance._pageAction.getFilterMap());
                new PdfWindow(0, "Catchup Math Student List", action);
            }
        });
        getHeader().addTool(print);

        setModal(true);
    }
    
    enum StudentEventType {REGISTER,DETAILS}
    
    /** call additional student information that needs a full StudentModelExt
     * 
     * @param type
     * @param sm
     */
    private void showStudentInfo(final StudentEventType type, final StudentModelI sm) {
        if(sm == null)
            return;
        
        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetStudentModelAction action = new GetStudentModelAction(sm.getUid());
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }            
            
            @Override
            public void oncapture(StudentModelI result) {
                CmBusyManager.setBusy(false);
                StudentModelExt sm = new StudentModelExt(result);
                if(type==StudentEventType.REGISTER) {
                    new RegisterStudent(sm, StudentGridPanel.instance._cmAdminMdl).showWindow();
                }
                else if(type == StudentEventType.DETAILS) {
                    new StudentDetailsWindow(sm);
                }
            }
        }.register();
    }

    
    private Grid<StudentModelI> defineGrid(final ListStore<StudentModelI> store, ColumnModel<StudentModelI> cm) {
        final Grid<StudentModelI> grid = new Grid<StudentModelI>(store, cm);
        grid.setBorders(true);
        //grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //grid.getSelectionModel().setFiresEvents(true);
        grid.setWidth(255);
        grid.setHeight(300);
        grid.setStateful(true);
        grid.setLoadMask(true);
        return grid;
    }

    private ColumnModel<StudentModelI> defineColumns() {
        List<ColumnConfig<StudentModelI, ?>> cols = new ArrayList<ColumnConfig<StudentModelI, ?>>();

        cols.add(new ColumnConfig<StudentModelI, String>(_dataAccess.name(), 235, "Student Name"));
        // column.setSortable(true);

        return new ColumnModel<StudentModelI>(cols);
    }

	public interface DataPropertyAccess extends PropertyAccess<StudentModelI> {
		ValueProvider<StudentModelI, String> name();

		@Path("name")
		ModelKeyProvider<StudentModelI> nameKey();
	}

	private static final DataPropertyAccess _dataAccess = GWT.create(DataPropertyAccess.class);

}
