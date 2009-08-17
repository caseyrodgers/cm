package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.rpc.action.GetReportDefAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
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
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

    StudentModel studentModel;
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
    public StudentDetailsWindow(final StudentModel studentModel) {
        setStyleName("student-details-window");
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
        toolBar.setStyleName("student-details-window-toolbar");
        toolBar.add(showWorkBtn());
        toolBar.add(showTopicsBtn());
        toolBar.add(new FillToolItem());
        toolBar.add(displayPrintableReportToolItem(studentModel));

        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new BorderLayout());
        lc.setStyleName("student-details-window-container");

        lc.add(studentInfoPanel());
        lc.add(toolBar, new BorderLayoutData(LayoutRegion.NORTH, 35));

        LayoutContainer gridContainer = new LayoutContainer();
        gridContainer.setLayout(new FitLayout());
        gridContainer.setStyleName("student-details-panel-grid");
        gridContainer.add(samGrid);
        lc.add(gridContainer, new BorderLayoutData(LayoutRegion.CENTER));
        add(lc);

        _studentCount = new Label();
        _studentCount.setStyleName("students-count");
        // TODO: count not displaying correctly
        // add(_studentCount);

        Button btnClose = closeButton();
        setButtonAlign(HorizontalAlignment.RIGHT);
        addButton(btnClose);

        template.overwrite(html.getElement(), Util.getJsObject(studentModel));

        getStudentActivityRPC(store, studentModel);

        setVisible(true);
    }

    /**
     * Enable or disable buttons depending on user state.
     * 
     * If grid id empty disable all buttons.  If any rows
     * exist enable the showWork, and only enable the 
     * standards button is program supports tracking standards,
     * ie. Not Auto-enroll or Chapter tests.
     * 
     * 
     */
    private void enableDisableButtons() {
        
        if(this.samGrid.getStore().getCount() == 0) {
            _showTopicsBtn.disable();
            _showWorkButton.disable();
        }
        else {
            _showWorkButton.enable();
            StudentActivityModel sam = samGrid.getSelectionModel().getSelectedItem();
            if (sam != null) {
                String desc = sam.getProgramDescr();
                String dl = desc.toLowerCase();
                if (dl.indexOf("chap") > -1 || dl.indexOf("auto") > -1)
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

        _showTopicsBtn = new Button("Show Standards");
        _showTopicsBtn.disable();
        _showTopicsBtn.addSelectionListener(stListener);
        _showTopicsBtn.setStyleName("student-details-panel-sw-btn");
        return _showTopicsBtn;
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
        _showWorkButton.setStyleName("student-details-panel-sw-btn");
        return _showWorkButton;
    }

    /**
     * Display Topics for the currently selected row
     * 
     */
    private void showTopicsForSelected() {
        StudentActivityModel sam = samGrid.getSelectionModel().getSelectedItem();
        if (sam == null) {
            CatchupMathTools.showAlert("Select a row in the table first");
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
            CatchupMathTools.showAlert("Select a row in the table first");
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

    private Button displayPrintableReportToolItem(final StudentModel sm) {
        Button ti = new Button();
        ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable student detail report");
        ti.setStyleName("student-details-panel-pr-btn");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                displayPrintableReportRPC(sm);
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
        sb.append("  <div class='fld'><label>Pass %:</label><div>{pass-percent}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Tutoring:</label><div>{");
        sb.append(StudentModel.TUTORING_STATE_KEY).append("}&nbsp;</div></div>");
        sb.append("</div>");
        sb.append("<div class='form right'>");
        sb.append("  <div class='fld'><label>Passcode:</label><div>{passcode}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Show Work:</label><div>{");
        sb.append(StudentModel.SHOW_WORK_STATE_KEY).append("}&nbsp;</div></div>");
        sb.append("</div>");
        sb.append("</div>");

        template = XTemplate.create(sb.toString());
        html = new HTML();

        html.setHeight("70px"); // to eliminate the jump when setting values in
                                // template
    }

    protected void getStudentActivityRPC(final ListStore<StudentActivityModel> store, StudentModel sm) {
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getStudentActivity(sm, new AsyncCallback<List<StudentActivityModel>>() {

            public void onSuccess(List<StudentActivityModel> list) {
                store.add(list);

                _studentCount.setText("count: " + list.size());
                
                if(list.size() > 0) {
                    enableDisableButtons();
                }
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }

    protected void displayPrintableReportRPC(final StudentModel sm) {
        List<Integer> studentUids = new ArrayList<Integer>();
        studentUids.add(sm.getUid());

        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new GetReportDefAction(studentUids), new AsyncCallback<StringHolder>() {

            public void onSuccess(StringHolder reportId) {
                String url = "/cm_admin/genPDF?id=" + reportId.getResponse() + "&aid=" + sm.getAdminUid()
                        + "&type=studentDetail";
                Window.open(url, "_blank", "location=0,menubar=0,resizable=1");
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }
}
