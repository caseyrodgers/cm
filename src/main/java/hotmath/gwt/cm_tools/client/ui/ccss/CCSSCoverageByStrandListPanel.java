package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.DateRangeWidget;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSStrandCoverageAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

//import hotmath.gwt.shared.client.rpc.action.GeneratePdfStudentCCSSReportAction;

/**
 * 
 * @author bob
 *
 */

public class CCSSCoverageByStrandListPanel extends BorderLayoutContainer {
	
    interface CCSSStrandCoverageDataAccess extends PropertyAccess<CCSSStrandCoverage> {
		 
		    @Path("label")
		    ValueProvider<CCSSStrandCoverage, String> name();

		    @Path("label")
		    ModelKeyProvider<CCSSStrandCoverage> key();
		  }

	private static final CCSSStrandCoverageDataAccess _dataAccess = GWT.create(CCSSStrandCoverageDataAccess.class);

    Grid<CCSSStrandCoverage> _grid;
    ListStore<CCSSStrandCoverage> _store = new ListStore<CCSSStrandCoverage>(_dataAccess.key());

    static CCSSCoverageByStrandListPanel __instance;
    
    BorderLayoutContainer _parent;
    BorderLayoutData _layoutData;
    int _uid;   // either student or group UID
    int _adminId;
    boolean _isGroupReport;
    DateRangeWidget _dateRange = DateRangeWidget.getInstance();

    public CCSSCoverageByStrandListPanel(BorderLayoutContainer parent, BorderLayoutData layoutData,
    		int uid, int adminId, boolean isGroupReport) {
        __instance = this;
        _parent = parent;
        _layoutData = layoutData;
        _uid = uid;
        _adminId = adminId;
        _isGroupReport = isGroupReport;

        setVisible(false);

        CenterLayoutContainer northContainer = new CenterLayoutContainer();
        northContainer.setBorders(true);
        northContainer.add(getHeading());

        setNorthWidget(northContainer, new BorderLayoutData(30));

        getDataFromServer();

    }

    private void printCurrentReport() {
        CCSSCoverageReport report = null; //_listReports.getSelectionModel().getSelectedItem();
        if(report == null) {
            CmMessageBox.showAlert("Nothing to print");
            return;
        }

        String reportName = report.getText();
        //TODO: 
        //GeneratePdfStudentCCSSReportAction action = new GeneratePdfStudentCCSSReportAction(StudentGridPanel.instance.getCmAdminMdl().getUid(), reportName,reportLayout, StudentGridPanel.instance.getPageAction());
        //action.setFilterMap(StudentGridPanel.instance.getPageAction().getFilterMap());
        //new PdfWindow(0, "Catchup Math CCSS Coverage Report", action);
    }
        
	protected void getDataFromServer() {
		
        new RetryAction<CmList<CCSSStrandCoverage>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                Date fromDate = _dateRange.getFromDate();
                Date toDate = _dateRange.getToDate();
                CCSSStrandCoverageAction action = new CCSSStrandCoverageAction(_adminId, _uid, fromDate, toDate);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

			@Override
            public void oncapture(CmList<CCSSStrandCoverage> data) {
                CmBusyManager.setBusy(false);
                updateUI(data);
                _dateRange.refresh();
                setVisible(true);
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
                setVisible(true);
            }
        }.register();
    }

	protected void updateUI(CmList<CCSSStrandCoverage> data) {
    	_store.clear();
        _store.addAll(data);
		
        _grid = defineGrid(_store, defineColumn());

        _grid.setToolTip("Display standard codes and Catchup Math activties for this strand.");

        _grid.addCellClickHandler(getClickHandler());

		setCenterWidget(_grid);

		forceLayout();
	}

    private Grid<CCSSStrandCoverage> defineGrid(final ListStore<CCSSStrandCoverage> store, ColumnModel<CCSSStrandCoverage> cm) {
        final Grid<CCSSStrandCoverage> grid = new Grid<CCSSStrandCoverage>(store, cm);
        grid.setBorders(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getView().setAutoExpandColumn(cm.findColumnConfig("name"));
        grid.setWidth(240);
        grid.setHeight(300);
        //grid.setStateful(true);
        //grid.setLoadMask(true);
        grid.setHideHeaders(true);
        return grid;
    }

    private ColumnModel<CCSSStrandCoverage> defineColumn() {
        List<ColumnConfig<CCSSStrandCoverage, ?>> cols = new ArrayList<ColumnConfig<CCSSStrandCoverage, ?>>();

        ColumnConfig<CCSSStrandCoverage, String>  colConfig = new ColumnConfig<CCSSStrandCoverage, String>(_dataAccess.name(), 250);
        colConfig.setSortable(false);
        cols.add(colConfig);

        return new ColumnModel<CCSSStrandCoverage>(cols);
    }

    private Map<String, CCSSStrandCoveragePanel> _panelMap = new HashMap<String, CCSSStrandCoveragePanel>();
    protected CellClickHandler getClickHandler() {
    	return new CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                if (_grid.getSelectionModel().getSelectedItems().size() > 0) {
                    CmLogger.debug("click handler: Showing Strand coverage details for selected strand");
                    if (__instance.getEastWidget() != null) __instance.remove(__instance.getEastWidget());
                    CCSSStrandCoverage strand = _grid.getSelectionModel().getSelectedItems().get(0);
                    CmLogger.debug("click handler: Showing Strand coverage details for strand: " + strand.getLevelName());
                    CCSSStrandCoveragePanel panel = _panelMap.get(strand.getLevelName());
                    if (panel == null) {
                    	panel = new CCSSStrandCoveragePanel(strand.getLevelName(), _uid, _adminId);
                    	_panelMap.put(strand.getLevelName(), panel);
                    }
                    _parent.setCenterWidget(panel);
                    _parent.forceLayout();
                }
            }
        };
    }

	private HTML getHeading() {
		return new HTML("<h1 style='color:#1C97D1; font-size:100%;'>Strands</h1>");
	}
	
    static {
        hotmath.gwt.shared.client.eventbus.EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_PRINT_CCSS_COVERAGE_REPORT) {
                    __instance.printCurrentReport();    
                }
            }
        });
    }
    
}