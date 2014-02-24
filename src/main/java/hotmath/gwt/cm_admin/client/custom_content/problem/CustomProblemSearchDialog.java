package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.model.LessonLinkedModel;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetLessonsLinkToAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

public class CustomProblemSearchDialog extends GWindow {
    
    static private CustomProblemSearchDialog __instance;
    static public CustomProblemSearchDialog getInstance(Callback callback) {
        if(__instance == null) {
            __instance = new CustomProblemSearchDialog();
        }
        __instance.setCallback(callback);
        __instance.toFront();
        return __instance;
    }
    
    
    interface Callback {
        void selectionChanged(List<? extends LessonModel> models);
    }
    
    Callback _callback;

    protected CmList<LessonLinkedModel> _lessons;
    private void setCallback(Callback callback) {
        this._callback = callback;
    }
    
    private CustomProblemSearchDialog() {
        super(false);
        
        setHeadingText("Custom Problem Filter");
        setPixelSize(400, 400);
        setCollapsible(true);
        setModal(false);
        
        buildUi();
        
        getDataFromServer();
        
        addButton(new TextButton("Hide", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _grid.getSelectionModel().deselectAll();
                _callback.selectionChanged(null);
                hide();
            }
        }));
        setVisible(true);
    }

    private void getDataFromServer() {
        
        new RetryAction<CmList<LessonLinkedModel>>() {

            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetLessonsLinkToAction action = new GetLessonsLinkToAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<LessonLinkedModel> lessons) {
                _lessons = lessons;
                CmBusyManager.setBusy(false);
                _grid.getStore().clear();
                
                _grid.getStore().addAll(lessons);
            }
        }.register();
    }

    interface GridProps extends PropertyAccess<String> {
        ModelKeyProvider<LessonLinkedModel> lessonFile();
        ValueProvider<LessonLinkedModel, String> lessonName();
        ValueProvider<LessonLinkedModel, Integer> problemCount();
    }
    GridProps props = GWT.create(GridProps.class);
    
    Grid<LessonLinkedModel> _grid;
    private void buildUi() {
        BorderLayoutContainer bCont = new BorderLayoutContainer();
        bCont.setNorthWidget(new HTML("<p style='margin: 10px'>Select the topics you would like to see problems for.</p>"), new BorderLayoutData(40));
        
        
        ListStore<LessonLinkedModel> store = new ListStore<LessonLinkedModel>(props.lessonFile());
        List<ColumnConfig<LessonLinkedModel, ?>> list = new ArrayList<ColumnConfig<LessonLinkedModel, ?>>();
        list.add(new ColumnConfig<LessonLinkedModel, String>(props.lessonName(), 200, "Topic Name"));
        list.add(new ColumnConfig<LessonLinkedModel, Integer>(props.problemCount(), 50, "Count"));
        ColumnModel<LessonLinkedModel> cm = new ColumnModel<LessonLinkedModel>(list);
        _grid = new Grid<LessonLinkedModel>(store, cm);
        _grid.getView().setAutoFill(true);
        
        _grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<LessonLinkedModel>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<LessonLinkedModel> event) {
                _callback.selectionChanged(_grid.getSelectionModel().getSelectedItems());
            }
        });
        bCont.setCenterWidget(_grid);
        
        setWidget(bCont);
    }

}
