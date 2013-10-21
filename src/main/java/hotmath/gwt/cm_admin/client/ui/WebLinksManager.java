package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.GroupCombo.Callback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction.CrudOperation;
import hotmath.gwt.cm_rpc.client.rpc.GetWebLinksForAdminAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class WebLinksManager extends GWindow {
    
    int adminId ;
    protected CmList<WebLinkModel> _allLinks;
    public WebLinksManager(int adminId) {
        super(false);
        this.adminId = adminId;
        setHeadingText("Web Links Manager");
        setPixelSize(500,  300);
        buildUi();
        setVisible(true);
        
        readWebLinksFromServer(adminId);
    }

    private void readWebLinksFromServer(final int adminId) {
        new RetryAction<CmList<WebLinkModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetWebLinksForAdminAction action = new GetWebLinksForAdminAction(adminId);
                setAction(action);
                CmShared.getCmService().execute(action, this);                
            }
            
            @Override
            public void oncapture(CmList<WebLinkModel> links) {
                CmBusyManager.setBusy(false);
                _allLinks = links;
                _grid.getStore().clear();
                _grid.getStore().addAll(_allLinks);
            }
        }.attempt();
    }

    Grid<WebLinkModel> _grid; 
    GridProperties gridProps = GWT.create(GridProperties.class);
    private GroupCombo _groupCombo;
    private void buildUi() {
        _groupCombo = new GroupCombo(this.adminId,new Callback() {
            
            @Override
            public void groupSelected(GroupInfoModel group) {
                filterByGroup(group);
            }
        });
        addTool(createAddButton());
        addTool(createDelButton());
        addTool(createEditButton());
        addTool(_groupCombo.asWidget());
        addTool(new ToolButton(ToolButton.GEAR));
        
        //_groupCombo.asWidget().getElement().setAttribute("style",  "left: 20px");
        //getButtonBar().add(_groupCombo.asWidget());
        //addButton(_groupCombo.asWidget());
        addCloseButton();

        ListStore<WebLinkModel> store = new ListStore<WebLinkModel>(gridProps.id());
        List<ColumnConfig<WebLinkModel, ?>> cols = new ArrayList<ColumnConfig<WebLinkModel, ?>>();
        cols.add(new ColumnConfig<WebLinkModel, String>(gridProps.name(), 160, "Name"));
        cols.add(new ColumnConfig<WebLinkModel, String>(gridProps.url(), 240, "URL"));
        ColumnModel<WebLinkModel> cm = new ColumnModel<WebLinkModel>(cols);
        _grid = new Grid<WebLinkModel>(store,cm);
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                editCurrent();
            }
        });
        setWidget(_grid);
    }

    
    protected void filterByGroup(GroupInfoModel group) {
        List<WebLinkModel> filtered = null;
        
        if(group.getId() == 0) {
            filtered = _allLinks;
        }
        else {
            filtered = new ArrayList<WebLinkModel>();
            for(WebLinkModel w: _allLinks) {
                for(GroupInfoModel gm: w.getLinkGroups()) {
                    if(gm.getId() == group.getId()) {
                        filtered.add(w);
                        break;
                    }
                }
            }
        }
        _grid.getStore().clear();
        _grid.getStore().addAll(filtered);
    }

    private Widget createEditButton() {
        TextButton button = new TextButton("Edit", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                editCurrent();
            }
        });
        button.setToolTip("Edit selected web link");
        return button;
    }

    protected void editCurrent() {
        final WebLinkModel webLink = getSelectedWebLink();
        if(webLink == null) {
            return;
        }
        new WebLinkEditorDialog(webLink, new CallbackOnComplete() {
            
            @Override
            public void isComplete() {
                readWebLinksFromServer(adminId);                
            }
        });
    }

    private Widget createDelButton() {
        TextButton button = new TextButton("Delete", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                deleteCurrent();
            }
        });
        button.setToolTip("Delete selected web link");
        return button;
    }
    
    private Widget createAddButton() {
        TextButton button = new TextButton("Add", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                WebLinkModel webLinkModel = new WebLinkModel(0,adminId, "New Web Link", "http://", "");
                new WebLinkEditorDialog(webLinkModel, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        readWebLinksFromServer(adminId);
                    }
                });
            }
        });
        button.setToolTip("Add a new web link");
        return button;
    }


    private WebLinkModel getSelectedWebLink() {
        WebLinkModel model = _grid.getSelectionModel().getSelectedItem();
        if(model == null) {
            CmMessageBox.showAlert("Please select a web link first");
        }
        return model;
    }
    
    protected void deleteCurrent() {
        final WebLinkModel webLink = getSelectedWebLink();
        if(webLink == null) {
            return;
        }
        
        CmMessageBox.confirm("Delete Web Link",  "Are you sure you want to remove this web link?",new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if(yesNo) {
                    new RetryAction<RpcData>() {
                        @Override
                        public void attempt() {
                            CmBusyManager.setBusy(true);
                            DoWebLinksCrudOperationAction action = new DoWebLinksCrudOperationAction(adminId, CrudOperation.DELETE, webLink);
                            setAction(action);
                            CmShared.getCmService().execute(action, this);                
                        }
                        
                        @Override
                        public void oncapture(RpcData data) {
                            CmBusyManager.setBusy(false);
                            _grid.getStore().remove(webLink);
                        }
                    }.attempt();
                }
            }
        });
    }


    public interface GridProperties extends PropertyAccess<String> {
        @Path("name")
        ModelKeyProvider<WebLinkModel> id();

        ValueProvider<WebLinkModel, String> url();

        ValueProvider<WebLinkModel, String> name();        
    }


    public static void startTest() {
        new WebLinksManager(2);
    }
}
