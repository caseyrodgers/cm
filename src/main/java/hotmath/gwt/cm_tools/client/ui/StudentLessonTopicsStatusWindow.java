package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForTestRunAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Display Student's Lesson Topic (Prescribed Standards) Status
 * 
 * @author Bob
 * 
 */
public class StudentLessonTopicsStatusWindow extends CmWindow {

    private Integer runId;
    private String programName;
    private Grid<LessonItemModel> limGrid;
    private int width = 400;
    private int height = 300;

    public StudentLessonTopicsStatusWindow(StudentModelExt student, final StudentActivityModel activityModel) {

        setStyleName("student-lesson-topic-status-window");
        runId = activityModel.getRunId();
        setSize(width, height);
        setResizable(false);
        super.setModal(true);

        programName = activityModel.getProgramDescr();

        setLayout(new BorderLayout());
        StringBuffer sb = new StringBuffer();
        sb.append("For ").append(student.getName());
        if (programName != null)
            sb.append(" in program ").append(programName);
        setHeading(sb.toString());

        final ListStore<LessonItemModel> store = new ListStore<LessonItemModel>();
        ColumnModel cm = defineColumns();

        limGrid = new Grid<LessonItemModel>(store, cm);
        limGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        limGrid.getSelectionModel().setFiresEvents(false);
        limGrid.setStripeRows(true);
        limGrid.setWidth(width - 20);
        limGrid.setHeight(height - 70);

        LayoutContainer cp = new LayoutContainer();
        cp.setLayout(new FitLayout());
        cp.add(limGrid);
        add(cp);

        Button stateStandardsBtn = createStandardsButton();
        addButton(stateStandardsBtn);

        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        closeBtn.setIconStyle("icon-delete");

        addButton(closeBtn);
        setVisible(false);

        this.getStudentLessonTopicsRPC(store);

    }
    
    private Button createStandardsButton() {
        Button btn = new Button("State Correlations");
        btn.setToolTip("Show standards for selected topic");

        Menu menu = new Menu();

        menu.add(new MenuItem("California",new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                showStandardsFor("California", "CA");
                }
        }));
        menu.add(new MenuItem("Texas",new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                showStandardsFor("Texas", "TX");
                }
        }));

        menu.add(new MenuItem("Utah",new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                showStandardsFor("Utah", "UT");
                }
        }));
        menu.add(new MenuItem("Common Core",new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
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
    
    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig topicName = new ColumnConfig();
        topicName.setId(LessonItemModel.NAME_KEY);
        topicName.setHeader("Topics Covered This Section");
        topicName.setWidth(240);
        topicName.setSortable(true);
        topicName.setMenuDisabled(true);
        configs.add(topicName);

        ColumnConfig prescribed = new ColumnConfig();
        prescribed.setId(LessonItemModel.PRESCRIBED_KEY);
        prescribed.setHeader("Prescribed for Review");
        prescribed.setWidth(130);
        prescribed.setSortable(false);
        prescribed.setMenuDisabled(true);
        configs.add(prescribed);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    protected void getStudentLessonTopicsRPC(final ListStore<LessonItemModel> store) {
        
        CatchupMathTools.setBusy(true);
        
        CmServiceAsync s = CmShared.getCmService();
        s.execute(new   GetLessonItemsForTestRunAction(runId), new AsyncCallback<CmList<LessonItemModel>>() {

            @Override
            public void onSuccess(CmList<LessonItemModel> list) {
                store.add(list);
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
