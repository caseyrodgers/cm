package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetGradeBookDataAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.AssignmentModel;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

public class GradeBookDialog extends CmWindow {
    
    int uid;
    Grid _grid;
    public GradeBookDialog(int uid) {
        this.uid = uid;
        setHeading("Grade Book View");
        setSize("640px", "480px");
        setLayout(new FillLayout());
        
        addCloseButton();
        setVisible(true);
        
        readServerData();
    }
    
    @SuppressWarnings("unchecked")
    private void loadDataIntoGrid(CmList<GradeBookModel> data) {
        ListStore<GradeBookModel> store = new ListStore<GradeBookModel>();
        _grid = defineGrid(store, defineColumns(data));
        add(_grid);
        xferAssignmentList(data);
        _grid.getStore().add(data);
        
        layout(true);
    }
    
    private void xferAssignmentList(CmList<GradeBookModel> gbList) {
    	for (GradeBookModel gbMdl : gbList) {
    		for (AssignmentModel asgMdl : gbMdl.getAssignmentList()) {
    			gbMdl.set(asgMdl.getName(), asgMdl.getPercentCorrect());
    		}
    	}
		
	}

	private void readServerData() {

        new RetryAction<CmList<GradeBookModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetGradeBookDataAction action = new GetGradeBookDataAction(uid);
                action.setStudentGridAction(StudentGridPanel.instance._pageAction);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<GradeBookModel> result) {
                CmBusyManager.setBusy(false);
                loadDataIntoGrid(result);
                _grid.setLoadMask(false);
            }
        }.register();
        
    }
    
    private Grid<GradeBookModel> defineGrid(final ListStore<GradeBookModel> store, ColumnModel cm) {
        final Grid<GradeBookModel> grid = new Grid<GradeBookModel>(store, cm);
        //grid.setAutoExpandColumn("userName");
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModelExt>>() {
            public void handleEvent(final SelectionEvent<StudentModelExt> se) {
                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    CatchupMathTools.showAlert("RDC: selected: " + grid.getSelectionModel().getSelectedItems().size());
                }
            }
        });

        grid.setWidth("500px");
        grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);
        return grid;
    }
    
    private ColumnModel defineColumns(CmList<GradeBookModel> data) {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("userName");
        column.setHeader("Student");
        column.setWidth(140);
        column.setSortable(true);
        configs.add(column);
        
        if(data.size() > 0) {
            GradeBookModel m =  data.get(0);

            for(AssignmentModel mdl : m.getAssignmentList()) {
                ColumnConfig lessonColumn = new ColumnConfig();
                lessonColumn.setId(mdl.getName());
                lessonColumn.setHeader(mdl.getName());
                lessonColumn.setToolTip(mdl.getCpName() + " (" + mdl.getLessonName() + ")");
                lessonColumn.setWidth(70);
                lessonColumn.setSortable(true);

                configs.add(lessonColumn);
            }
        }
        
        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }    
    
}
