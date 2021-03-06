package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc.client.rpc.ResetStudentActivityAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageByStrandWindow;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageChartWindow;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;
import hotmath.gwt.shared.client.rpc.action.GetStudentActivityAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/*
 * Displays historical record of Student activity in reverse chronological order
 *
 * Derived from StudentDetailsPanel (retired)
 * 
 * @author Bob
 * @author Casey
 * 
 */

public class StudentDetailsPanel extends BorderLayoutContainer {

    StudentModelI studentModel;
    private Grid<StudentActivityModel> samGrid;
    private Label _studentCount;
    private HTML dateRange = new HTML();
    DetailsProperties detailsProps = GWT.create(DetailsProperties.class);

    /**
     * Create StudentDetailsWindow for student. Shows all student activity for
     * given user order by last use (most recent first)
     * 
     * StudentModel must be fully filled out to populate the infoPanel
     * 
     * @param studentModel
     */
    public StudentDetailsPanel(final StudentModelI studentModel) {
        this.studentModel = studentModel;

        if (studentModel.getSettings().getShowWorkRequired()) {
            studentModel.setShowWorkState("REQUIRED");
        } else {
            studentModel.setShowWorkState("OPTIONAL");
        }

        ListStore<StudentActivityModel> store = new ListStore<StudentActivityModel>(detailsProps.key());

        ColumnModel<StudentActivityModel> cm = defineColumns();

        samGrid = new Grid<StudentActivityModel>(store, cm);
        samGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // samGrid.getSelectionModel().setFiresEvents(true);
        samGrid.getView().setStripeRows(true);
        samGrid.getView().setAutoExpandColumn(cm.getColumn(cm.getColumnCount() - 1));
        samGrid.getView().setAutoFill(true);

        samGrid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<StudentActivityModel>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<StudentActivityModel> event) {
                enableDisableButtons();
            }
        });

        ToolBar toolBar = new ToolBar();
        toolBar.add(showWorkBtn());
        toolBar.add(showTopicsBtn());
        toolBar.add(displayReportCardToolItem(studentModel));

        toolBar.add(displayAssignmentReportToolItem(studentModel));
        if (CmCore.getQueryParameter("debug") != null) {
            toolBar.add(displayAssignmentWorkToolItem(studentModel));
        }

        toolBar.add(createCCSSButton(studentModel));
        
        toolBar.add(showQuizResultsBtn());
        
        toolBar.add(createActivityLogButton());
        
        toolBar.add(new FillToolItem());
        toolBar.add(displayPrintableReportToolItem(studentModel));

        FlowLayoutContainer northContainer = new FlowLayoutContainer();
        StudentInfoPanel infoPanel = new StudentInfoPanel(studentModel);
        infoPanel.setHeight(60);
        northContainer.add(infoPanel);
        northContainer.add(toolBar);

        setNorthWidget(northContainer, new BorderLayoutData(100));

        SimpleContainer simpContainer = new SimpleContainer();
        simpContainer.add(samGrid);

        setCenterWidget(simpContainer);

        _studentCount = new Label();
        _studentCount.addStyleName("students-count");

        getStudentActivityRPC(store, studentModel);

        if (CmCore.isDebug() == true) {
            Menu debugMenu = buildDebugMenu();
            samGrid.setContextMenu(debugMenu);
        }

        refreshDateRangeLabel();
        dateRange.addStyleName("date-range-label");
    }

    private Widget createActivityLogButton() {
        TextButton txt = new TextButton("Time Log");
        txt.setToolTip("Show student active time log");
        txt.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new UserActivityLogDialog(studentModel);
            }
        });
        return txt;
    }

    class TestData {
        String name, value;

        public TestData(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    interface DataProperties extends PropertyAccess<String> {

        ValueProvider<TestData, String> name();

        ValueProvider<TestData, String> value();

        @Path("name")
        ModelKeyProvider<TestData> key();

    }

    private Menu buildDebugMenu() {
        MenuItem detailDebug = new MenuItem("Debug Info");
        detailDebug.addSelectionHandler(new SelectionHandler<Item>() {

            @Override
            public void onSelection(SelectionEvent<Item> event) {
                StudentActivityModel m = samGrid.getSelectionModel().getSelectedItem();
                CatchupMathTools.showAlert("testId: " + m.getTestId() + ", runId: " + m.getRunId());
            }
        });
        Menu menu = new Menu();
        menu.add(detailDebug);

        MenuItem resetHistory = new MenuItem("Reset To Here");
        resetHistory.setToolTip("Reset student's history to this point");
        resetHistory.addSelectionHandler(new SelectionHandler<Item>() {
            public void onSelection(SelectionEvent<Item> event) {
                StudentActivityModel sm = samGrid.getSelectionModel().getSelectedItem();
                resetHistory(sm);
            }
        });
        menu.add(resetHistory);
        return menu;
    }

    private void resetHistory(final StudentActivityModel studentModel) {
        CmMessageBox.confirm("Reset History", "Are you sure you want to reset this student's history to this point, erasing all later entries?",
                new ConfirmCallback() {
                    @Override
                    public void confirmed(boolean yesNo) {
                        if (yesNo) {
                            resetHistoryAux(studentModel);
                        }
                    }
                });
    }

    private void resetHistoryAux(final StudentActivityModel studentActivity) {

        new RetryAction<RpcData>() {
            public void oncapture(RpcData list) {
                getStudentActivityRPC(samGrid.getStore(), studentModel);
                hotmath.gwt.shared.client.eventbus.EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }

            @Override
            public void attempt() {
                ResetStudentActivityAction action = new ResetStudentActivityAction(studentModel.getUid(), studentActivity.getTestId(),
                        studentActivity.getRunId());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }
        }.register();
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

        if (this.samGrid.getStore().size() == 0) {
            _showTopicsBtn.disable();
            _showWorkButton.disable();
            _showQuizResults.disable();
        } else {
            _showWorkButton.enable();
            _showTopicsBtn.enable();
            StudentActivityModel sam = samGrid.getSelectionModel().getSelectedItem();
            if (sam != null) {
                String desc = sam.getProgramDescr();
                String dl = desc.toLowerCase();
                if (dl.indexOf("auto") > -1)
                    _showTopicsBtn.disable();

                if (sam.isQuiz() && sam.getRunId() > 0) {
                    _showQuizResults.enable();
                    _showWorkButton.disable();
                } else {
                    _showQuizResults.disable();
                    if (sam.isAssignment() == true)
                    	_showWorkButton.disable();
                }
            }
        }
    }

    TextButton _showTopicsBtn, _showWorkButton, _showQuizResults;

    private TextButton showQuizResultsBtn() {
        _showQuizResults = new TextButton("Quiz Results", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                StudentActivityModel sam = samGrid.getSelectionModel().getSelectedItem();
                new ShowQuizResultsDialog(sam.getRunId());
            }
        });
        _showQuizResults.addStyleName("student-grid-panel-button");
        _showQuizResults.disable();

        return _showQuizResults;
    }

    private TextButton showTopicsBtn() {

        _showTopicsBtn = new TextButton("Show Topics");
        _showTopicsBtn.disable();
        _showTopicsBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                showTopicsForSelected();
            }
        });
        _showTopicsBtn.addStyleName("student-grid-panel-button");
        return _showTopicsBtn;
    }

    private TextButton showWorkBtn() {
        _showWorkButton = new TextButton("Show Work");
        _showWorkButton.disable();
        _showWorkButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showWorkForSelected();
            }
        });
        _showWorkButton.addStyleName("student-grid-panel-button");
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

        if (sam.isCustomQuiz()) {
            CatchupMathTools.showAlert("Custom Quizzes do have assigned topics");
            return;
        }

        if (sam.isAssignment()) {
            CatchupMathTools.showAlert("For more details, click \"Assignments Report\" or use the Assignments tool.");
            return;
        }

        /**
         * only show list if a Review is selected, quizzes do not have lessons
         * 
         */
        if (sam.getRunId() == 0) {
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

        if (sam.isAssignment()) {
            CatchupMathTools.showAlert("For more details, click \"Assignments Report\" or use the Assignments tool.");
            return;
        }

        new StudentShowWorkWindow(studentModel, sam);
    }

    private TextButton displayPrintableReportToolItem(final StudentModelI sm) {
        TextButton ti = new TextButton("Print", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                DateRangePanel dateRange = DateRangePanel.getInstance();
                Date fromDate = dateRange != null ? dateRange.getFromDate() : null;
                Date toDate = dateRange != null ? dateRange.getToDate() : null;
                new PdfWindow(sm.getAdminUid(), "Catchup Math Details Report for: " + sm.getName(), new GeneratePdfAction(PdfType.STUDENT_DETAIL,
                        sm.getAdminUid(), Arrays.asList(sm.getUid()), fromDate, toDate));
            }
        });
        // ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable student detail report");
        //ti.addStyleName("student-details-panel-pr-btn");
        return ti;
    }

    private TextButton displayReportCardToolItem(final StudentModelI sm) {
        TextButton ti = new TextButton("Report Card", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                DateRangePanel dateRange = DateRangePanel.getInstance();
                Date fromDate = dateRange != null ? dateRange.getFromDate() : null;
                Date toDate = dateRange != null ? dateRange.getToDate() : null;
                new PdfWindow(sm.getAdminUid(), "Catchup Math Report Card for: " + sm.getName(), new GeneratePdfAction(PdfType.REPORT_CARD, sm.getAdminUid(),
                        Arrays.asList(sm.getUid()), fromDate, toDate));
            }
        });
        // ti.setIconStyle("printer-icon");
        ti.setText("Report Card");
        ti.setToolTip("Display a printable report card");
        ti.addStyleName("student-grid-panel-button");
        return ti;
    }

    private TextButton displayAssignmentReportToolItem(final StudentModelI sm) {
        TextButton ti = new TextButton("Assignment Report", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                DateRangePanel dateRange = DateRangePanel.getInstance();
                Date fromDate = null, toDate = null;
                if (dateRange != null) {
                    fromDate = dateRange.getFromDate();
                    toDate = dateRange.getToDate();
                }
                new PdfWindow(sm.getAdminUid(), "Catchup Math Assignment Report for: " + sm.getName(), new GeneratePdfAction(PdfType.ASSIGNMENT_REPORT,
                        sm.getAdminUid(), Arrays.asList(sm.getUid()), fromDate, toDate));
            }
        });
        // ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable assignment report");
        ti.addStyleName("student-grid-panel-button");

        return ti;
    }

    private TextButton displayAssignmentWorkToolItem(final StudentModelI sm) {
        TextButton ti = new TextButton("Assignment Work", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
            	new StudentAssignmentWorkWindow(sm);
            }
        });
        ti.setToolTip("Show student's assignment work");
        ti.addStyleName("student-grid-panel-button");

        return ti;
    }

    private TextButton createCCSSButton(StudentModelI sm) {
        TextButton btn = new StudentPanelButton("CCSS");
        btn.setToolTip("View CCSS Coverage table and charts");

        Menu menu = new Menu();
        menu.add(defineCCSSCoverageTableItem(sm));
        menu.add(defineCCSSCoverageChartsItem(sm));
        menu.add(defineCCSSStrandCoverageItem(sm));
        
        btn.setMenu(menu);
        return btn;
    }

    private MyMenuItem defineCCSSCoverageTableItem(final StudentModelI sm) {
        return new MyMenuItem("Coverage Table", "View a CCSS coverage table.", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                new CCSSCoverageWindow(sm, null);
            }
        });
    }

    private MyMenuItem defineCCSSCoverageChartsItem(final StudentModelI sm) {
        return new MyMenuItem("Coverage Charts", "View CCSS coverage charts.", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                new CCSSCoverageChartWindow(sm.getAdminUid(), sm.getUid(), false, sm.getName());
            }
        });
    }

    private MyMenuItem defineCCSSStrandCoverageItem(final StudentModelI sm) {
        return new MyMenuItem("Strand Coverage Table", "View CCSS Strand coverage tables.", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                new CCSSCoverageByStrandWindow(sm, null);
            }
        });
    }

    private ColumnModel<StudentActivityModel> defineColumns() {

        List<ColumnConfig<StudentActivityModel, ?>> configs = new ArrayList<ColumnConfig<StudentActivityModel, ?>>();

        configs.add(new ColumnConfig<StudentActivityModel, String>(detailsProps.useDate(), 75, "Date"));
        configs.get(configs.size() - 1).setSortable(false);
        configs.get(configs.size() - 1).setMenuDisabled(true);

        configs.add(new ColumnConfig<StudentActivityModel, String>(detailsProps.program(), 120, "Program"));
        configs.get(configs.size() - 1).setMenuDisabled(true);
        configs.get(configs.size() - 1).setCell(new AbstractCell() {
            @Override
            public void render(Context context, Object value, SafeHtmlBuilder sb) {
                StudentActivityModel sam = samGrid.getStore().get(context.getIndex());
                String html = null;
                if (sam.isArchived()) {
                    html = sam.getProgramDescr() + " [archived]";
                } else {
                    html = sam.getProgramDescr();
                }
                if(html == null) {
                    html = "---";
                }
                sb.appendEscaped(html);
            }
        });

        configs.add(new ColumnConfig<StudentActivityModel, String>(detailsProps.programType(), 115, "Prog-Type"));
        configs.get(configs.size() - 1).setSortable(false);
        configs.get(configs.size() - 1).setMenuDisabled(true);

        configs.add(new ColumnConfig<StudentActivityModel, String>(detailsProps.activity(), 90, "Activity"));
        configs.get(configs.size() - 1).setSortable(false);
        configs.get(configs.size() - 1).setMenuDisabled(true);

        configs.add(new ColumnConfig<StudentActivityModel, String>(detailsProps.result(), 165, "Result"));
        configs.get(configs.size() - 1).setSortable(false);
        configs.get(configs.size() - 1).setMenuDisabled(true);

        ColumnModel<StudentActivityModel> cm = new ColumnModel<StudentActivityModel>(configs);
        return cm;
    }

    protected void getStudentActivityRPC(final ListStore<StudentActivityModel> store, final StudentModelI sm) {

        CmBusyManager.setBusy(true);

        new RetryAction<CmList<StudentActivityModel>>() {
            public void oncapture(CmList<StudentActivityModel> list) {
                try {
                    store.clear();
                    store.addAll(list);
                    _studentCount.setText("count: " + list.size());
                    if (list.size() > 0) {
                        enableDisableButtons();
                    }
                } catch (Exception e) {
                    Log.error("Error: " + list.size(), e);
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }

            @Override
            public void attempt() {
                CmServiceAsync s = CmRpcCore.getCmService();
                DateRangePanel dateRange = DateRangePanel.getInstance();
                Date fromDate = dateRange != null ? dateRange.getFromDate() : null;
                Date toDate = dateRange != null ? dateRange.getToDate() : null;

                GetStudentActivityAction action = new GetStudentActivityAction(sm, fromDate, toDate);
                setAction(action);
                s.execute(action, this);
            }
        }.register();
    }

    private void refreshDateRangeLabel() {
        if (DateRangePanel.getInstance() != null) {
            dateRange.setText("Date range: " + DateRangePanel.getInstance().formatDateRange());
        }
    }

    public Widget getDateRange() {
        return dateRange;
    }
}

interface DetailsProperties extends PropertyAccess<StudentActivityModel> {
    @Path("id")
    ModelKeyProvider<StudentActivityModel> key();

    ValueProvider<StudentActivityModel, String> useDate();

    ValueProvider<StudentActivityModel, String> program();

    ValueProvider<StudentActivityModel, String> programType();

    ValueProvider<StudentActivityModel, String> activity();

    ValueProvider<StudentActivityModel, String> result();
}
