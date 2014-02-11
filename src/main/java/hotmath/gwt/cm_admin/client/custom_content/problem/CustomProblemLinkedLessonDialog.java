package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.ui.WebLinkAddTargetsDialog;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetCustomProblemLinkedLessonAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemLinkedLessonAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class CustomProblemLinkedLessonDialog extends GWindow {

    private static CustomProblemLinkedLessonDialog __instance;

    public static Component getInstance(SolutionInfo solutionInfo) {
        __instance = null;
        if (__instance == null) {
            __instance = new CustomProblemLinkedLessonDialog();
        }
        __instance.setSolution(solutionInfo);
        return __instance;
    }

    SolutionInfo solutionInfo;

    public CustomProblemLinkedLessonDialog() {
        super(false);
        setHeadingText("Link Lesson to Problem");

        setPixelSize(400, 320);

        drawGui();
        addCloseButton();
    }

    BorderLayoutContainer _main;

    private void drawGui() {
        _main = new BorderLayoutContainer();
        _main.setNorthWidget(new HTML("<p style='padding: 10px;font-size: 1.2em'>Lessons that are linked to this problem.</p>"), new BorderLayoutData(
                40));
        _main.setCenterWidget(new DefaultGxtLoadingPanel());

        addTool(new TextButton("Add Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                WebLinkAddTargetsDialog.getSharedInstance(1, new WebLinkAddTargetsDialog.Callback() {
                    @Override
                    public void targetsAdded(List<LessonModel> lessons) {
                        setLessonsInGrid(false, lessons);
                        saveLessonsToServer();
                    }
                });
            }
        }));
        addTool(new TextButton("Remove Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                LessonModel lm = _grid.getSelectionModel().getSelectedItem();
                if(lm == null) {
                    CmMessageBox.showAlert("No lesson selected");
                    return;
                }
                
                _grid.getStore().remove(lm);
                saveLessonsToServer();
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
                GetCustomProblemLinkedLessonAction action = new GetCustomProblemLinkedLessonAction(solutionInfo.getPid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<LessonModel> value) {
                setLessonsInGrid(true, value);
            }
        }.register();
        
    }

    private void saveLessonsToServer() {

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SaveCustomProblemLinkedLessonAction action = new SaveCustomProblemLinkedLessonAction(UserInfoBase.getInstance().getUid(),
                        solutionInfo.getPid(), new CmArrayList<LessonModel>(_grid.getStore().getAll()));
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData value) {
                Log.info("Custom Problem linked to lessons: " + value);
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

        cols.add(new ColumnConfig<LessonModel, String>(props.lessonName(), 100, "Lesson Name"));

        ColumnModel<LessonModel> colModel = new ColumnModel<LessonModel>(cols);
        _grid = new Grid<LessonModel>(store, colModel);
        _grid.getView().setAutoFill(true);
        _grid.getView().setAutoExpandColumn(cols.get(0));
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        _main.setCenterWidget(_grid);
        forceLayout();
    }

    public void setSolution(SolutionInfo si) {
        this.solutionInfo = si;
        
        getDataFromServer();
    }
}
