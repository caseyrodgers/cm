package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.ui.WebLinkAddTargetsDialog;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemLinkedLessonAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.Style.SelectionMode;
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

public class CustomProblemLinkedLessonsPanel extends ContentPanel {

    String _pid;
    public CustomProblemLinkedLessonsPanel() {
        drawGui();
    }

    BorderLayoutContainer _main;

    boolean _isDirty;
    
    private void drawGui() {
        _main = new BorderLayoutContainer();
        _main.setNorthWidget(new HTML("<b style='display: block;margin: 10px 5px'>List correlated lessons (optional, for later reference or filtering):</b>"), new BorderLayoutData(35));
        _main.setCenterWidget(new DefaultGxtLoadingPanel());


        setHeaderVisible(false);
        
        addButton(new TextButton("Add Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                WebLinkAddTargetsDialog.getSharedInstance(1, new WebLinkAddTargetsDialog.Callback() {
                    @Override
                    public void targetsAdded(List<LessonModel> lessons) {
                        setLessonsInGrid(false, lessons);
                        _isDirty=true;
                        // saveLessonsToServer();
                    }
                });
            }
        }));
        addButton(new TextButton("Remove Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                LessonModel lm = _grid.getSelectionModel().getSelectedItem();
                if(lm == null) {
                    CmMessageBox.showAlert("No lesson selected");
                    return;
                }
                
                _grid.getStore().remove(lm);
                _isDirty=true;
                // saveLessonsToServer();
            }
        }));

        drawGrid();

        setWidget(_main);
    }

    /** Add lessons to grid, and do not show duplicates */
    private void setLessonsInGrid(boolean clear, List<LessonModel> lessons) {
        if(clear) {
            _grid.getStore().clear();
        }
        
        List<LessonModel> storeLess = _grid.getStore().getAll();
        for(LessonModel lm: lessons) {
            boolean found=false;
            for(LessonModel slm: storeLess) {
                if(slm.getLessonFile().equals(lm.getLessonFile())) {
                    found=true;
                    break;
                }
            }
            if(!found) {
                _grid.getStore().add(lm);
            }
        }
    }

    private void getDataFromServer() {
        new RetryAction<CmList<LessonModel>>() {
            @Override
            public void attempt() {
                GetCustomProblemLinkedLessonAction action = new GetCustomProblemLinkedLessonAction(_pid);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<LessonModel> value) {
                setLessonsInGrid(true, value);
            }
        }.register();
        
    }

    interface LessonGridProps extends PropertyAccess<String> {
        @Path("lessonFile")
        ModelKeyProvider<LessonModel> key();

        ValueProvider<LessonModel, String> lessonName();
    }

    LessonGridProps props = GWT.create(LessonGridProps.class);

    Grid<LessonModel> _grid;

    private void drawGrid() {
        ListStore<LessonModel> store = new ListStore<LessonModel>(props.key());
        List<ColumnConfig<LessonModel, ?>> cols = new ArrayList<ColumnConfig<LessonModel, ?>>();

        cols.add(new ColumnConfig<LessonModel, String>(props.lessonName(), 100, ""));

        ColumnModel<LessonModel> colModel = new ColumnModel<LessonModel>(cols);
        _grid = new Grid<LessonModel>(store, colModel);
        _grid.getView().setAutoFill(true);
        _grid.getView().setAutoExpandColumn(cols.get(0));
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        _main.setCenterWidget(_grid);
        _main.forceLayout();
    }

    public void setSolution(String pid) {
        this._pid = pid;
        
        getDataFromServer();
    }
}
