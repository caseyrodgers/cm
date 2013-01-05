package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.LessonItemModelProperties;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForCustomProgramTestAction;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForTestRunAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * Display Student's Lesson Topic (Prescribed Standards) Status
 * 
 * @author Bob
 * 
 */
public class StudentLessonTopicsStatusWindow extends GWindow {

    private Integer runId;
    private Integer testId;
    private String programName;
    private Grid<LessonItemModel> limGrid;
    private int width = 400;
    private int height = 300;
    private boolean isCustomProgram;

    static LessonItemModelProperties __propsLessonItem = GWT.create(LessonItemModelProperties.class);
    public StudentLessonTopicsStatusWindow(StudentModelI student, final StudentActivityModel activityModel) {

        super(false);
        
        setStyleName("student-lesson-topic-status-window");
        runId = activityModel.getRunId();
        testId = activityModel.getTestId();

        setPixelSize(width, height);
        setResizable(false);
        setModal(true);

        programName = activityModel.getProgramDescr();
        isCustomProgram = programName.trim().startsWith("CP:");

        StringBuffer sb = new StringBuffer();
        sb.append("For ").append(student.getName());
        if (programName != null)
            sb.append(" in program ").append(programName);
        setHeadingText(sb.toString());

        final ListStore<LessonItemModel> store = new ListStore<LessonItemModel>(__propsLessonItem.id());
        ColumnModel<LessonItemModel> cm = defineColumns();

        limGrid = new Grid<LessonItemModel>(store, cm);
        limGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //limGrid.getSelectionModel().setFiresEvents(false);
        limGrid.getView().setStripeRows(true);
        limGrid.setWidth(width - 20);
        limGrid.setHeight(height - 70);

        SimpleContainer sp = new SimpleContainer();
        sp.setWidget(limGrid);
        add(sp);

        TextButton stateStandardsBtn = createStandardsButton();
        addButton(stateStandardsBtn);

        TextButton closeBtn = new TextButton("Close");
        closeBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();
            }
        });
        //closeBtn.setIconStyle("icon-delete");

        addButton(closeBtn);
        setVisible(false);

        getStudentLessonTopicsRPC(store);

    }
    
    private TextButton createStandardsButton() {
        TextButton btn = new TextButton("Standard Correlations");
        btn.setToolTip("Show standards for selected topic");

        Menu menu = new Menu();

        menu.add(new MenuItem("California", new SelectionHandler<MenuItem>() {
            public void onSelection(SelectionEvent<MenuItem> event) {
                showStandardsFor("California", "CA");
                }
        }));
        
        menu.add(new MenuItem("Texas",new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                showStandardsFor("Texas", "TX");
                }
        }));

        menu.add(new MenuItem("Utah",new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                showStandardsFor("Utah", "UT");
                }
        }));
        menu.add(new MenuItem("Common Core",new SelectionHandler<MenuItem>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<MenuItem> event) {
                showStandardsFor("Common Core", "common");
                }
        }));
        
        btn.setMenu(menu);

        return btn;        
    }

    private void showStandardsFor(String stateLabel, String state) {
        LessonItemModel lim = limGrid.getSelectionModel().getSelectedItem();
        if (lim != null) {
            new StudentLessTopicsStateStandardsWindow(lim,stateLabel,state);
        } else {
            CatchupMathTools.showAlert("Select a topic first");
        }
    }
    
    private ColumnModel<LessonItemModel> defineColumns() {
        List<ColumnConfig<LessonItemModel, ?>> configs = new ArrayList<ColumnConfig<LessonItemModel, ?>>();

        configs.add(new ColumnConfig<LessonItemModel, String>(__propsLessonItem.name(),240,"Topics Covered This Section"));
        configs.get(configs.size()-1).setMenuDisabled(true);

        configs.add(new ColumnConfig<LessonItemModel, String>(__propsLessonItem.prescribed(),130,"Prescribed for Review"));
        configs.get(configs.size()-1).setSortable(false);
        configs.get(configs.size()-1).setMenuDisabled(true);

        ColumnModel<LessonItemModel> cm = new ColumnModel<LessonItemModel>(configs);
        return cm;
    }

    protected void getStudentLessonTopicsRPC(final ListStore<LessonItemModel> store) {
        
        CatchupMathTools.setBusy(true);
        
        CmServiceAsync s = CmShared.getCmService();
        
        if (! isCustomProgram) {
        	s.execute(new   GetLessonItemsForTestRunAction(runId), new AsyncCallback<CmList<LessonItemModel>>() {

        		@Override
        		public void onSuccess(CmList<LessonItemModel> list) {
        			store.addAll(list);
        			setVisible(true);
        			CatchupMathTools.setBusy(false);
        		}

        		public void onFailure(Throwable caught) {
        			CatchupMathTools.setBusy(false);
        			caught.toString();
        			String msg = caught.getMessage();
        			CatchupMathTools.showAlert(msg);

        		}
        	});
        }
        else {
        	s.execute(new GetLessonItemsForCustomProgramTestAction(testId), new AsyncCallback<CmList<LessonItemModel>>() {

        		@Override
        		public void onSuccess(CmList<LessonItemModel> list) {
        			store.addAll(list);
        			setVisible(true);
        			CatchupMathTools.setBusy(false);
        		}

        		public void onFailure(Throwable caught) {
        			CatchupMathTools.setBusy(false);
        			caught.toString();
        			String msg = caught.getMessage();
        			CatchupMathTools.showAlert(msg);

        		}
        	});        	
        }
    }

}
