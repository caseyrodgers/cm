package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfParallelProgramUsageReportAction;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramUsageAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
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
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/*
 * Displays Student status in selected Parallel Program
 *
 * @author Bob
 * 
 */

public class ParallelProgramUsageWindow extends CmWindow {

    private ParallelProgramModel ppModel;
    private Grid<ParallelProgramUsageModel> ppumGrid;
    private HTML html;
    private XTemplate template;
   
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
        setSize(540, 330);
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
        lc.add(infoPanel());
        lc.add(toolbar());

        add(lc, new BorderLayoutData(LayoutRegion.NORTH, 65));

        LayoutContainer gridContainer = new LayoutContainer();
        gridContainer.setLayout(new FitLayout());
        gridContainer.setStyleName("parallel-program-usage-panel-grid");
        gridContainer.add(ppumGrid);
        gridContainer.setHeight(250);

        add(gridContainer, new BorderLayoutData(LayoutRegion.CENTER));

        Button btnClose = closeButton();
        setButtonAlign(HorizontalAlignment.RIGHT);
        addButton(btnClose);
        
        template.overwrite(html.getElement(), Util.getJsObject(ppModel));

        getParallelProgramUsageRPC(store, ppModel);

        if (CmShared.getQueryParameter("debug") != null) {
            Menu debugMenu = buildDebugMenu();
            ppumGrid.setContextMenu(debugMenu);
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

	private ToolBar toolbar() {
        ToolBar toolBar = new ToolBar();
        toolBar.add(detailsButton());
        toolBar.add(new FillToolItem());
        if (CmShared.getQueryParameter("debug") != null)
            toolBar.add(displayPrintableReportButton(ppModel));
        return toolBar;
    }

    private Button displayPrintableReportButton(final ParallelProgramModel pp) {
        Button ti = new Button();
        ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable usage report");
        ti.addStyleName("student-details-panel-pr-btn");
        
        Window.alert("adminID: " + pp.getAdminId());

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {

                new PdfWindow(pp.getAdminId(), "Catchup Math Usage Report for: " + pp.getName(),
                        new GeneratePdfParallelProgramUsageReportAction(pp.getAdminId(), pp.getId()));
            }
        });
        //ti.disable();
        return ti;
    }

	private Button detailsButton() {
		return new Button("Details", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ParallelProgramUsageModel mdl = getGridItem();
                if (mdl != null) {
                    StudentDetailsWindow.showStudentDetails(mdl.getUserId());
                }
            }});
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
        result.setWidth(220);
        result.setSortable(false);
        result.setMenuDisabled(true);
        configs.add(result);
        
        ColumnConfig date = new ColumnConfig();
        date.setId(ParallelProgramUsageModel.USE_DATE);
        date.setHeader("Date");
        date.setWidth(77);
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

    private Widget infoPanel() {
        defineInfoTemplate();
        return html;
    }

    /**
     * Define the template for the header (does not add to container)
     * 
     * Defines the global 'html' object, filled in via RPC call.
     * 
     */
    private void defineInfoTemplate() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='detail-info'>");
        sb.append("<div class='form left'>");
        sb.append("  <div class='fld'><label>Program:</label><div>{");
        sb.append(ParallelProgramModel.PROGRAM_NAME).append("}&nbsp;</div></div>");
        sb.append("</div>");
        sb.append("</div>");

        template = XTemplate.create(sb.toString());
        html = new HTML();

        html.setHeight("35px"); // to eliminate the jump when setting values in
        // template
    }
    
    private ParallelProgramUsageModel getGridItem() {
        ParallelProgramUsageModel mdl = ppumGrid.getSelectionModel().getSelectedItem();
        if (mdl == null) {
            CatchupMathTools.showAlert("Please make a selection first");
        }
        return mdl;
    }
}
