package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentDesigner.Callback;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.AddAssignmentProblemsAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
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
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
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
    public AssignmentDesigner(Assignment assignment) {
        this._assignment = assignment;
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
        _mainContainer.setWestWidget(createProblemList());
        setupViewerGui();
        return _mainContainer;
    }
    
    
    private void setupViewerGui() {
        
        FlowLayoutContainer fc = new FlowLayoutContainer();
        fc.setScrollMode(ScrollMode.AUTO);
        fc.add(QuestionViewerPanel.getInstance());
        _mainContainer.setCenterWidget(fc);
        _mainContainer.forceLayout();
    }
    
    AssignmentProblemListView _listView;
    private Widget createProblemList() {
        _listView = new AssignmentProblemListView(_assignment, new Callback() {
            @Override
            public void problemHasBeenSelected(ProblemDto problem) {
                QuestionViewerPanel.getInstance().viewQuestion(problem);   
            }
        });
        BorderLayoutData data = new BorderLayoutData();
        data.setSize(300.0);
        _listView.setLayoutData(data);
        return _listView;
    }
    
    
    static interface Callback {
        void problemHasBeenSelected(ProblemDto problem);
    }
    
    public List<ProblemDto> getAssignmentPids() {
        List<ProblemDto> data = _listView.getGrid().getStore().getAll();
        return data;
    }
    
    
    private void readAssignmentProblems() {
        if(_assignment.getAssignKey() == 0) {
            return;
        }
        
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
                
                _listView.getGrid().getStore().clear();
                _listView.getGrid().getStore().addAll(assignment.getPids());
                
                
                /** select first problem, if any */
                if(false && assignment.getPids().size() > 0) {
                    List<ProblemDto> selectedNodes = new ArrayList<ProblemDto>();
                    selectedNodes.add(assignment.getPids().get(0));
                    _listView.getGrid().getSelectionModel().setSelection(selectedNodes);
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
    public AssignmentProblemListView(Assignment assignment, final Callback callbackOnComplete) {
        
        this.assignment = assignment;

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
        ColumnConfig<ProblemDto, String> nameCol = new ColumnConfig<ProblemDto, String>(v, 50, "Problem Number");
     
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
        root.setHeadingText("Assignment Problems");
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
                showSelectedProblemHtml(callbackOnComplete);                
            }
        });
     
        VerticalLayoutContainer problemListContainer = new VerticalLayoutContainer();
        root.setWidget(problemListContainer);
     
        problemListContainer.add(problemListGrid, new VerticalLayoutData(1, 1));
     
        // needed to enable quicktips (qtitle for the heading and qtip for the
        // content) that are setup in the change GridCellRenderer
        new QuickTip(problemListGrid);
        
        
        addTool(createAddButton());
        addTool(createDelButton());
    }
    
    public Grid getGrid() {
        return problemListGrid;
    }

    private Widget createDelButton() {
        
        TextButton btn = new TextButton("Del");
        btn.setToolTip("Remove selected problem(s) from Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
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
                AddProblemDialog.showDialog(new AddProblemDialog.Callback() {
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
