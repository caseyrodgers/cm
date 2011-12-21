package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionLessonChooserDialog extends CmWindow {

    private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

    interface AboutDialogUiBinder extends UiBinder<Widget, PrescriptionLessonChooserDialog> {
    }

    PrescriptionData pData;
    Grid<LessonChoice> _grid;
    Button _nextSegment;

    public PrescriptionLessonChooserDialog() {
        setSize("450px", "380px");
        setHeading("Choose Lesson");
        setResizable(false);
        setModal(true);
        setScrollMode(Scroll.AUTOY);
        addStyleName(PrescriptionLessonChooserDialog.class.getName());
        
        
        if(!UserInfo.getInstance().isCustomProgram()) {
            _nextSegment = new Button("Next Quiz", new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    CatchupMathTools.showAlert("Next Quiz");
                }
            });
            addButton(_nextSegment);
        }

        
        addButton(new Button("Load Lesson", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                loadSelectedLesson();
            }
        }));

        addCloseButton();
        
        
    }

    int scrollIntoView=0;
    public void showDialog(PrescriptionData pData) {

        if (this.pData != pData) {
            if (_grid != null) {
                remove(_grid);
            }

            
            String currentTopic = pData.getCurrSession().getTopic();
            ListStore<LessonChoice> store = new ListStore<LessonChoice>();
            boolean isSegmentComplete=true;
            for (SessionTopic st : pData.getSessionTopics()) {
                LessonChoice lc = new LessonChoice(st.getTopic(), st.isComplete(), st.getTopicStatus());
                store.add(lc);

                if (lc.getTopic().equals(currentTopic)) {
                    lc.setStyle("selected");
                    scrollIntoView = store.getCount()-1;
                }
                
                
                if(!lc.isComplete()) {
                    isSegmentComplete=false;
                }
            }
            _grid = defineGrid(store, defineColumns());
            add(_grid);
            
            
            
            /** This does not seem to work ... 
             * 
             */
            new Timer() {
                @Override
                public void run() {
                    _grid.getView().ensureVisible(scrollIntoView, 0, true);                }
            }.schedule(5000);
            
        }

        setVisible(true);
    }

    public void showCentered() {
        center();
    }

    Grid<LessonChoice> defineGrid(final ListStore<LessonChoice> store, ColumnModel cm) {

        final Grid<LessonChoice> grid = new Grid<LessonChoice>(store, cm);
        grid.setAutoExpandColumn("topic");
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        grid.addListener(Events.CellDoubleClick, new Listener<GridEvent<LessonChoice>>() {
            public void handleEvent(GridEvent<LessonChoice> be) {
                loadSelectedLesson();
            }
        });

        grid.setWidth("440px");
        grid.setHeight("300px");

        grid.getView().setViewConfig(new GVC());

        return grid;
    }

    private void loadSelectedLesson() {
        if (_grid.getSelectionModel().getSelectedItems().size() > 0) {
            LessonChoice lesson = _grid.getSelectionModel().getSelectedItem();
            boolean found = false;
            int sessionNumber = 0;
            for (LessonChoice item : _grid.getStore().getModels()) {
                if (item == lesson) {
                    found = true;
                    break;
                }
                sessionNumber++;
            }

            if (!found) {
                CatchupMathTools.showAlert("Lesson not found: " + lesson);
            } else {
                CmHistoryManager.getInstance().addHistoryLocation(
                        new CmLocation(LocationType.PRESCRIPTION, sessionNumber));

                hide();
            }
        }
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("topic");
        column.setHeader("Lesson");
        column.setWidth(160);
        column.setSortable(false);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("isComplete");
        column.setHeader("Completed");
        column.setWidth(80);
        column.setSortable(false);
        column.setColumnStyleName("is_complete");
        column.setRenderer(new GridCellRenderer<LessonChoice>() {
            @Override
            public Object render(LessonChoice model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<LessonChoice> store, Grid<LessonChoice> grid) {

                if (model.isComplete()) {
                    return "<img src='/gwt-resources/images/check_black.png'/>";
                } else {
                    return "";
                }
            }
        });
        configs.add(column);

        column = new ColumnConfig();
        column.setId("status");
        column.setHeader("Status");
        column.setWidth(120);
        column.setSortable(false);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    static PrescriptionLessonChooserDialog __instance;

    static public PrescriptionLessonChooserDialog getSharedInstance() {
        if (__instance == null) {
            __instance = new PrescriptionLessonChooserDialog();
        }
        return __instance;
    }

}

class GVC extends GridViewConfig {
    @Override
    public String getRowStyle(ModelData model, int rowIndex, ListStore<ModelData> ds) {
        if (model != null) {
            return model.get("style");
        }
        return "";
    }
}