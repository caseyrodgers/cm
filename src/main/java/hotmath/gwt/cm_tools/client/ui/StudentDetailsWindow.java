package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.ResetStudentActivityAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;
import hotmath.gwt.shared.client.rpc.action.GetStudentActivityAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
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
        setSize(645, 410);
        setModal(true);
        setResizable(false);
        setHeading("Student Details For: " + studentModel.getName());

        ListStore<StudentActivityModel> store = new ListStore<StudentActivityModel>();
        ColumnModel cm = defineColumns();

        samGrid = new Grid<StudentActivityModel>(store, cm);
        samGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        samGrid.getSelectionModel().setFiresEvents(true);
        samGrid.setStripeRows(true);
        samGrid.setWidth(565);
        samGrid.setHeight(225);

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
        toolBar.add(showQuizResultsBtn());
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
        gridContainer.setStyleName("student-details-panel-grid");
        gridContainer.add(samGrid);
        gridContainer.setHeight(250);
        
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
            Menu debugMenu = buildDebugMenu();
            samGrid.setContextMenu(debugMenu);
        }

        setVisible(true);
    }

	private Menu buildDebugMenu() {
		MenuItem detailDebug = new MenuItem("Debug Info");
		detailDebug.addSelectionListener(new SelectionListener<MenuEvent>() {
		    public void componentSelected(MenuEvent ce) {
		        StudentActivityModel m = samGrid.getSelectionModel().getSelectedItem();
		        CatchupMathTools.showAlert("testId: " + m.getTestId() + ", runId: " + m.getRunId());
		    }
		});
		Menu menu = new Menu();
		menu.add(detailDebug);

		MenuItem resetHistory = new MenuItem("Reset To Here");
		resetHistory.setToolTip("Reset student's history to this point");
		resetHistory.addSelectionListener(new SelectionListener<MenuEvent>() {
		    public void componentSelected(MenuEvent ce) {
		        StudentActivityModel sm = samGrid.getSelectionModel().getSelectedItem();
		        resetHistory(sm);
		    }
		});
		menu.add(resetHistory);
		return menu;
	}
	
	private void resetHistory(final StudentActivityModel studentModel) {
	    MessageBox.confirm("Reset History","Are you sure you want to reset this student's history to this point, erasing all later entries?", new Listener<MessageBoxEvent>() {
	        @Override
	        public void handleEvent(MessageBoxEvent be) {
	            if(!be.isCancelled()) {
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
                ResetStudentActivityAction action = new ResetStudentActivityAction(studentModel.getUid(),studentActivity.getTestId(), studentActivity.getRunId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
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

        if (this.samGrid.getStore().getCount() == 0) {
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
                else
                    _showTopicsBtn.enable();
                
                if(sam.getIsQuiz() && sam.getRunId() > 0) {
                    _showQuizResults.enable();
                    _showWorkButton.disable();
                }
                else {
                    _showQuizResults.disable();
                   _showWorkButton.enable();
                }
            }
        }
    }

    Button _showTopicsBtn, _showWorkButton, _showQuizResults;

    private Button showQuizResultsBtn() {
        _showQuizResults = new Button("Quiz Results", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                StudentActivityModel sam = samGrid.getSelectionModel().getSelectedItem();
                new ShowQuizResultsDialog(sam.getRunId());
            }
        });
        _showQuizResults.addStyleName("student-details-panel-sw-btn");
        _showQuizResults.disable();
        
        return _showQuizResults;
    }
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
        program.setWidth(120);
        program.setSortable(false);
        program.setMenuDisabled(true);
        configs.add(program);

        ColumnConfig programType = new ColumnConfig();
        programType.setId(StudentActivityModel.PROGRAM_TYPE_KEY);
        programType.setHeader("Prog-Type");
        programType.setWidth(115);
        programType.setSortable(false);
        programType.setMenuDisabled(true);
        configs.add(programType);

        ColumnConfig activity = new ColumnConfig();
        activity.setId(StudentActivityModel.ACTIVITY_KEY);
        activity.setHeader("Activity");
        activity.setWidth(90);
        activity.setSortable(false);
        activity.setMenuDisabled(true);
        configs.add(activity);

        ColumnConfig result = new ColumnConfig();
        result.setId(StudentActivityModel.RESULT_KEY);
        result.setHeader("Result");
        result.setWidth(165);
        result.setSortable(false);
        result.setMenuDisabled(true);
        configs.add(result);
        
        ColumnConfig timeOnTask = new ColumnConfig();
        timeOnTask.setId(StudentActivityModel.TIME_ON_TASK_KEY);
        timeOnTask.setHeader("Time");
        timeOnTask.setWidth(65);
        timeOnTask.setSortable(true);
        timeOnTask.setMenuDisabled(true);
        configs.add(timeOnTask);

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
        sb.append("  <div class='fld'><label>Password:</label><div>{");
        sb.append(StudentModelExt.PASSCODE_KEY).append("}&nbsp;</div></div>");
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
                    store.removeAll();
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
    
    
    
    
    
    static public void showStudentDetails(final int userId) {
        
        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetStudentModelAction action = new GetStudentModelAction(userId);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }            
            
            @Override
            public void oncapture(StudentModelI result) {
                CmBusyManager.setBusy(false);
                StudentModelExt sm = new StudentModelExt(result);
                new StudentDetailsWindow(sm);
            }
        }.register();
    }    
}
