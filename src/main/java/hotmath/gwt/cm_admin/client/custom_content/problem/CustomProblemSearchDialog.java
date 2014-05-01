package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

public class CustomProblemSearchDialog extends GWindow {
    
    static private CustomProblemSearchDialog __instance;
    static public CustomProblemSearchDialog getInstance(List<LessonModel> lessonModels, Callback callback) {
        if(__instance == null) {
            __instance = new CustomProblemSearchDialog();
        }
        __instance.setCallback(lessonModels, callback);
        __instance.toFront();
        return __instance;
    }
    
    
    interface Callback {
        void selectionChanged(List<? extends LessonModel> models, String comments);
    }
    
    Callback _callback;

    protected CmList<LessonModel> _lessons;

    private List<LessonModel> _lessonModels;
    private void setCallback(List<LessonModel> lessonModels, Callback callback) {
        this._lessonModels = lessonModels;
        this._callback = callback;
        
        List<LessonModel> selectedItems = _grid.getSelectionModel().getSelectedItems();
        
        _grid.getStore().clear();
        _grid.getStore().addAll(_lessonModels);
        
        if(selectedItems.size() > 0) {
            _grid.getSelectionModel().setSelection(selectedItems);
        }
    }
    
    private CustomProblemSearchDialog() {
        super(false);
        
        setHeadingText("Custom Problem Filter");
        setPixelSize(400, 400);
        setCollapsible(true);
        
        setModal(true);
        setAutoHide(true);
        
        buildUi();
        
        
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
                _callback.selectionChanged(null, null);
                hide();
            }
        }));
        setVisible(true);
    }

//    private void __getDataFromServer(List<LessonModel> lessonModels) {
//        
//        new RetryAction<CmList<LessonModel>>() {
//
//            @Override
//            public void attempt() {
//                CmBusyManager.setBusy(true);
//                GetLessonsLinkToAction action = new GetLessonsLinkToAction(UserInfoBase.getInstance().getUid());
//                setAction(action);
//                CmShared.getCmService().execute(action, this);
//            }
//
//            @Override
//            public void oncapture(CmList<LessonModel> lessons) {
//                _lessons = lessons;
//                CmBusyManager.setBusy(false);
//                _grid.getStore().clear();
//                
//                _grid.getStore().addAll(lessons);
//            }
//        }.register();
//    }

    interface GridProps extends PropertyAccess<String> {
        ModelKeyProvider<LessonModel> lessonFile();
        ValueProvider<LessonModel, String> lessonName();
        ValueProvider<LessonModel, Integer> problemCount();
    }
    GridProps props = GWT.create(GridProps.class);
    
    Grid<LessonModel> _grid;
    TextField comments = new TextField();
    private void buildUi() {
        BorderLayoutContainer bCont = new BorderLayoutContainer();
        bCont.setNorthWidget(new HTML("<p style='margin: 10px'>Select the topics you would like to see problems for.</p>"), new BorderLayoutData(40));
        
        comments.setToolTip("Filter on comment text");
        comments.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				_callback.selectionChanged(_grid.getSelectionModel().getSelectedItems(), comments.getCurrentValue());
			}
		});
        
        SimplePanel commentPanel = new SimplePanel();
        commentPanel.getElement().setAttribute("style",  "padding: 15px");
        commentPanel.setWidget(new MyFieldLabel(comments,  "Comments", 80, 200));
        
        bCont.setSouthWidget(commentPanel,new BorderLayoutData(60));
        
        ListStore<LessonModel> store = new ListStore<LessonModel>(props.lessonFile());
        List<ColumnConfig<LessonModel, ?>> list = new ArrayList<ColumnConfig<LessonModel, ?>>();
        list.add(new ColumnConfig<LessonModel, String>(props.lessonName(), 200, "Topic Name"));
        // list.add(new ColumnConfig<LessonModel, Integer>(props.problemCount(), 50, "Count"));
        ColumnModel<LessonModel> cm = new ColumnModel<LessonModel>(list);
        _grid = new Grid<LessonModel>(store, cm);
        _grid.getView().setAutoFill(true);
        
        _grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<LessonModel>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<LessonModel> event) {
                _callback.selectionChanged(_grid.getSelectionModel().getSelectedItems(),comments.getCurrentValue());
            }
        });
        bCont.setCenterWidget(_grid);
        
        setWidget(bCont);
    }

}
