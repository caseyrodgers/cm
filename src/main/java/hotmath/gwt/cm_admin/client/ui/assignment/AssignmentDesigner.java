package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentDesigner.Callback;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.AddAssignmentProblemsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.assignment.StudentProblemGridCell;
import hotmath.gwt.cm_tools.client.ui.assignment.StudentProblemGridCell.ProblemGridCellCallback;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

/** Provide ability to design/create/modify an Assignment
 * 
 * @author casey
 *
 */
public class AssignmentDesigner extends SimpleContainer {
    
    Assignment _assignment;
    
    public interface AssignmentDesignerCallback {
        boolean isDraftMode();
    }
    AssignmentDesignerCallback _callBack;
    
    public AssignmentDesigner(Assignment assignment, AssignmentDesignerCallback callBack) {
        this._assignment = assignment;
        this._callBack = callBack;
        setWidget(createUi());
        
        
        readAssignmentProblems();
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_QUESTION_VIEWER_CLOSED) {
                    setupViewerGui();
                }
            }
        });
    }

    
    BorderLayoutContainer _mainContainer;
    private Widget createUi() {
        _mainContainer = new BorderLayoutContainer();
        BorderLayoutData ld = new BorderLayoutData();
        ld.setSize(.5d);
        ld.setCollapsible(true);
        ld.setSplit(true);
        _mainContainer.setWestWidget(createProblemList(), ld);
        setupViewerGui();
        return _mainContainer;
    }
    
    
    private void setupViewerGui() {
        
        BorderLayoutData bd = new BorderLayoutData();
        bd.setSplit(true);
        _mainContainer.setCenterWidget(QuestionViewerPanel.getInstance(), bd);
        _mainContainer.forceLayout();
    }
    
    AssignmentProblemListView _listView;
    private Widget createProblemList() {
        _listView = new AssignmentProblemListView(_assignment, new Callback() {
            @Override
            public void problemHasBeenSelected(ProblemDto problem) {
                QuestionViewerPanel.getInstance().viewQuestion(problem, false);   
            }
            
            @Override
            public boolean isDraftMode() {
                return _callBack.isDraftMode();
            }
            
            @Override
            public void clearTutorView() {
                QuestionViewerPanel.getInstance().removeQuestion();
            }
        });
        BorderLayoutData data = new BorderLayoutData();
        data.setSize(300.0);
        _listView.setLayoutData(data);
        return _listView;
    }
    
    
    static interface Callback {
        void problemHasBeenSelected(ProblemDto problem);
        boolean isDraftMode();
        void clearTutorView();
    }
    
    public List<ProblemDto> getAssignmentPids() {
        List<ProblemDto> data = _listView.getGrid().getStore().getAll();
        return data;
    }
    
    
    private void readAssignmentProblems() {
        if(_assignment.getAssignKey() == 0) {
            return;
        }
        
        
        QuestionViewerPanel.getInstance().removeQuestion();
        
        new RetryAction<Assignment>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                int aid = UserInfoBase.getInstance().getUid();
                GetAssignmentAction action = new GetAssignmentAction(_assignment.getAssignKey());// aid, _assignment);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(Assignment assignment) {
                CmBusyManager.setBusy(false);
                
                
                if(assignment.getPids().size() == 0) {
                    _listView.setNoProblemsMessge();    
                }
                else {
                    _listView.setProblemListing();
                    _listView.getGrid().getStore().clear();
                    _listView.getGrid().getStore().addAll(assignment.getPids());
                }
                
                
                /** select first problem, if any */
                if(false && assignment.getPids().size() > 0) {
                    _listView.getGrid().getSelectionModel().select(assignment.getPids().get(0), false);
                }
            }
            
            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);
                super.onFailure(error);
            }

        }.register();
    }
    
    
    
}



class AssignmentProblemListView extends ContentPanel {
    
    final Grid<ProblemDto> problemListGrid;
    Assignment assignment;
    Callback callback;
    
    VerticalLayoutContainer problemListContainer;
    
