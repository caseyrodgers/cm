package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramUsageAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.Label;

/*
 * Displays Student status in selected Parallel Program
 *
 * @author Bob
 * 
 */

public class ParallelProgramUsageWindow extends CmWindow {

    ParallelProgramModel ppModel;
    private Grid<ParallelProgramUsageModel> ppumGrid;
    private Label _studentCount;

    /**
     * Create Usage Window for Parallel Programs.
     * Displays student name, current Activity and most recent Result
     * for each Student.
     * 
     * @param ppModel
     */
    public ParallelProgramUsageWindow(final ParallelProgramModel ppModel) {
        addStyleName("parallel-program-usage-window");
        this.ppModel = ppModel;
        setSize(540, 315);
        setModal(true);
        setResizable(false);
        setHeading("Usage for: " + ppModel.getName());

        ListStore<ParallelProgramUsageModel> store = new ListStore<ParallelProgramUsageModel>();
        ColumnModel cm = defineColumns();

        ppumGrid = new Grid<ParallelProgramUsageModel>(store, cm);
        ppumGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ppumGrid.getSelectionModel().setFiresEvents(true);
        ppumGrid.setStripeRows(true);
        ppumGrid.setWidth(460);
        ppumGrid.setHeight(225);

        setLayout(new BorderLayout());
        addStyleName("parallel-program-usage-window-container");

        LayoutContainer lc = new LayoutContainer();

        add(lc, new BorderLayoutData(LayoutRegion.NORTH, 50));

        LayoutContainer gridContainer = new LayoutContainer();
        gridContainer.setLayout(new FitLayout());
        gridContainer.setStyleName("parallel-program-usage-panel-grid");
        gridContainer.add(ppumGrid);
        gridContainer.setHeight(250);
        
        add(gridContainer, new BorderLayoutData(LayoutRegion.CENTER));

        Button btnClose = closeButton();
        setButtonAlign(HorizontalAlignment.RIGHT);
        addButton(btnClose);

        getParallelProgramUsageRPC(store, ppModel);

        if (CmShared.getQueryParameter("debug") != null) {
            //Menu debugMenu = buildDebugMenu();
            //ppumGrid.setContextMenu(debugMenu);
        }

        setVisible(true);
    }

	private Menu buildDebugMenu() {
		MenuItem detailDebug = new MenuItem("Debug Info");
		detailDebug.addSelectionListener(new SelectionListener<MenuEvent>() {
		    public void componentSelected(MenuEvent ce) {
		        ParallelProgramUsageModel m = ppumGrid.getSelectionModel().getSelectedItem();
		        CatchupMathTools.showAlert("UserID: " + m.getUserId());
		    }
		});
		Menu menu = new Menu();
		menu.add(detailDebug);

		return menu;
	}

    private Button closeButton() {
        Button btn = new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        btn.setIconStyle("icon-delete");
        return btn;
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig program = new ColumnConfig();
        program.setId(ParallelProgramUsageModel.STUDENT_NAME);
        program.setHeader("Name");
        program.setWidth(125);
        program.setSortable(true);
        program.setMenuDisabled(true);
        configs.add(program);

        ColumnConfig activity = new ColumnConfig();
        activity.setId(ParallelProgramUsageModel.ACTIVITY);
        activity.setHeader("Activity");
        activity.setWidth(100);
        activity.setSortable(false);
        activity.setMenuDisabled(true);
        configs.add(activity);

        ColumnConfig result = new ColumnConfig();
        result.setId(ParallelProgramUsageModel.RESULT);
        result.setHeader("Result");
        result.setWidth(225);
        result.setSortable(false);
        result.setMenuDisabled(true);
        configs.add(result);
        
        ColumnConfig date = new ColumnConfig();
        date.setId(ParallelProgramUsageModel.USE_DATE);
        date.setHeader("Date");
        date.setWidth(75);
        date.setSortable(false);
        date.setMenuDisabled(true);
        configs.add(date);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    protected void getParallelProgramUsageRPC(final ListStore<ParallelProgramUsageModel> store, final ParallelProgramModel ppm) {

        CmBusyManager.setBusy(true);

        new RetryAction<CmList<ParallelProgramUsageModel>>() {
            public void oncapture(CmList<ParallelProgramUsageModel> list) {
                try {
                    store.removeAll();
                    store.add(list);

                    //_studentCount.setText("count: " + list.size());

                } finally {
                    CmBusyManager.setBusy(false);
                }
            }

            @Override
            public void attempt() {
                CmServiceAsync s = CmShared.getCmService();
                GetParallelProgramUsageAction action = new GetParallelProgramUsageAction();
                action.setParallelProgId(ppm.getId());
                setAction(action);
                s.execute(action, this);
            }
        }.register();
    }
}
