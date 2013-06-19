package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentDesigner.Callback;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

public class AssignmentProblemListView extends ContentPanel {
    
    final Grid<ProblemDto> problemListGrid;
    Assignment assignment;
    Callback callback;
    
    VerticalLayoutContainer problemListContainer;
    private TextButton _deleteButton;

    MyOrdinalProvider ordinalNumberValudProvider;
    private TextButton _upButton;
    private TextButton _downButton;
    public AssignmentProblemListView(Assignment assignment, final Callback callback) {
        
        this.assignment = assignment;
        this.callback = callback;

        AssignmentProblemListPanelProperties props = GWT.create(AssignmentProblemListPanelProperties.class);
        
        ordinalNumberValudProvider = new MyOrdinalProvider();
        ColumnConfig<ProblemDto, Integer> problemNumber = new ColumnConfig<ProblemDto, Integer>(ordinalNumberValudProvider, 25, "");
        ColumnConfig<ProblemDto, String> nameCol = new ColumnConfig<ProblemDto, String>(props.labelWithType(), 50, "Problems Assigned");
        
        nameCol.setSortable(false);
     
        List<ColumnConfig<ProblemDto, ?>> cols = new ArrayList<ColumnConfig<ProblemDto, ?>>();
        cols.add(problemNumber);
        cols.add(nameCol);
        ColumnModel<ProblemDto> cm = new ColumnModel<ProblemDto>(cols);
        
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
        problemListGrid.setHideHeaders(true);
        
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
        _deleteButton = createDelButton();
        addTool(_deleteButton);
        _upButton = new TextButton("Up", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                moveSelectProblem(-1);
            }
        });
        _upButton.setToolTip("Move selected problem up");
        addTool(_upButton);

        _downButton = new TextButton("Down", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                moveSelectProblem(1);
            }
        });
        addTool(_downButton);
        _upButton.setToolTip("Move selected problem down");
        
        if(store.size()==0) {
            activateButtons(false);
        }
    }
    
    protected void moveSelectProblem(int i) {
        ProblemDto selected = problemListGrid.getSelectionModel().getSelectedItem();
        if(selected == null) {
            CmMessageBox.showAlert("No selected problem");
            return;
        }
        
        List<ProblemDto> curr = problemListGrid.getStore().getAll();
        int which=0;
        for(ProblemDto p: curr) {
            if(p.getPid().equals(selected.getPid())) {
                break;
            }
            which++;
        }
        
        
        if(which + i < 0 || which + i > curr.size()-1) {
            return;
        }
        
        // make editable copy
        List<ProblemDto> newList = new ArrayList<ProblemDto>();
        newList.addAll(curr);
        
        newList.remove(selected);
        newList.add(which + i, selected);
        
        problemListGrid.getStore().clear();
        ordinalNumberValudProvider.resetOrdinalNumber();
        problemListGrid.getStore().addAll(newList);
        
        problemListGrid.getSelectionModel().select(selected,false);
    }

    private void activateButtons(boolean b) {
        _deleteButton.setEnabled(b);
        _upButton.setEnabled(b);
        _downButton.setEnabled(b);
    }

    public void setProblemListing(CmList<ProblemDto> cmList) {
        setWidget(problemListContainer);
        ordinalNumberValudProvider.resetOrdinalNumber();
        problemListGrid.getStore().clear();
        problemListGrid.getStore().addAll(cmList);
        
        activateButtons(cmList.size()>0);
        forceLayout();
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

    private TextButton createDelButton() {
        
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
                for(int j=0, jt=problemListGrid.getStore().getAll().size();j<jt;j++) {
                    ProblemDto pd = (ProblemDto)problemListGrid.getStore().getAll().get(j);
                
                    boolean found=false;
                    for(int i=0, t=problemListGrid.getSelectionModel().getSelectedItems().size();i<t;i++) {
                        ProblemDto pto = (ProblemDto)problemListGrid.getSelectionModel().getSelectedItems().get(i);
                        if(pto == pd) {
                            // remove it
                            found=true;
                            
                            int tot=problemListGrid.getStore().getAll().size();
                            if(j<tot-1) {
                                nextSelect = (ProblemDto)problemListGrid.getStore().get(j+1);
                            }
                            else if(j>1){
                                nextSelect = (ProblemDto)problemListGrid.getStore().get(j-1);
                            }
                            else if(j==1){
                                nextSelect = (ProblemDto)problemListGrid.getStore().get(0);
                            } 
                            break;
                        }
                    }
                    if(!found) {
                        newList.add(pd);
                    }
                }
                ordinalNumberValudProvider.resetOrdinalNumber();
                problemListGrid.getStore().clear();
                problemListGrid.getStore().addAll(newList);
                
                
                if(nextSelect != null) {
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
                
                if(CmShared.getQueryParameter("debug") == null && !callback.isDraftMode()) {
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
        
        /** make sure no duplicates
         * 
         */
        List<ProblemDto> currList = problemListGrid.getStore().getAll();
        for(ProblemDto pd: problemsAdded) {
            
            boolean found=false;
            for(ProblemDto p: currList) {
                if(p.getPid().equals(pd.getPid())) {
                    found=true;
                    break;
                }
            }
            
            if(!found) {
                problemListGrid.getStore().add(pd);
            }
            else {
               CmMessageBox.showAlert("Problem '" + pd.getLabel() + "' is already in assignment");
            }
        }
        
        if(problemListGrid.getStore().size() == 0) {
            setWidget(createDefaultNoProblemsMessge());
            activateButtons(false);
        }
        else {
            setWidget(problemListContainer);
            activateButtons(true);
        }
        forceLayout();
    }
    
    private void showSelectedProblemHtml(Callback callbackOnComplete) {
        ProblemDto data = problemListGrid.getSelectionModel().getSelectedItem();
        if(data != null) {
            callbackOnComplete.problemHasBeenSelected(data);
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
        return problemListGrid.getStore().getAll();
    }
    
    public interface AssignmentProblemListPanelProperties extends PropertyAccess<String> {
        @Path("pid")
        ModelKeyProvider<ProblemDto> id();
        ValueProvider<ProblemDto, String> labelWithType();
        ValueProvider<ProblemDto, Integer> ordinalNumber();
    }

    
    class MyOrdinalProvider implements ValueProvider<ProblemDto, Integer> {

        int orderPosition=0;
        public void resetOrdinalNumber() {
            orderPosition = 0;
        }
        @Override
        public Integer getValue(ProblemDto object) {
            return ++orderPosition;
        }

        @Override
        public void setValue(ProblemDto object, Integer value) {
            object.setOrdinalNumber(value);
        }

        @Override
        public String getPath() {
            return "/";
        }
    };

    
}