    public AssignmentProblemListView(Assignment assignment, final Callback callback) {
        
        this.assignment = assignment;
        this.callback = callback;

        ValueProvider<ProblemDto, String> v = new ValueProvider<ProblemDto, String>() {

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
        ColumnConfig<ProblemDto, String> nameCol = new ColumnConfig<ProblemDto, String>(v, 50, "Problems Assigned");
        nameCol.setCell(new StudentProblemGridCell(new ProblemGridCellCallback() {
            @Override
            public ProblemDto getProblem(int which) {
                return problemListGrid.getStore().get(which);
            }
        }));
     
        List<ColumnConfig<ProblemDto, ?>> l = new ArrayList<ColumnConfig<ProblemDto, ?>>();
        l.add(nameCol);
        ColumnModel<ProblemDto> cm = new ColumnModel<ProblemDto>(l);
     
        
        ModelKeyProvider<ProblemDto> kp = new ModelKeyProvider<ProblemDto>() {
            @Override
            public String getKey(ProblemDto item) {
                return item.getPid();
            }
        };
        

        
        ListStore<ProblemDto> store = new ListStore<ProblemDto>(kp);

        if(assignment.getPids() != null) {
            for(ProblemDto prob: assignment.getPids()) {
                store.add(prob);    
            }
        }
        
        ContentPanel root = this;
        root.setHeadingText("Assigned Problems");
        // root.getHeader().setIcon(ExampleImages.INSTANCE.table());
        root.addStyleName("margin-10");
         
        problemListGrid = new Grid<ProblemDto>(store, cm);
        problemListGrid.getView().setAutoExpandColumn(nameCol);
        problemListGrid.getView().setStripeRows(true);
        problemListGrid.getView().setColumnLines(true);
        problemListGrid.setBorders(false);
        
        problemListGrid.setColumnReordering(true);
        
        problemListGrid.getSelectionModel().addSelectionHandler(new SelectionHandler<ProblemDto>() {
            @Override
            public void onSelection(SelectionEvent<ProblemDto> event) {
                showSelectedProblemHtml(callback);                
            }
        });
     
        problemListContainer = new VerticalLayoutContainer();
        
        problemListContainer.add(problemListGrid, new VerticalLayoutData(1, 1));
        
        if(store.size() == 0) {
            root.setWidget(createDefaultNoProblemsMessge());
        }
        else {
            root.setWidget(problemListContainer);
        }
     
        // needed to enable quicktips (qtitle for the heading and qtip for the
        // content) that are setup in the change GridCellRenderer
        new QuickTip(problemListGrid);
        
        
        addTool(createAddButton());
        addTool(createDelButton());
    }
    
    public void setProblemListing() {
        setWidget(problemListContainer);
        forceLayout();
    }

    private Widget createDefaultNoProblemsMessge() {
        CenterLayoutContainer cc = new CenterLayoutContainer();
        cc.add(new HTML("<h1>No problems assigned</h1>"));
        
        return cc;
    }
    
    public void setNoProblemsMessge() {
        setWidget(createDefaultNoProblemsMessge());
        forceLayout();
    }

    public Grid getGrid() {
        return problemListGrid;
    }

    private Widget createDelButton() {
        
        TextButton btn = new TextButton("Delete");
        btn.setToolTip("Remove selected problem(s) from Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(CmShared.getQueryParameter("debug") == null && !callback.isDraftMode()) {
                    CmMessageBox.showAlert("Assignments can only be edited in draft mode.");
                    return;
                }
                
                callback.clearTutorView();
                
                List<ProblemDto> newList = new ArrayList<ProblemDto>();
                
                
                
                ProblemDto nextSelect=null;
                for(int j=0, jt=getGrid().getStore().getAll().size();j<jt;j++) {
                    ProblemDto pd = (ProblemDto)getGrid().getStore().getAll().get(j);
                
                    boolean found=false;
                    for(int i=0, t=getGrid().getSelectionModel().getSelectedItems().size();i<t;i++) {
                        ProblemDto pto = (ProblemDto)getGrid().getSelectionModel().getSelectedItems().get(i);
                        if(pto == pd) {
                            // remove it
                            found=true;
                            
                            int tot=getGrid().getStore().getAll().size();
                            if(j<tot-1) {
                                nextSelect = (ProblemDto)getGrid().getStore().get(j+1);
                            }
                            else if(j>1){
                                nextSelect = (ProblemDto)getGrid().getStore().get(j-1);
                            }
                            else if(j==1){
                                nextSelect = (ProblemDto)getGrid().getStore().get(0);
                            } 
                            break;
                        }
                    }
                    if(!found) {
                        newList.add(pd);
                    }
                }
                
                getGrid().getStore().clear();
                getGrid().getStore().addAll(newList);
                
                
                if(nextSelect != null) {
                    newList.clear();
                    newList.add(nextSelect);
                    getGrid().getSelectionModel().setSelection(newList);
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
                
                if(CmShared.getQueryParameter("debug") == null && callback.isDraftMode()) {
                    CmMessageBox.showAlert("Assignments can only be edited in draft mode.");
                    return;
                }
                
                AddProblemDialog.showDialog(new AddProblemDialog.AddProblemsCallback() {
                    @Override
                    public void problemsAdded(List<ProblemDto> problemsAdded) {
                        addProblemsToAssignment(problemsAdded);
                    }

                });
            }
        });

        return btn;
    }
    

    private void addProblemsToAssignment(final List<ProblemDto> problemsAdded) {
        if(problemsAdded.size() == 0) {
            setWidget(createDefaultNoProblemsMessge());
        }
        else {
            setWidget(problemListContainer);
        }
        forceLayout();
        problemListGrid.getStore().addAll(problemsAdded);
        if(true) {
            return;
        }
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                int assignKey = 1;
                AddAssignmentProblemsAction action = new AddAssignmentProblemsAction(assignKey, problemsAdded);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                Info.display("Problems Added", problemsAdded.size() + " problem(s) have been added.");
            }
        }.register();
    }
    
    private void showSelectedProblemHtml(Callback callbackOnComplete) {
        ProblemDto data = problemListGrid.getSelectionModel().getSelectedItem();
        if(data != null) {
            callbackOnComplete.problemHasBeenSelected(data);
        }
    }

    
}
