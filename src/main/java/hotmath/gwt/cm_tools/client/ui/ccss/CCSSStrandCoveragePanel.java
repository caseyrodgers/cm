package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.DateRangeWidget;
import hotmath.gwt.shared.client.model.CCSSStrandCoverageData;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSStrandCoverageDataAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class CCSSStrandCoveragePanel extends SimpleContainer {
    
    Grid<CCSSStrandCoverageData> _grid;
    CCSSStrandCoveragePanel __instance;
    int _uid;
    int _adminId;
    String _levelName;

    interface GridProperties extends PropertyAccess<CCSSStrandCoverageData> {

    	@Path("ccssName")
        ModelKeyProvider<CCSSStrandCoverageData> id();

        ValueProvider<CCSSStrandCoverageData, Integer> sequenceNum();

        @Path("ccssName")
        ValueProvider<CCSSStrandCoverageData, String> name();

        ValueProvider<CCSSStrandCoverageData, Boolean> asAssignment();
        ValueProvider<CCSSStrandCoverageData, Boolean> asLesson();
        ValueProvider<CCSSStrandCoverageData, Boolean> asQuiz();
    }

    GridProperties _dataAccess = GWT.create(GridProperties.class);
    
    public CCSSStrandCoveragePanel(String levelName, int uid, int adminId) {
        __instance = this;
        this._uid = uid;
        this._adminId = adminId;
        this._levelName = levelName;
        getDataFromServer();
    }

    protected ColumnModel<CCSSStrandCoverageData> getColumns() {
        List<ColumnConfig<CCSSStrandCoverageData, ?>> cols = new ArrayList<ColumnConfig<CCSSStrandCoverageData, ?>>();

        cols.add(new ColumnConfig<CCSSStrandCoverageData, String>(_dataAccess.name(), 100, "Standard"));
        // column.setSortable(true);

        SimpleSafeHtmlCell<Boolean> cell =
        		new SimpleSafeHtmlCell<Boolean>(new AbstractSafeHtmlRenderer<Boolean>() {
        		      @Override
        		      public SafeHtml render(Boolean value) {
        		    	  String html = (value == true) ?
        		    		  html = "<div class='ccss-strand-coverage-grid'><img src='/gwt-resources/images/green_checkmark_small.png'/></div>" : "";
    		    		  return new SafeHtmlBuilder().appendHtmlConstant(html).toSafeHtml();
        		      }
        		}, BrowserEvents.CLICK);

        ColumnConfig<CCSSStrandCoverageData, Boolean> assignCol = new ColumnConfig<CCSSStrandCoverageData, Boolean>(_dataAccess.asAssignment(), 70, "Assignment");
        cols.add(assignCol);
        assignCol.setCell(cell);

        ColumnConfig<CCSSStrandCoverageData, Boolean> lessonCol = new ColumnConfig<CCSSStrandCoverageData, Boolean>(_dataAccess.asLesson(), 70, "Lesson");
        cols.add(lessonCol);
        lessonCol.setCell(cell);

        ColumnConfig<CCSSStrandCoverageData, Boolean> quizCol = new ColumnConfig<CCSSStrandCoverageData, Boolean>(_dataAccess.asQuiz(), 70, "Quiz");
        cols.add(quizCol);
        quizCol.setCell(cell);

        return new ColumnModel<CCSSStrandCoverageData>(cols);

    }

    protected HTML getNoDataMessage() {
    	return new HTML("Nothing to display");
    }

    protected String getWindowTitle() {
    	return "CCSS Strand Coverage";
    }

	protected void getDataFromServer() {
        CmLogger.debug("getDataFromServer(): loading strand coverage details for " + _levelName);
        new RetryAction<CmList<CCSSStrandCoverageData>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                Date fromDate = DateRangeWidget.getInstance().getFromDate();
                Date toDate = DateRangeWidget.getInstance().getToDate();
                CCSSStrandCoverageDataAction action = new CCSSStrandCoverageDataAction(_adminId, _uid,fromDate, toDate);
                action.setLevelName(_levelName);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CCSSStrandCoverageData> allData) {
                CmBusyManager.setBusy(false);
                CmLogger.debug("getDataFromServer(): got " + allData.size() + "strand coverage details for " + _levelName);
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

    CmList<CCSSStrandCoverageData> strandCovData;
    public CmList<CCSSStrandCoverageData> getData() {
        return strandCovData;
    }

    private void drawTable(CmList<CCSSStrandCoverageData> data) {
        strandCovData = data;

        ListStore<CCSSStrandCoverageData> store = new ListStore<CCSSStrandCoverageData>(_dataAccess.id());
        _grid = defineGrid(store, getColumns());
        if(data == null || data.size() == 0) {
            add(new NoRowsFoundPanel(getNoDataMessage()));
        }
        else {
            for (int i = 0, t = data.size(); i < t; i++) {
                store.add(data.get(i));
            }
            
            add(_grid);
            
            _grid.setToolTip("Double click for CCSS details");

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
        forceLayout();
    }

    private Grid<CCSSStrandCoverageData> defineGrid(final ListStore<CCSSStrandCoverageData> store, ColumnModel<CCSSStrandCoverageData> cm) {
        final Grid<CCSSStrandCoverageData> grid = new Grid<CCSSStrandCoverageData>(store, cm);
        grid.setBorders(true);

        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getView().setAutoExpandColumn(cm.findColumnConfig("ccssName"));
        
        /** set to default to allow IE to render table correctly
         * 
         */
        grid.setWidth("310");
        grid.setHeight("100%");
        grid.setLoadMask(true);
        return grid;
    }

    protected void showCCSSDetails() {
        final CCSSStrandCoverageData model = _grid.getSelectionModel().getSelectedItem();
        new StandardWindowForCCSS(model.getCcssName(), null);
    }


}

/** Shown when no data is available
 * 
 */
class NoRowsPanel extends CenterLayoutContainer {
    public NoRowsPanel(HTML message) {
        add(message);
    }
}