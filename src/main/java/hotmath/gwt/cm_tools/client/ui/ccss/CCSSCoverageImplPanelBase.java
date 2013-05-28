package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageData;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageDataAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

abstract public class CCSSCoverageImplPanelBase extends SimpleContainer {
    
    CCSSCoverageImplBase base;
    Grid<CCSSCoverageData> _grid;
    CCSSCoverageImplPanelBase __instance;

    int userId;
    int groupId;

    interface GridProperties extends PropertyAccess<CCSSCoverageReport> {

    	@Path("uid")
        ModelKeyProvider<CCSSCoverageData> id();

        @Path("id")
        ValueProvider<CCSSCoverageData, String> ccssName();
    }

    GridProperties _gridProps = GWT.create(GridProperties.class);
    
    public CCSSCoverageImplPanelBase(CCSSCoverageImplBase base) {
        __instance = this;
        this.base = base;
        getDataFromServer();
    }

    abstract public CCSSCoverageDataAction.ReportType getReportType();
    
    abstract protected ColumnModel<CCSSCoverageData> getColumns();

    protected String getWindowTitle() {
    	return "CCSS Coverage";
    }

    public String[][] getPanelValues() {
        return new String[0][0];    
    }
    
    public String[] getPanelColumns() {
        return new String[0];
    }
    
    public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	protected void getDataFromServer() {
        new RetryAction<CmList<CCSSCoverageData>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                CCSSCoverageDataAction action = new CCSSCoverageDataAction(StudentGridPanel.instance.getPageAction(), getReportType(),
                        StudentGridPanel.instance.getCmAdminMdl().getUid(), userId, groupId, 
                        DateRangePanel.getInstance().getFromDate(),
                        DateRangePanel.getInstance().getToDate());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CCSSCoverageData> allData) {
                CmBusyManager.setBusy(false);
                drawTable(allData);
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
                drawTable(null);
            }
        }.register();
    }

    CmList<CCSSCoverageData> ccssCoverageData;
    public CmList<CCSSCoverageData> getData() {
        return ccssCoverageData;
    }

    private void drawTable(CmList<CCSSCoverageData> data) {
        ccssCoverageData = data;

        ListStore<CCSSCoverageData> store = new ListStore<CCSSCoverageData>(_gridProps.id());
        _grid = defineGrid(store, getColumns());
        if(data == null || data.size() == 0) {
            add(new NoRowsFoundPanel());
        }
        else {
            
            for (int i = 0, t = data.size(); i < t; i++) {
                store.add(data.get(i));
            }
            
            add(_grid);
            
            String tip = getGridToolTip();
            if(tip != null)
                _grid.setToolTip(tip);

            _grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
                @Override
                public void onCellClick(CellDoubleClickEvent event) {
                    if (_grid.getSelectionModel().getSelectedItems().size() > 0) {
                        CmLogger.debug("click handler: Showing CCSS Details");
                        showCCSSDetails();
                    }
                }
            });

        }
        
    }
    
    protected String getGridToolTip() {
        return "Double click for CCSS details";
    }

    private Grid<CCSSCoverageData> defineGrid(final ListStore<CCSSCoverageData> store, ColumnModel<CCSSCoverageData> cm) {
        final Grid<CCSSCoverageData> grid = new Grid<CCSSCoverageData>(store, cm);
        grid.setBorders(true);

        grid.getView().setAutoExpandColumn(cm.findColumnConfig("name"));
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        /** set to default to allow IE to render table correctly
         * 
         */
        grid.setWidth("500");
        grid.setHeight("100%");
        grid.setLoadMask(true);
        return grid;
    }

    //TODO:
    protected void showCCSSDetails() {

        final CCSSCoverageData model = _grid.getSelectionModel().getSelectedItem();
        
    }

}

/** Shown when no data is available
 * 
 */
class NoRowsFoundPanel extends CenterLayoutContainer {
    public NoRowsFoundPanel() {
        add(new HTML("<h1 style='color:#1C97D1; font-size:1.2em; margin:10px; padding:10px'>No data for the selected category.<br/>Please 'mouseover' the menu item for details.</h1>"));
    }
}