package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.GWT;

/* Provide standard display of student lists
 * 
 */
public class TrendingDataStudentListDialog extends CmWindow {

    Grid<StudentModelExt> _grid;
    
    public TrendingDataStudentListDialog(String title, List<StudentModelExt> students) {

        setSize(300, 400);
        setHeading(title);
        addStyleName("trending-data-student-list");
        setLayout(new FillLayout());
    
        ListStore<StudentModelExt> store = new ListStore<StudentModelExt>();
        store.add(students);
        
        _grid = defineGrid(store, defineColumns());
        add(_grid);
        
        
        addButton(new MyButton("Edit", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                StudentModelExt sm = _grid.getSelectionModel().getSelectedItem();
                showStudentInfo(StudentEventType.REGISTER, sm);
            }
        },"Edit selected student's registration."));
        
        addButton(new MyButton("Details", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                StudentModelExt sm = _grid.getSelectionModel().getSelectedItem();
                showStudentInfo(StudentEventType.DETAILS, sm);
            }
        },"Show selected student's history detail."));

        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));
        
        
        getHeader().addTool(new Button("Print List",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        List<Integer> uids = new ArrayList<Integer>();
                        List<StudentModelExt> students = _grid.getStore().getModels();
                        int adminId = StudentGridPanel.instance._cmAdminMdl.getId();
                        for(int i=0,t=students.size();i<t;i++) {
                            uids.add(students.get(i).getUid());
                        }
                        GeneratePdfAction action = new GeneratePdfAction(PdfType.STUDENT_LIST,adminId,uids);
                        action.setTitle(getHeading());
                        action.setFilterMap(StudentGridPanel.instance._pageAction.getFilterMap());
                        new PdfWindow(0, "Catchup Math Student List", action);
                    }
                });                
            }
        }));

        setModal(true);
        setVisible(true);
    }
    
    enum StudentEventType {REGISTER,DETAILS}
    
    /** call additional student information that needs a full StudentModelExt
     * 
     * @param type
     * @param sm
     */
    private void showStudentInfo(final StudentEventType type, final StudentModelExt sm) {
        if(sm == null)
            return;
        
        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CmShared.getCmService().execute(new GetStudentModelAction(sm.getUid()),this);
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
        }.attempt();
    }

    
    private Grid<StudentModelExt> defineGrid(final ListStore<StudentModelExt> store, ColumnModel cm) {
        final Grid<StudentModelExt> grid = new Grid<StudentModelExt>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.setWidth("410px");
        grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);
        return grid;
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId(StudentModelExt.NAME_KEY);
        column.setHeader("Student Name");
        column.setWidth(235);
        column.setSortable(true);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
 
    class MyButton extends Button {
        public MyButton(String name, SelectionListener<ButtonEvent> listener, String tip) {
            super(name, listener);
            setToolTip(tip);
        }
    }    
}
