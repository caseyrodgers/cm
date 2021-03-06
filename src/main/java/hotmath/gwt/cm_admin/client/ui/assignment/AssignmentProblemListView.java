package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentDesigner.Callback;
import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetCorrelatedTopicsPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStats;
import hotmath.gwt.cm_rpc_assignments.client.model.PidStats;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetAssignmentRealTimeStatsAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageForLessonsWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

public class AssignmentProblemListView extends ContentPanel {

    final Grid<ProblemDtoLocal> problemListGrid;
    Assignment _assignment;
    Callback callback;

    VerticalLayoutContainer problemListContainer;
    private TextButton _deleteButton;

    MyOrdinalProvider ordinalNumberValueProvider;
    private TextButton _upButton;
    private TextButton _downButton;
    private TextButton _ccssButton;
    ProblemDtoLocal _selectedRecord;
    private boolean _isModified = false;

    public AssignmentProblemListView(Assignment assignment, final Callback callback) {

        this._assignment = assignment;
        this.callback = callback;

        boolean isDraft = assignment.getStatus().equals("Draft");
        
        
        AssignmentProblemListPanelProperties props = GWT.create(AssignmentProblemListPanelProperties.class);

        ordinalNumberValueProvider = new MyOrdinalProvider();
        List<ColumnConfig<ProblemDtoLocal, ?>> cols = new ArrayList<ColumnConfig<ProblemDtoLocal, ?>>();
        cols.add(new ColumnConfig<ProblemDtoLocal, Integer>(props.ordinal(), 25, ""));
        cols.get(cols.size() - 1).setMenuDisabled(true);
        cols.add(new ColumnConfig<ProblemDtoLocal, String>(props.labelWithType(), 150, "Problems Assigned"));
        cols.get(cols.size() - 1).setMenuDisabled(true);

        if(!isDraft) {
	        ColumnConfig<ProblemDtoLocal, String> percentCol = new ColumnConfig<ProblemDtoLocal, String>(props.percentCorrect(), 55, "Correct");
	        percentCol.setComparator(new Comparator<String>() {
	            @Override
	            public int compare(String o1, String o2) {
	                /** strip of percent and treat as number */
	                if (o1 == null || o2 == null) {
	                    return 0;
	                }
	                int i1 = Integer.parseInt(o1.split("%")[0].trim());
	                int i2 = Integer.parseInt(o2.split("%")[0].trim());
	                return i1 - 12;
	            }
	        });
	        percentCol.setMenuDisabled(true);
	        cols.add(percentCol);
	
	        cols.get(cols.size() - 1).setToolTip(SafeHtmlUtils.fromString("Percentage of correct answers"));
	        cols.get(cols.size() - 1).setMenuDisabled(true);
	        cols.get(cols.size() - 1).setToolTip(SafeHtmlUtils.fromSafeConstant("Display percentage of correct answers"));
        } 
	        

        ModelKeyProvider<ProblemDtoLocal> kp = new ModelKeyProvider<ProblemDtoLocal>() {
            @Override
            public String getKey(ProblemDtoLocal item) {
                return item.getPid();
            }
        };

        ListStore<ProblemDtoLocal> store = new ListStore<ProblemDtoLocal>(kp);

        if (assignment.getPids() != null) {
            int ordinal = 0;
            for (ProblemDto prob : assignment.getPids()) {
                store.add(new ProblemDtoLocal(assignment, prob, ++ordinal));
            }
        }

        ContentPanel root = this;
        root.setHeadingText("Assigned Problems");
        // root.getHeader().setIcon(ExampleImages.INSTANCE.table());
        root.addStyleName("margin-10");

        ColumnModel<ProblemDtoLocal> probColModel = new ColumnModel<ProblemDtoLocal>(cols);
        problemListGrid = new Grid<ProblemDtoLocal>(store, probColModel);
        problemListGrid.getView().setAutoExpandColumn(cols.get(1));
        problemListGrid.getView().setStripeRows(true);
        problemListGrid.getView().setColumnLines(true);
        problemListGrid.setBorders(false);
        problemListGrid.setHideHeaders(false);
        problemListGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        problemListGrid.setColumnReordering(true);

        problemListGrid.getSelectionModel().addSelectionHandler(new SelectionHandler<ProblemDtoLocal>() {
            @Override
            public void onSelection(SelectionEvent<ProblemDtoLocal> event) {
                showSelectedProblemHtml(callback);
            }
        });

        /**
         * If active assignment, then provide real time stats
         * 
         */
        if (!isDraft) {
            problemListGrid.addRowDoubleClickHandler(new RowDoubleClickHandler() {
                @Override
                public void onRowDoubleClick(RowDoubleClickEvent event) {
                    showUserProblemStats();
                }
            });

            
            TextButton answers = new TextButton("Answer Stats", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    showUserProblemStats();
                }
            });
            addTool(answers);
        }

        problemListContainer = new VerticalLayoutContainer();

        problemListContainer.add(problemListGrid, new VerticalLayoutData(1, 1));

        if (store.size() == 0) {
            root.setWidget(createDefaultNoProblemsMessge());
        }
        else {
            root.setWidget(problemListContainer);
        }

        // needed to enable quicktips (qtitle for the heading and qtip for the
        // content) that are setup in the change GridCellRenderer
        new QuickTip(problemListGrid);

        addTool(createAddButton());
        _deleteButton = createDelButton();
        addTool(_deleteButton);

        if (CmCore.isDebug() == true) {

            final ToggleButton tb = new ToggleButton("Run Test");
            tb.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    if (tb.getValue()) {
                        runTests();
                    }
                    else {
                        stopTests();
                    }
                }
            });
            addTool(tb);
        }
        _upButton = new TextButton("Up", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                moveSelectProblem(-1);
            }
        });
        _upButton.setToolTip("Move problem selection up");
        addTool(_upButton);

        _downButton = new TextButton("Down", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                moveSelectProblem(1);
            }
        });
        addTool(_downButton);
        _downButton.setToolTip("Move problem selection down");

        _ccssButton = new TextButton("CCSS", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                ProblemDtoLocal selected = problemListGrid.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    CmMessageBox.showAlert("Please select a problem first");
                    return;
                }
                ProblemDto problem = selected.getProblem();
                
                // lesson not available ...
                /** 
                 *  TODO: get the base lesson model for this problem
                 */

                getProblemLessonsFromServerThenShowWindow(problem);
            }
        });
        addTool(_ccssButton);
        _ccssButton.setToolTip("Display CCSS coverage for selected problem.");

        if (store.size() == 0) {
            activateButtons(false);
        }
    }

    protected void getProblemLessonsFromServerThenShowWindow(final ProblemDto problem) {
    	   new RetryAction<CmList<PrescriptionSessionResponse>>() {
               @Override
               public void attempt() {
                   GetCorrelatedTopicsPrescriptionAction action = new GetCorrelatedTopicsPrescriptionAction(problem.getPid());
                   setAction(action);
                   CmRpcCore.getCmService().execute(action, this);
               }

               public void oncapture(CmList<PrescriptionSessionResponse> prescriptions) {
            	   
            	   /** just use first lesson for ... 
            	    * 
            	    * TODO: use all lessons assigned to this problem 
            	    * 
            	    */
            	   LessonModel lessonModel = null;
        		   List<LessonModel> list = new ArrayList<LessonModel>();
            	   if(prescriptions.size() > 0) {
            		   for (PrescriptionSessionResponse prescription : prescriptions) {
                		   String file = prescription.getPrescriptionData().getCurrSession().getFile();
                		   String title = prescription.getPrescriptionData().getCurrSession().getTopic();
                		   lessonModel = new LessonModel(title, file);
                		   list.add(lessonModel);
            		   }
            	   }
            	   else {
            		   lessonModel = new LessonModel();
            		   list.add(lessonModel);
            	   }
            	   new CCSSCoverageForLessonsWindow(list, _assignment.getAdminId(), problem.getName());           
               }
           }.register();
           
           
    							
	}

	Tester _tester;

    protected void stopTests() {
        if (_tester != null) {
            _tester.stopTests();
            _tester = null;
        }
    }

    protected void runTests() {
        final List<ProblemDtoLocal> probs = problemListGrid.getStore().getAll();
        _tester = new Tester(probs, this);
        _tester.startTests();
    }

    class Tester {

        private List<ProblemDtoLocal> probs;
        int count;
        private AssignmentProblemListView view;
        private Timer _timer;

        public Tester(List<ProblemDtoLocal> probs, AssignmentProblemListView view) {
            this.probs = probs;
            this.count = 0;
            this.view = view;
        }

        public void startTests() {
            _timer = new Timer() {
                @Override
                public void run() {
                    
                    ProblemDtoLocal sel = view.problemListGrid.getSelectionModel().getSelectedItem();
                    count=0;
                    if(sel != null) {
                        for(int d=0;d<probs.size();d++) {
                            ProblemDtoLocal dl = probs.get(d);
                            if(dl.equals(sel)) {
                                count=d;
                                break;
                            }
                        }
                        
                        if(count < probs.size()-1) {
                            count++;
                        }
                        else {
                            count=0;
                        }
                    }
                    ProblemDtoLocal item = probs.get(count);
                    view.problemListGrid.getSelectionModel().select(item, false);
                    view.problemListGrid.getView().focusRow(count);
                    _timer.schedule(6000);
                }
            };
            _timer.schedule(0);
        }

        public void stopTests() {
            _timer.cancel();
            _timer = null;
        }

    }

    protected void showUserProblemStats() {
        _selectedRecord = problemListGrid.getSelectionModel().getSelectedItem();
        if (_selectedRecord == null) {
            CmMessageBox.showAlert("Please select a problem first");
            return;
        }
        new AssignmentProblemStatsDialog(AssignmentProblemListView.this._assignment.getAssignKey(), _selectedRecord.getPid(), _selectedRecord.getLabel(),
                new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        showSelectedProblemHtml(callback);
                    }
                });
    }

    private void startCheckingRealTimeStats() {
        GetAssignmentRealTimeStatsAction action = new GetAssignmentRealTimeStatsAction(this._assignment.getAssignKey());
        CmRpcCore.getCmService().execute(action, new AsyncCallback<AssignmentRealTimeStats>() {
            @Override
            public void onSuccess(AssignmentRealTimeStats result) {
                updateRealTimeStats(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error: " + caught);
            }
        });

    }

    protected void updateRealTimeStats(AssignmentRealTimeStats pidStats) {
        ordinalNumberValueProvider.doIncreaseOnEachOrdinal(false);

        List<ProblemDtoLocal> storePids = problemListGrid.getStore().getAll();

        ordinalNumberValueProvider.resetOrdinalNumber();
        for (ProblemDtoLocal pl : storePids) {
            // look up stats
            for (PidStats ps : pidStats.getPidStats()) {
                if (ps.getPid().equals(pl.getPid())) {
                    pl.setPidStats(ps);
                    problemListGrid.getStore().update(pl);
                    break;
                }
            }
        }

    }

    protected void moveSelectProblem(int i) {
        ProblemDtoLocal selected = problemListGrid.getSelectionModel().getSelectedItem();
        if (selected == null) {
            CmMessageBox.showAlert("No selected problem");
            return;
        }

        List<ProblemDtoLocal> curr = problemListGrid.getStore().getAll();
        int which = 0;
        for (ProblemDtoLocal p : curr) {
            if (p.getPid().equals(selected.getPid())) {
                break;
            }
            which++;
        }

        if (which + i < 0 || which + i > curr.size() - 1) {
            return;
        }

        // make editable copy
        List<ProblemDtoLocal> newList = new ArrayList<ProblemDtoLocal>();
        newList.addAll(curr);

        newList.remove(selected);
        newList.add(which + i, selected);

        problemListGrid.getStore().clear();
        ordinalNumberValueProvider.resetOrdinalNumber();
        problemListGrid.getStore().addAll(newList);

        problemListGrid.getSelectionModel().select(selected, false);
        _isModified = true;
    }

    private void activateButtons(boolean b) {
        _deleteButton.setEnabled(b);
        _upButton.setEnabled(b);
        _downButton.setEnabled(b);
    }

    public void setProblemListing(CmList<ProblemDto> cmList) {
        setWidget(problemListContainer);
        ordinalNumberValueProvider.resetOrdinalNumber();
        problemListGrid.getStore().clear();

        problemListGrid.getStore().addAll(createActiveList(cmList));

        activateButtons(cmList.size() > 0);
        forceLayout();

        if (!_assignment.getStatus().equals("Draft")) {
            startCheckingRealTimeStats();
        }
    }

    private Widget createDefaultNoProblemsMessge() {
        CenterLayoutContainer cc = new CenterLayoutContainer();
        cc.add(new HTML("<h1>Press Add to begin selecting problem.</h1>"));

        return cc;
    }

    public void setNoProblemsMessge() {
        setWidget(createDefaultNoProblemsMessge());

        forceLayout();
    }

    public boolean getIsModified() {
		return _isModified;
	}

	public void setIsModified(boolean _isModified) {
		this._isModified = _isModified;
	}

	private TextButton createDelButton() {

        TextButton btn = new TextButton("Delete");
        btn.setToolTip("Remove selected problem(s) from Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if (CmCore.isDebug() == false && !callback.isDraftMode()) {
                    CmMessageBox.showAlert("Assignments can only be edited in draft mode.");
                    return;
                }

                List<ProblemDtoLocal> newList = new ArrayList<ProblemDtoLocal>();

                ProblemDtoLocal nextSelect = null;
                for (int j = 0, jt = problemListGrid.getStore().getAll().size(); j < jt; j++) {
                    ProblemDtoLocal pd = (ProblemDtoLocal) problemListGrid.getStore().getAll().get(j);

                    boolean found = false;
                    for (int i = 0, t = problemListGrid.getSelectionModel().getSelectedItems().size(); i < t; i++) {
                        ProblemDtoLocal pto = (ProblemDtoLocal) problemListGrid.getSelectionModel().getSelectedItems().get(i);
                        if (pto == pd) {
                            // remove it
                            found = true;
                            _isModified = true;

                            int tot = problemListGrid.getStore().getAll().size();
                            if (j < tot - 1) {
                                nextSelect = (ProblemDtoLocal) problemListGrid.getStore().get(j + 1);
                            }
                            else if (j > 1) {
                                nextSelect = (ProblemDtoLocal) problemListGrid.getStore().get(j - 1);
                            }
                            else if (j == 1) {
                                nextSelect = (ProblemDtoLocal) problemListGrid.getStore().get(0);
                            }
                            break;
                        }
                    }
                    if (!found) {
                        newList.add(pd);
                    }
                }
                ordinalNumberValueProvider.resetOrdinalNumber();
                problemListGrid.getStore().clear();
                problemListGrid.getStore().addAll(newList);

                if (nextSelect != null) {
                    newList.clear();
                    newList.add(nextSelect);
                    problemListGrid.getSelectionModel().setSelection(newList);
                }
            }
        });

        return btn;
    }

    private Widget createAddButton() {

        TextButton btn = new TextButton("Add");
        btn.setToolTip("Add one or more problems to Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                if (CmCore.isDebug() == false && !callback.isDraftMode()) {
                    CmMessageBox.showAlert("Assignments can only be edited in draft mode.");
                    return;
                }

                AddProblemDialog.showDialog(new AddProblemDialog.AddProblemsCallback() {
                    @Override
                    public void problemsAdded(List<ProblemDto> problemsAdded) {
                        addProblemsToAssignment(createActiveList(problemsAdded));
                    }

                });
            }
        });

        return btn;
    }

    private List<ProblemDtoLocal> createActiveList(List<ProblemDto> problemsAdded) {
        List<ProblemDtoLocal> la = new ArrayList<ProblemDtoLocal>();
        int ordinal = 0;
        for (ProblemDto p : problemsAdded) {
            la.add(new ProblemDtoLocal(_assignment, p, ++ordinal));
        }
        return la;
    }

    private void addProblemsToAssignment(final List<ProblemDtoLocal> problemsAdded) {

        /**
         * make sure no duplicates
         * 
         */
        List<ProblemDtoLocal> currList = problemListGrid.getStore().getAll();
        for (ProblemDtoLocal pd : problemsAdded) {

            boolean found = false;
            for (ProblemDtoLocal p : currList) {
                if (p.getPid().equals(pd.getPid())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                problemListGrid.getStore().add(pd);
            }
            else {
                CmMessageBox.showAlert("Problem '" + pd.getLabel() + "' is already in assignment");
            }
        }

        if (problemListGrid.getStore().size() == 0) {
            setWidget(createDefaultNoProblemsMessge());
            activateButtons(false);
        }
        else {
            setWidget(problemListContainer);
            _isModified = true;
            activateButtons(true);
        }
        forceLayout();
    }

    private void showSelectedProblemHtml(Callback callbackOnComplete) {
        ProblemDtoLocal data = problemListGrid.getSelectionModel().getSelectedItem();
        if (data != null) {
            callbackOnComplete.problemHasBeenSelected(data.getProblem());
        }
    }

    ValueProvider<ProblemDto, String> valueProvider = new ValueProvider<ProblemDto, String>() {

        @Override
        public String getValue(ProblemDto object) {
            return object.getName();
        }

        @Override
        public void setValue(ProblemDto object, String value) {
            object.setName(value);
        }

        @Override
        public String getPath() {
            return "/";
        }

    };

    public List<ProblemDto> getAssignmentPids() {
        List<ProblemDto> problems = new ArrayList<ProblemDto>();
        for (ProblemDtoLocal a : problemListGrid.getStore().getAll()) {
            problems.add(a.getProblem());
        }
        return problems;
    }

    public interface AssignmentProblemListPanelProperties extends PropertyAccess<String> {
        @Path("pid")
        ModelKeyProvider<ProblemDtoLocal> id();

        ValueProvider<ProblemDtoLocal, String> percentCorrect();

        ValueProvider<ProblemDtoLocal, Integer> ordinal();

        ValueProvider<ProblemDtoLocal, String> labelWithType();

        ValueProvider<ProblemDtoLocal, Integer> ordinalNumber();
    }

    class MyOrdinalProvider implements ValueProvider<ProblemDtoLocal, Integer> {

        int orderPosition = 0;
        boolean increase = true;

        public void doIncreaseOnEachOrdinal(boolean yesNo) {
            increase = yesNo;
        }

        public void resetOrdinalNumber() {
            orderPosition = 0;
        }

        @Override
        public Integer getValue(ProblemDtoLocal object) {
            if (this.increase) {
                ++orderPosition;
            }
            return orderPosition;
        }

        @Override
        public void setValue(ProblemDtoLocal object, Integer value) {
            object.setOrdinalNumber(value);
        }

        @Override
        public String getPath() {
            return "/";
        }
    };

    /**
     * Composite of ProblemDto and real time active info
     * 
     * @author casey
     * 
     */
    class ProblemDtoLocal {

        ProblemDto problem;
        int ordinal;
        PidStats pidStats;
        private Assignment assignment;

        public ProblemDtoLocal(Assignment assignment, ProblemDto problem, int ordinal) {
            this.assignment = assignment;
            this.problem = problem;
            this.ordinal = ordinal;
        }

        public void setPidStats(PidStats ps) {
            this.pidStats = ps;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }

        public void setProblem(ProblemDto problem) {
            this.problem = problem;
        }

        public ProblemDto getProblem() {
            return this.problem;
        }

        public String getLabel() {
            return this.problem.getLabel();
        }

        public String getLabelWithType() {
            String labelWithType = problem.getLabelWithType();
            if (assignment.isPersonalized()) {
                if (problem.getProblemType() == ProblemType.MULTI_CHOICE) {
                    labelWithType += " *";
                }
            }
            return labelWithType;
        }

        public void setOrdinalNumber(Integer value) {
            problem.setOrdinalNumber(value);
        }

        public Integer getOrdinalNumber() {
            return problem.getOrdinalNumber();
        }

        public String getPid() {
            return problem.getPid();
        }

        public String getPercentCorrect() {
            if (pidStats == null) {
                return "--";
            }
            else {
                int percent = pidStats.getCorrectPercent();
                return (percent < 10 ? " " : "") + percent + "%";
            }
        }

    }
}
