package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentActivityAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/*
 * Displays historical record of Student activity in reverse chronological order
 *
 * Derived from StudentDetailsPanel (retired)
 * 
 * @author Bob
 * @author Casey
 * 
 */

public class StudentDetailsWindow extends CmWindow {

    StudentModelExt studentModel;
    private HTML html;
    private XTemplate template;
    private Grid<StudentActivityModel> samGrid;
    private Label _studentCount;

    /**
     * Create StudentDetailsWindow for student. Shows all student activity for
     * given user order by last use (most recent first)
     * 
     * StudentModel must be fully filled out to populate the infoPanel
     * 
     * @param studentModel
     */
    public StudentDetailsWindow(final StudentModelExt studentModel) {
        addStyleName("student-details-window");
        this.studentModel = studentModel;
        setSize(580, 410);
        setModal(true);
        setResizable(false);
        setHeading("Student Details For: " + studentModel.getName());

        ListStore<StudentActivityModel> store = new ListStore<StudentActivityModel>();
        ColumnModel cm = defineColumns();

        samGrid = new Grid<StudentActivityModel>(store, cm);
        // samGrid.setStyleName("student-details-panel-grid");
        samGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        samGrid.getSelectionModel().setFiresEvents(true);
        samGrid.setStripeRows(true);
        samGrid.setWidth(565);
        samGrid.setHeight(210);

        samGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<StudentActivityModel>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<StudentActivityModel> se) {
                enableDisableButtons();
            }
        });

        ToolBar toolBar = new ToolBar();
        toolBar.add(showWorkBtn());
        toolBar.add(showTopicsBtn());
        toolBar.add(displayReportCardToolItem(studentModel));
        toolBar.add(showTutoringUseBtn());
        toolBar.add(new FillToolItem());
        toolBar.add(displayPrintableReportToolItem(studentModel));

        setLayout(new BorderLayout());
        addStyleName("student-details-window-container");

        LayoutContainer lc = new LayoutContainer();
        lc.add(studentInfoPanel());
        lc.add(toolBar);
        add(lc, new BorderLayoutData(LayoutRegion.NORTH, 50));

        LayoutContainer gridContainer = new LayoutContainer();
        gridContainer.setLayout(new FitLayout());
        gridContainer.addStyleName("student-details-panel-grid");
        gridContainer.add(samGrid);
        add(gridContainer, new BorderLayoutData(LayoutRegion.CENTER));

        _studentCount = new Label();
        _studentCount.addStyleName("students-count");
        // TODO: count not displaying correctly
        // add(_studentCount);

        Button btnClose = closeButton();
        setButtonAlign(HorizontalAlignment.RIGHT);
        addButton(btnClose);

        template.overwrite(html.getElement(), Util.getJsObject(studentModel));

        getStudentActivityRPC(store, studentModel);

        if (CmShared.getQueryParameter("debug") != null) {
            MenuItem detailDebug = new MenuItem("Debug Info");
            detailDebug.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    StudentActivityModel m = samGrid.getSelectionModel().getSelectedItem();
                    CatchupMathTools.showAlert("testId: " + m.getTestId() + ", runId: " + m.getRunId());
                }
            });
            final Menu contextMenu = new Menu();
            contextMenu.add(detailDebug);

            MenuItem detailLogin = new MenuItem("Login/View");
            detailLogin.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    StudentActivityModel sm = samGrid.getSelectionModel().getSelectedItem();
                    loginAsSelectedUser(sm);
                }
            });
            contextMenu.add(detailLogin);

            samGrid.setContextMenu(contextMenu);
        }

        setVisible(true);
    }

    /**
     * Logon as selected user, connecting to selected test/run
     * 
     */
    private void loginAsSelectedUser(StudentActivityModel sm) {

        String server = CmShared.getServerForCmStudent();

        String url = server + "/cm_student/CatchupMath.html?debug=true&uid=" + studentModel.getUid() + "&debug_info="
                + studentModel.getUid() + ":" + sm.getTestId() + ":" + sm.getRunId();

        Window.open(url, "_blank", "location=1,menubar=1,resizable=1,scrollbars=1");
    }

    /**
     * Enable or disable buttons depending on user state.
     * 
     * If grid is empty disable all buttons. If any rows exist, enable the
     * showWork button and only enable the standards button if program supports
     * tracking standards; ie - Not Auto-enroll or Chapter tests.
     * 
     * 
     */
    private void enableDisableButtons() {

        if (this.samGrid.getStore().getCount() == 0) {
            _showTopicsBtn.disable();
            _showWorkButton.disable();
        } else {
            _showWorkButton.enable();
            _showTopicsBtn.enable();
            StudentActivityModel sam = samGrid.getSelectionModel().getSelectedItem();
            if (sam != null) {
                String desc = sam.getProgramDescr();
                String dl = desc.toLowerCase();
                if (dl.indexOf("auto") > -1)
                    _showTopicsBtn.disable();
                else
                    _showTopicsBtn.enable();
            }
        }
    }

    Button _showTopicsBtn, _showWorkButton;

    private Button showTopicsBtn() {
        SelectionListener<ButtonEvent> stListener = new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showTopicsForSelected();
            }
        };

        _showTopicsBtn = new Button("Show Topics");
        _showTopicsBtn.disable();
        _showTopicsBtn.addSelectionListener(stListener);
        _showTopicsBtn.addStyleName("student-details-panel-sw-btn");
        return _showTopicsBtn;
    }

    private Button showTutoringUseBtn() {

        Button btn = new Button("Tutoring Sessions");
        btn.addStyleName("student-details-panel-sw-btn");

        if(this.studentModel.getTutoringUse() == 0) {
            btn.setEnabled(false);
        }
        else {
            btn.setToolTip("Display any tutoring sessions created by this user");
            btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
    
                @Override
                public void componentSelected(ButtonEvent ce) {
                    showTutoringForUser();
                }
            });
        }
        return btn;
    }

    private void showTutoringForUser() {

        String contentUrl = "pid=ADMIN_VIEW";
        String url = "/collab/lwl/cm_lwl_launch.jsp?admin=true&pid=ADMIN_VIEW&uid=" + studentModel.getUid()
                + "&contentUrl=" + contentUrl;

        int w = 800;
        int h = 560;
        String windowProps = "toolbar=0, titlebar=0, status=0, menubar=0, resizable=0, width=" + w + ", height=" + h
                + ", directories=0, location=0,scrollbars=0,directories=0,location=0";

        com.google.gwt.user.client.Window.open(url, "_blank", windowProps);
    }

    private Button showWorkBtn() {
        SelectionListener<ButtonEvent> swListener = new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showWorkForSelected();
            }
        };

        _showWorkButton = new Button("Show Work");
        _showWorkButton.disable();
        _showWorkButton.addSelectionListener(swListener);
        _showWorkButton.addStyleName("student-details-panel-sw-btn");
        return _showWorkButton;
    }

    /**
     * Display Topics for the currently selected row
     * 
     */
    private void showTopicsForSelected() {
        StudentActivityModel sam = samGrid.getSelectionModel().getSelectedItem();
        if (sam == null) {
            CatchupMathTools.showAlert("Select a Quiz or Review from the list first");
            return;
        }
        
        /** only show list if a Review is selected, quizzes do not have lessons
         * 
         */
        if(sam.getRunId() == 0) {
            CatchupMathTools.showAlert("Topics not shown unless Quiz is completed");
            return;
        }
        new StudentLessonTopicsStatusWindow(studentModel, sam);
    }

    /**
     * Display show work for the currently selected resource all of its
     * children.
     * 
     */
    private void showWorkForSelected() {
        StudentActivityModel sam = samGrid.getSelectionModel().getSelectedItem();
        if (sam == null) {
            CatchupMathTools.showAlert("Select a Review from the list first");
            return;
        }
        new StudentShowWorkWindow(studentModel, sam);
    }

    private Button closeButton() {
        Button btn = new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        btn.setIconStyle("icon-delete");
        return btn;
    }

    private Button displayPrintableReportToolItem(final StudentModelExt sm) {
        Button ti = new Button();
        ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable student detail report");
        ti.addStyleName("student-details-panel-pr-btn");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {

                new PdfWindow(sm.getAdminUid(), "Catchup Math Details Report for: " + sm.getName(),
                        new GeneratePdfAction(PdfType.STUDENT_DETAIL, sm.getAdminUid(), Arrays.asList(sm.getUid())));
            }
        });
        return ti;
    }

    private Button displayReportCardToolItem(final StudentModelExt sm) {
        Button ti = new Button();
        // ti.setIconStyle("printer-icon");
        ti.setText("Report Card");
        ti.setToolTip("Display a printable report card");
        ti.addStyleName("student-details-panel-sw-btn");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new PdfWindow(sm.getAdminUid(), "Catchup Math Report Card for: " + sm.getName(), new GeneratePdfAction(
                        PdfType.REPORT_CARD, sm.getAdminUid(), Arrays.asList(sm.getUid())));
            }
        });
        return ti;
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig date = new ColumnConfig();
        date.setId(StudentActivityModel.USE_DATE_KEY);
        date.setHeader("Date");
        date.setWidth(75);
        date.setSortable(false);
        date.setMenuDisabled(true);
        configs.add(date);

        ColumnConfig program = new ColumnConfig();
        program.setId(StudentActivityModel.PROGRAM_KEY);
        program.setHeader("Program");
        program.setWidth(105);
        program.setSortable(false);
        program.setMenuDisabled(true);
        configs.add(program);

        ColumnConfig activity = new ColumnConfig();
        activity.setId(StudentActivityModel.ACTIVITY_KEY);
        activity.setHeader("Activity-Section");
        activity.setWidth(125);
        activity.setSortable(false);
        activity.setMenuDisabled(true);
        configs.add(activity);

        ColumnConfig result = new ColumnConfig();
        result.setId(StudentActivityModel.RESULT_KEY);
        result.setHeader("Result");
        result.setWidth(225);
        result.setSortable(false);
        result.setMenuDisabled(true);
        configs.add(result);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    private Widget studentInfoPanel() {
        defineStudentInfoTemplate();
        return html;
    }

    /**
     * Define the template for the header (does not add to container)
     * 
     * Defines the global 'html' object, filled in via RPC call.
     * 
     */
    private void defineStudentInfoTemplate() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='detail-info'>");
        sb.append("<div class='form left'>");
        sb.append("  <div class='fld'><label>Password:</label><div>{passcode}&nbsp;</div></div>");
        sb.append("</div>");
        sb.append("<div class='form right'>");
        sb.append("  <div class='fld'><label>Show Work:</label><div>{");
        sb.append(StudentModelExt.SHOW_WORK_STATE_KEY).append("}&nbsp;</div></div>");
        sb.append("</div>");
        sb.append("</div>");

        template = XTemplate.create(sb.toString());
        html = new HTML();

        html.setHeight("35px"); // to eliminate the jump when setting values in
        // template
    }

    protected void getStudentActivityRPC(final ListStore<StudentActivityModel> store, final StudentModelExt sm) {

        CmBusyManager.setBusy(true);

        new RetryAction<CmList<StudentActivityModel>>() {
            public void oncapture(CmList<StudentActivityModel> list) {
                try {
                    store.add(list);

                    _studentCount.setText("count: " + list.size());

                    if (list.size() > 0) {
                        enableDisableButtons();
                    }
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }

            @Override
            public void attempt() {
                CmServiceAsync s = CmShared.getCmService();
                GetStudentActivityAction action = new GetStudentActivityAction(sm);
                setAction(action);
                s.execute(action, this);
            }
        }.register();
    }
}
