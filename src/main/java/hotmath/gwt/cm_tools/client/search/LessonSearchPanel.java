package hotmath.gwt.cm_tools.client.search;

import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class LessonSearchPanel extends SimpleContainer {

    Grid<Topic> _gridOfLessons;
    private TextField _lessonText;
    protected CmList<Topic> _allLessons;
    TabPanel _tabPanel = new TabPanel();
    
    public LessonSearchPanel() {
        BorderLayoutContainer main = new BorderLayoutContainer();
        BorderLayoutData lData = new BorderLayoutData(50);
        
        FramedPanel frame = new FramedPanel();
        HorizontalPanel hPanel = new HorizontalPanel();
        frame.setWidget(hPanel);
        frame.setHeaderVisible(false);
        _lessonText = new TextField();
        hPanel.add(new FieldLabel(_lessonText, "Topic"));
        hPanel.add(new TextButton("Search", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                getLessonsFromServer();
            }
        }));
        main.setNorthWidget(frame, new BorderLayoutData(45));
        _gridOfLessons = createLessonGrid();

        _lessonText.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if(event.getNativeKeyCode() == 13) {
                    getLessonsFromServer();
                }
                //limitListTo(_lessonText.getText());
            }
        });
        
        main.setCenterWidget(_gridOfLessons);
        
        _tabPanel.add(main, new TabItemConfig("Topics",false));
        
        setWidget(_tabPanel);
    }

    protected void limitListTo(String text) {
        
        if(_gridOfLessons.getStore().size() == 0) {
            return;
        }
        
        List<Topic> listNew = null;
        if(text == null || text.length() == 0) {
            listNew = _allLessons;
        }
        else {
            listNew = new ArrayList<Topic>();
            String searchFor =  text.toLowerCase();
            for(Topic t: _allLessons) {
                if(t.getName().toLowerCase().contains(searchFor)) {
                    listNew.add(t);
                }
            }
        }
        _gridOfLessons.getStore().clear();
        _gridOfLessons.getStore().addAll(listNew);
    }

    private void getLessonsFromServer() {
        new RetryAction<CmList<Topic>>() {
            @Override
            public void attempt() {
                SearchTopicAction action = new SearchTopicAction(SearchType.LESSON_LIKE,"%" + _lessonText.getValue() + "%");
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            
            @Override
            public void oncapture(CmList<Topic> value) {
                _allLessons = value;
                _gridOfLessons.getStore().clear();
                _gridOfLessons.getStore().addAll(value);
            }
        }.register();
    }

    LessonGridProps _props = GWT.create(LessonGridProps.class);
    private Grid<Topic> createLessonGrid() {
        ListStore<Topic> store = new ListStore<Topic>(_props.key());
        ArrayList<ColumnConfig<Topic, ?>> list = new ArrayList<ColumnConfig<Topic, ?>>();
        
        list.add(new ColumnConfig<Topic, String>(_props.name(), 200, "Topic Name"));
        
        ColumnModel<Topic> cm = new ColumnModel<Topic>(list);
        Grid<Topic> grid = new Grid<Topic>(store,  cm);
        grid.getView().setAutoExpandColumn(list.get(0));
        grid.getView().setForceFit(true);
        
        
        grid.addRowDoubleClickHandler(new RowDoubleClickHandler() {
            @Override
            public void onRowDoubleClick(RowDoubleClickEvent event) {
                loadTopic(_gridOfLessons.getSelectionModel().getSelectedItem());
            }
        });
        
        return grid;
    }
    
    protected void loadTopic(final Topic topic) {
        Log.debug("Searching for topic: " + topic);
        new RetryAction<PrescriptionSessionResponse>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetTopicPrescriptionAction action = new  GetTopicPrescriptionAction(topic.getFile());
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            
            public void oncapture(PrescriptionSessionResponse value) {
                CmBusyManager.setBusy(false);
                
//                PrescriptionCmGuiDefinition prescriptionGui = new PrescriptionCmGuiDefinition();
//                CmMainPanel panel = new CmMainPanel(prescriptionGui);
//                prescriptionGui.setPrescriptionData(value,  0);
//                
//                _tabPanel.add(panel, new TabItemConfig(topic.getName(), true));
//                _tabPanel.setActiveWidget(_tabPanel.getWidget(_tabPanel.getWidgetCount()-1));
            }
        }.register();
    }

    interface LessonGridProps extends PropertyAccess<String> {
        @Path("file")
        ModelKeyProvider<Topic> key();

        ValueProvider<Topic, String> name();
    }
}
