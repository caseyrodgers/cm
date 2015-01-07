package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModelProperties;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfParallelProgramUsageReportAction;
import hotmath.gwt.shared.client.rpc.action.GetParallelProgramUsageAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/*
 * Displays Student status in selected Parallel Program
 *
 * @author Bob
 * 
 */

public class ParallelProgramUsageWindow extends GWindow {

    private ParallelProgramModel ppModel;
    private Grid<ParallelProgramUsageModel> ppumGrid;
    private HTML templateHolder = new HTML();

    ParallelProgLoaderTemplate _template = GWT.create(ParallelProgLoaderTemplate.class) ;
    ParallelProgramUsageModelProperties _props = GWT.create(ParallelProgramUsageModelProperties.class);
    
    
    /**
     * Create Usage Window for Parallel Programs.
     * Displays student name, current Activity and most recent Result
     * for each Student.
     * 
     * @param ppModel
     */
    public ParallelProgramUsageWindow(final ParallelProgramModel ppModel) {
        super(true);
        
        this.ppModel = ppModel;
        setPixelSize(540, 330);
        setModal(true);
        setResizable(false);
        setHeadingText("Parallel Program Usage for: " + ppModel.getName());

        ListStore<ParallelProgramUsageModel> store = new ListStore<ParallelProgramUsageModel>(_props.id());
        ColumnModel<ParallelProgramUsageModel> cm = defineColumns();

        ppumGrid = new Grid<ParallelProgramUsageModel>(store, cm);
        ppumGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ppumGrid.getView().setStripeRows(true);

        addStyleName("parallel-program-usage-window-container");

        VerticalLayoutContainer tbVert = new VerticalLayoutContainer();
        tbVert.add(templateHolder);
        tbVert.add(toolbar());

        BorderLayoutContainer borLay = new BorderLayoutContainer();
        setWidget(borLay);
        
        borLay.setNorthWidget(tbVert, new BorderLayoutData(65));

        SimpleContainer gridContainer = new SimpleContainer();
        //gridContainer.setStyleName("parallel-program-usage-panel-grid");
        gridContainer.setWidget(ppumGrid);
        gridContainer.setHeight(250);

        borLay.setCenterWidget(gridContainer);
        
        templateHolder.setHTML(_template.render(ppModel).asString());
        
        getParallelProgramUsageRPC(store, ppModel);

        if (CmCore.isDebug() == true) {
            Menu debugMenu = buildDebugMenu();
            ppumGrid.setContextMenu(debugMenu);
        }

        setVisible(true);
    }

	private Menu buildDebugMenu() {
		MenuItem detailDebug = new MenuItem("Debug Info");
		detailDebug.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
		        ParallelProgramUsageModel m = ppumGrid.getSelectionModel().getSelectedItem();
		        CmMessageBox.showAlert("UserID: " + m.getUserId());
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
        toolBar.add(displayPrintableReportButton(ppModel));
        return toolBar;
    }

    private TextButton displayPrintableReportButton(final ParallelProgramModel pp) {
        TextButton ti = new TextButton("Print");
        //ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable usage report");
        ti.addStyleName("student-details-panel-pr-btn");
        
        ti.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new PdfWindow(pp.getAdminId(), "Catchup Math Usage Report for: " + pp.getName(),
                        new GeneratePdfParallelProgramUsageReportAction(pp.getAdminId(), pp.getId()));
            }
        });
        return ti;
    }

	private TextButton detailsButton() {
		return new TextButton("Details", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                ParallelProgramUsageModel mdl = getGridItem();
                if (mdl != null) {
                    StudentDetailsWindow.showStudentDetails(mdl.getUserId());
                }
            }});
	}

    private ColumnModel<ParallelProgramUsageModel> defineColumns() {
        List<ColumnConfig<ParallelProgramUsageModel, ?>> configs = new ArrayList<ColumnConfig<ParallelProgramUsageModel, ?>>();

        configs.add(new ColumnConfig<ParallelProgramUsageModel, String>(_props.studentName(),125, "Name"));
        configs.get(configs.size()-1).setMenuDisabled(true);

        configs.add(new ColumnConfig<ParallelProgramUsageModel, String>(_props.activity(),100, "Activity"));
        configs.get(configs.size()-1).setMenuDisabled(true);

        configs.add(new ColumnConfig<ParallelProgramUsageModel, String>(_props.result(),220, "Result"));
        configs.get(configs.size()-1).setMenuDisabled(true);

        configs.add(new ColumnConfig<ParallelProgramUsageModel, String>(_props.useDate(),77, "Date"));
        configs.get(configs.size()-1).setMenuDisabled(true);
        configs.get(configs.size()-1).setSortable(false);

        return new ColumnModel<ParallelProgramUsageModel>(configs);
    }

    protected void getParallelProgramUsageRPC(final ListStore<ParallelProgramUsageModel> store, final ParallelProgramModel ppm) {

        CmBusyManager.setBusy(true);

        new RetryAction<CmList<ParallelProgramUsageModel>>() {
            public void oncapture(CmList<ParallelProgramUsageModel> list) {
                try {
                    store.clear();
                    store.addAll(list);
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }

            @Override
            public void attempt() {
                CmServiceAsync s = CmRpcCore.getCmService();
                GetParallelProgramUsageAction action = new GetParallelProgramUsageAction();
                action.setParallelProgId(ppm.getId());
                setAction(action);
                s.execute(action, this);
            }
        }.register();
    }

//    private Widget infoPanel() {
//        return templateHolder;
//    }

    private ParallelProgramUsageModel getGridItem() {
        ParallelProgramUsageModel mdl = ppumGrid.getSelectionModel().getSelectedItem();
        if (mdl == null) {
            CmMessageBox.showAlert("Please make a selection first");
        }
        return mdl;
    }
}


interface ParallelProgLoaderTemplate extends XTemplates {
    @XTemplate(
    		"<div class='detail-info'>" +
            "    <div class='form left'> " +
            "        <div class='fld'><label>Program:</label><div>{programName}&nbsp;</div></div>" +
            "    </div> " +
            "</div>") 
    SafeHtml render(ParallelProgramModel adminInfo);
}
