package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.StudentPanelButton;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CCSSDetail;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSDetailAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/* Display CCSS standard list
 * 
 */
public class StandardListDialog extends GWindow {

    Grid<CCSSDetail> _grid;
    BorderLayoutContainer _container = new BorderLayoutContainer();

    ListStore<CCSSDetail> _store = new ListStore<CCSSDetail>(_dataAccess.nameKey());

    int _height = 400;
    int _width = 270;

    public StandardListDialog(String title, int height) {
    	super(false);
    	_height += (height < 70) ? height : 70;
    	buildUI(title);
    }
    
    public StandardListDialog(String title) {
    	super(false);
    	buildUI(title);
    }

    public void loadStandards(final List<String> stdNames) {
        new RetryAction<CmList<CCSSDetail>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);

                CCSSDetailAction action = new CCSSDetailAction(stdNames);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CCSSDetail> standards) {
                CmBusyManager.setBusy(false);
                addStandards(standards);
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
            }
        }.register();


    }

    protected void addStandards(List<CCSSDetail> standards) {
    	if (isVisible()) setVisible(false);
    	_store.clear();
        _store.addAll(standards);
        setVisible(true);
        forceLayout();
    }

    protected void buildUI(String title) {
        setWidth(_width);
        setHeight(_height);

        setHeadingText(title);

        _grid = defineGrid(_store, defineColumns());

        _grid.setToolTip("Click for description.");

        _grid.addCellClickHandler(getClickHandler());

        //_container.setScrollMode(ScrollMode.AUTO);
        _container.setCenterWidget(_grid);
        add(_container);
        
        super.addCloseButton();
        
        TextButton print = new StudentPanelButton("Print");
        print.setToolTip("Display a printable report.");
        print.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                List<String> stdNames = new ArrayList<String>();
                List<CCSSDetail> standards = _grid.getStore().getAll();
                //int adminId = StudentGridPanel.instance.getCmAdminMdl().getUid();
                for(int i=0,t=standards.size();i<t;i++) {
                    stdNames.add(standards.get(i).getCcssName());
                }
                CmMessageBox.showAlert("Sorry, print not available.");
                /*
                GeneratePdfAction action = new GeneratePdfAction(PdfType.STUDENT_LIST,adminId,uids);
                action.setTitle(header.getText());
                action.setFilterMap(StudentGridPanel.instance._pageAction.getFilterMap());
                new PdfWindow(0, "Catchup Math Student List", action);
                */
            }
        });
        //getHeader().addTool(print);

        setModal(true);
    }
    
    
    private Grid<CCSSDetail> defineGrid(final ListStore<CCSSDetail> store, ColumnModel<CCSSDetail> cm) {
        final Grid<CCSSDetail> grid = new Grid<CCSSDetail>(store, cm);
        grid.setBorders(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getView().setAutoExpandColumn(cm.findColumnConfig("name"));
        grid.setWidth(255);
        grid.setHeight(300);
        grid.setStateful(true);
        grid.setLoadMask(true);
        return grid;
    }

    private ColumnModel<CCSSDetail> defineColumns() {
        List<ColumnConfig<CCSSDetail, ?>> cols = new ArrayList<ColumnConfig<CCSSDetail, ?>>();

        cols.add(new ColumnConfig<CCSSDetail, String>(_dataAccess.name(), 235, "Standard Name"));
        // column.setSortable(true);

        return new ColumnModel<CCSSDetail>(cols);
    }

    protected CellClickHandler getClickHandler() {
    	return new CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                if (_grid.getSelectionModel().getSelectedItems().size() > 0) {
                    CmLogger.debug("click handler: Showing CCSS summary");
                    CCSSDetail detail = _grid.getSelectionModel().getSelectedItems().get(0);
                    new StandardWindowForCCSS(detail.getCcssName(), detail.getSummary());
                }
            }
        };
    }

	public interface DataPropertyAccess extends PropertyAccess<CCSSDetail> {
		@Path("ccssName")
		ValueProvider<CCSSDetail, String> name();

		@Path("ccssName")
		ModelKeyProvider<CCSSDetail> nameKey();
	}

	private static final DataPropertyAccess _dataAccess = GWT.create(DataPropertyAccess.class);

}
