package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetGradeBookDataAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.AssignmentModel;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

public class GradeBookDialog extends CmWindow {
    
    int uid;
    Grid<GradeBookModel> _grid;
	ComboBox<AssignmentModel> homeworkCombo;
    GradeBookModel gradeBookMdl;
    
    public GradeBookDialog(int uid) {
        this.uid = uid;
        setHeading("Grade Book View");
        setSize("640px", "480px");
        setLayout(new FillLayout());

        getButtonBar().setStyleAttribute("position", "relative");
        addHomeworkSelector();
        addDetailsButton();
        addCloseButton();

        readServerData();

        setVisible(true);
    }

    private void addDetailsButton() {
        StdButton detailsBtn = new StdButton("Homework Details", "Display homework details.", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                
                final GradeBookModel mdl = getGridItem();
                if (mdl != null) {
                    displayHomeworkDetails(mdl);
                }
                
            }

        });
        detailsBtn.setStyleAttribute("padding-right", "50px");
        detailsBtn.setStyleAttribute("padding-top", "4px");

        getButtonBar().add(detailsBtn);    	
    }

    private void addHomeworkSelector() {
        getButtonBar().add(defineHomeworkSelector());
    }

	private void displayHomeworkDetails(GradeBookModel mdl) {
		new HomeworkDetailsWindow(mdl).setVisible(true);		
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
                initHomeworkCombo(result);
                loadDataIntoGrid(result);
                _grid.setLoadMask(false);
            }
        }.register();
        
    }
    
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

    private Grid<GradeBookModel> defineGrid(final ListStore<GradeBookModel> store, ColumnModel cm) {
        final Grid<GradeBookModel> grid = new Grid<GradeBookModel>(store, cm);
        //grid.setAutoExpandColumn("userName");
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(final BaseEvent be) {
                CatchupMathTools.showAlert("RDC: selected: " + grid.getSelectionModel().getSelectedItems().size());
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
            gradeBookMdl =  data.get(0);

            for(AssignmentModel mdl : gradeBookMdl.getAssignmentList()) {
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

    private Component defineHomeworkSelector() {

        LayoutContainer lc = new HorizontalPanel();

        class MyFormPanel extends FormPanel {
            MyFormPanel() {
                setHeaderVisible(false);
                setLabelWidth(60);
                setBorders(false);
                setFrame(false);
                setFooter(false);
                setBodyBorder(false);
                setBorders(false);
                setWidth(300);
            }
        };

        FormPanel fp = new MyFormPanel();
        defineHomeworkCombo();
        fp.add(homeworkCombo);
        BorderLayoutData borderLayout = new BorderLayoutData(LayoutRegion.WEST, 300);
        lc.add(fp, borderLayout);
        
        return lc;
    }

	private void defineHomeworkCombo() {
		homeworkCombo = new ComboBox<AssignmentModel>();
		
        ListStore<AssignmentModel> store = new ListStore<AssignmentModel>();

        AssignmentModel mdl = new AssignmentModel();
        mdl.set("cpId", 0);
        mdl.set("cpName", "--- All ---");
        store.insert(mdl, 0);

		homeworkCombo.setFieldLabel("Homework");
		homeworkCombo.setForceSelection(false);
		homeworkCombo.setDisplayField("cpName");
		homeworkCombo.setEditable(false);
		homeworkCombo.setMaxLength(50);
		homeworkCombo.setAllowBlank(true);
		homeworkCombo.setTriggerAction(TriggerAction.ALL);
		homeworkCombo.setStore(store);
		homeworkCombo.setTitle("Make a selection");
		homeworkCombo.setId("hw_combo");
		homeworkCombo.setTypeAhead(true);
		homeworkCombo.setSelectOnFocus(true);
		homeworkCombo.setEmptyText("-- make a selection --");
		homeworkCombo.setWidth(300);

		homeworkCombo.addSelectionChangedListener(new SelectionChangedListener<AssignmentModel>() {
			public void selectionChanged(SelectionChangedEvent<AssignmentModel> se) {
			    AssignmentModel asgMdl = se.getSelectedItem();
                String homeworkName = asgMdl.getCpName();
                ColumnModel colMdl = _grid.getColumnModel();
                for(AssignmentModel mdl : gradeBookMdl.getAssignmentList()) {
                	if (homeworkName.equals("--- All ---") || mdl.getCpName().equals(homeworkName)) {
                		colMdl.setHidden(colMdl.getIndexById(mdl.getName()), false);
                	}
                	else {
                		colMdl.setHidden(colMdl.getIndexById(mdl.getName()), true);                		
                	}
                }
	        }
	    });
	}

	private void initHomeworkCombo(List<GradeBookModel> gbList) {
		
        ListStore<AssignmentModel> store = homeworkCombo.getStore();

        if (gbList != null) {
        	int cpId = -1;
            for (AssignmentModel asgMdl : gbList.get(0).getAssignmentList()) {
            	if (cpId != asgMdl.getCpId()) {
            		store.add(asgMdl);
            		cpId = asgMdl.getCpId();
            	}
            }
        }
	}

    private GradeBookModel getGridItem() {
        GradeBookModel mdl = _grid.getSelectionModel().getSelectedItem();
        if (mdl == null) {
            CatchupMathTools.showAlert("Please select a student");
        }
        return mdl;
    }


}
