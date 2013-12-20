package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.DeleteCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class CustomProblemManager extends GWindow {
    
    private TeacherIdentity teacher;
    BorderLayoutContainer _main;
    Grid<CustomProblemModel> _grid;
    GridProperties _gridProps = GWT.create(GridProperties.class);
    private DefaultGxtLoadingPanel _emptyPage;

    public CustomProblemManager(TeacherIdentity teacher) {
        super(true);
        
        this.teacher = teacher;
        setHeadingText("Custom Problem Manager");
        setPixelSize(800, 600);
        
        buildGui();
        readFromServer();
        setVisible(true);
    }
    

    private void buildGui() {
        _main = new BorderLayoutContainer();
        ListStore<CustomProblemModel> store = new ListStore<CustomProblemModel>(_gridProps.key());
        List<ColumnConfig<CustomProblemModel, ?>> cols = new ArrayList<ColumnConfig<CustomProblemModel, ?>>();
        
        cols.add(new ColumnConfig<CustomProblemModel, String>(_gridProps.teacherName(), 100, "Teacher"));
        cols.add(new ColumnConfig<CustomProblemModel, Integer>(_gridProps.problemNumber(), 100, "Problem"));
        
        ColumnModel<CustomProblemModel> colModel = new ColumnModel<CustomProblemModel>(cols);
        _grid = new Grid<CustomProblemModel>(store,  colModel);
        _grid.getView().setAutoExpandColumn(cols.get(0));
        _grid.getView().setAutoFill(true);
        
        _grid.getSelectionModel().addSelectionHandler(new SelectionHandler<CustomProblemModel>() {
            @Override
            public void onSelection(SelectionEvent<CustomProblemModel> event) {
                showProblem(event.getSelectedItem());
            }
        });
        
        setWidget(_main);
        
        _emptyPage = new DefaultGxtLoadingPanel("Click on a problem or create a new one");
        
        _main.setCenterWidget(_emptyPage);
        
        ContentPanel gridPanel = new ContentPanel();
        gridPanel.setWidget(_grid);
        
        gridPanel.addTool(new TextButton("New",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                CustomProblemModel problem = new CustomProblemModel(null,0,teacher, null);
                new CustomProblemPropertyEditor(problem,new CallbackOnComplete() {
                    
                    @Override
                    public void isComplete() {
                        readFromServer();
                    }
                });
            }
        }));
        gridPanel.addTool(new TextButton("Edit"));
        gridPanel.addTool(new TextButton("Del", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                deleteSelectedProblem();
            }
        }));
        gridPanel.addTool(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                readFromServer();
            }
        }));
        
        _main.setWestWidget(gridPanel, new BorderLayoutData(300));
    }
    
    

    protected void deleteSelectedProblem() {
        final CustomProblemModel problem = _grid.getSelectionModel().getSelectedItem();
        if(problem == null) {
            CmMessageBox.showAlert("Select a problem to delete");
            return;
        }
        
        CmMessageBox.confirm("Delete Problem",  "Are you sure you want to delete this problem?", new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if(yesNo) {
                    doDeleteProblem(problem);
                }
            }
        });
    }



    protected void doDeleteProblem(final CustomProblemModel problem) {
        new RetryAction<RpcData>() {

            @Override
            public void attempt() {
                DeleteCustomProblemAction action = new DeleteCustomProblemAction(problem);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(RpcData data) {
                readFromServer();
            }
        }.register();        
    }



    protected void showProblem(CustomProblemModel selectedItem) {
        
        if(selectedItem == null) {
            _main.setCenterWidget(_emptyPage);
            forceLayout();
            return;
        }
        else {
            ProblemDesigner problemDesigner = new ProblemDesigner();
            _main.setCenterWidget(problemDesigner);
            forceLayout();
            problemDesigner.loadProblem(selectedItem.getPid());
        }
    }



    private void readFromServer() {
        
        showProblem(null);
        
        new RetryAction<CmList<CustomProblemModel>>() {

            @Override
            public void attempt() {
                GetCustomProblemAction action = new GetCustomProblemAction(teacher);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(CmList<CustomProblemModel> problems) {
                _grid.getStore().clear();
                _grid.getStore().addAll(problems);
                
            }
        }.register();
    }
    
    public static void startTest() {
        new CustomProblemManager(new TeacherIdentity(2, null    ));
    }

}

interface GridProperties extends PropertyAccess<String> {

    @Path("pid")
    ModelKeyProvider<CustomProblemModel> key();

    ValueProvider<CustomProblemModel, Integer> problemNumber();

    @Path("teacher.teacherName")
    ValueProvider<CustomProblemModel, String> teacherName();

    ValueProvider<CustomProblemModel, String> pid();
}