package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

/* Provide standard display of student lists
 * 
 */
public class TrendingDataStudentListDialog extends CmWindow {

    Grid<StudentModelExt> _grid;
    
    public TrendingDataStudentListDialog(String title, List<StudentModelExt> students) {

        setSize(275, 400);
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

        setModal(true);
        setVisible(true);
    }
    
    enum StudentEventType {REGISTER,DETAILS}
    
    /** call additional student information that needs a full StudentModelExt
     * 
     * @param type
     * @param sm
     */
    private void showStudentInfo(final StudentEventType type, StudentModelExt sm) {
        if(sm == null)
            return;
        
        
        CmBusyManager.setBusy(true);

        CmServiceAsync service = (CmServiceAsync) Registry.get("cmService");
        service.execute(new GetStudentModelAction(sm.getUid()),new CmAsyncCallback<StudentModelI>() {
            @Override
            public void onSuccess(StudentModelI result) {
                StudentModelExt sm = new StudentModelExt(result);
                if(type==StudentEventType.REGISTER) {
                    new RegisterStudent(sm, StudentGridPanel.instance._cmAdminMdl).showWindow();
                }
                else if(type == StudentEventType.DETAILS) {
                    new StudentDetailsWindow(sm);
                }
                CmBusyManager.setBusy(false);
            }
            
            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                super.onFailure(caught);
            }
        });
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
