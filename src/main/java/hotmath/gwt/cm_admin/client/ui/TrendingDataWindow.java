package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.model.BaseModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

/** Provide a window to display trending data
 * 
 * @author casey
 *
 */
public class TrendingDataWindow extends CmWindow {
    Integer adminId;
    Grid<TrendingDataExt> _grid;

    public TrendingDataWindow(Integer adminId) {
        this.adminId = adminId;
        setHeading("Top Five Assigned Lessons");
        setWidth(410);
        setHeight(300);

        final ListStore<TrendingDataExt> store = new ListStore<TrendingDataExt>();
        _grid = defineGrid(store, defineColumns());
        
        setLayout(new FillLayout());
        add(_grid);
        loadTrendDataAsync();
        
        
        addButton(new Button("Close",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));
        
        setVisible(true);
    }

    private void loadTrendDataAsync() {
        CmServiceAsync service = (CmServiceAsync) Registry.get("cmService");
        service.execute(new GetAdminTrendingDataAction(adminId), new CmAsyncCallback<CmAdminTrendingDataI>() {
            public void onSuccess(CmAdminTrendingDataI arg0) {
                List<TrendingDataExt> ltde = new ArrayList<TrendingDataExt>();
                for(int i=0,t=arg0.getTrendingData().size();i<t;i++) {
                    ltde.add(new TrendingDataExt(arg0.getTrendingData().get(i)));
                }
                _grid.getStore().add(ltde);
               layout();
            }
        });
    }
    
    private Grid<TrendingDataExt> defineGrid(final ListStore<TrendingDataExt> store, ColumnModel cm) {
        final Grid<TrendingDataExt> grid = new Grid<TrendingDataExt>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.setWidth("410px");
        grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);
        return grid;
    }    
    
    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId(TrendingDataExt.LESSON_NAME_KEY);
        column.setHeader("Lesson Name");
        column.setWidth(235);
        column.setSortable(true);
        configs.add(column);

        ColumnConfig pass = new ColumnConfig();
        pass.setId(TrendingDataExt.LESSON_ASSIGN_KEY);
        pass.setHeader("Assigned Count");
        pass.setWidth(150);
        pass.setSortable(true);
        configs.add(pass);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
}


class TrendingDataExt extends BaseModel implements Serializable  {
    static final String LESSON_NAME_KEY = "lesson-name";
    static final String LESSON_ASSIGN_KEY = "lesson-assign-count";
    public TrendingDataExt(TrendingData td) {
        set(LESSON_NAME_KEY, td.getLessonName());
        set(LESSON_ASSIGN_KEY, td.getCountAssigned());
    }
}