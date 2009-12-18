package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetLessonItemsForTestRunAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
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

        Button stateStandardsBtn = new Button("California Standards");
        stateStandardsBtn.setToolTip("Show corresponding California state standards");
        stateStandardsBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                LessonItemModel lim = limGrid.getSelectionModel().getSelectedItem();
                if (lim != null) {
                    new StudentLessTopicsStateStandardsWindow(lim);
                } else {
                    CatchupMathTools.showAlert("Select a standard first");
                }
            }
        });

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

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig topicName = new ColumnConfig();
        topicName.setId(LessonItemModel.NAME_KEY);
        topicName.setHeader("Standards Covered This Section");
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
        
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new   GetLessonItemsForTestRunAction(runId), new AsyncCallback<CmList<LessonItemModel>>() {

            //@Override
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
