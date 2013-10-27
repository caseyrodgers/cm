package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.GroupCombo.Callback;
import hotmath.gwt.cm_admin.client.ui.WebLinkEditorDialog.EditType;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction.CrudOperation;
import hotmath.gwt.cm_rpc.client.rpc.GetWebLinksForAdminAction;
import hotmath.gwt.cm_rpc.client.rpc.GetWebLinksForAdminAction.TypeOfGet;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

public class WebLinksManager extends GWindow {
    
    int adminId ;
    protected CmList<WebLinkModel> _allLinks;
    ContentPanel _privateLinksPanel = new ContentPanel();
    ContentPanel _publicLinksPanel = new ContentPanel();
    public WebLinksManager(int adminId) {
        super(false);
        this.adminId = adminId;
        setHeadingText("Web Links Manager");
        setPixelSize(500,  350);
        buildUi();
        setVisible(true);
        
        readWebLinksFromServer(adminId);
    }

    private void readWebLinksFromServer(final int adminId) {
        new RetryAction<CmList<WebLinkModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetWebLinksForAdminAction action = new GetWebLinksForAdminAction(TypeOfGet.PRIVATE, adminId);
                setAction(action);
                CmShared.getCmService().execute(action, this);                
            }
            
            @Override
            public void oncapture(CmList<WebLinkModel> links) {
                CmBusyManager.setBusy(false);
                _allLinks = links;
                _grid4PrivateLinks.getStore().clear();
                _grid4PrivateLinks.getStore().addAll(_allLinks);
            }
        }.attempt();
    }

    TabPanel _tabPanel;
    Grid<WebLinkModel> _grid4PrivateLinks;
    Grid<WebLinkModel> _grid4PublicLinks;
    GridProperties gridProps = GWT.create(GridProperties.class);
    private GroupCombo _groupCombo;
    private void buildUi() {
        _groupCombo = new GroupCombo(this.adminId,new Callback() {
            
            @Override
            public void groupSelected(GroupInfoModel group) {
                filterByGroup(group);
            }
        });
        _privateLinksPanel.addTool(_groupCombo.asWidget());
        _privateLinksPanel.addTool(new HTML("&nbsp;&nbsp;"));
        _privateLinksPanel.addTool(createAddButton());
        _privateLinksPanel.addTool(createDelButton());
        _privateLinksPanel.addTool(createEditButton());
        
        //_groupCombo.asWidget().getElement().setAttribute("style",  "left: 20px");
        //getButtonBar().add(_groupCombo.asWidget());
        //addButton(_groupCombo.asWidget());
        addCloseButton();

        _tabPanel = new TabPanel();

        _grid4PrivateLinks = createPrivateLinkGrid();        
        _grid4PrivateLinks.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                editCurrent();
            }
        });        
        _privateLinksPanel.setWidget(_grid4PrivateLinks);

        _tabPanel.add(_privateLinksPanel, new TabItemConfig("Private Links", false));
        
        _publicLinksPanel.setWidget(new DefaultGxtLoadingPanel());
        _tabPanel.add(_publicLinksPanel, new TabItemConfig("Public Links", false));
        
        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                
                if(_tabPanel.getActiveWidget() == _tabPanel.getWidget(0)) {
                    readWebLinksFromServer(adminId);
                }
                else {
                    if(_grid4PublicLinks == null) {
                        createGrid4Public();
                    }
                    loadGrid4Public();
                }
            }
        });

        setWidget(_tabPanel);
    }

    protected void importWebLink(WebLinkModel selectionModel) {
        new WebLinkEditorDialog(EditType.IMPORT,adminId, selectionModel, new CallbackOnComplete() {
            @Override
            public void isComplete() {
            }
        });
    }

    private void createGrid4Public() {
        _grid4PublicLinks = createPrivateLinkGrid();
        _grid4PublicLinks.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                importSelectedWebLink();
            }
        }); 
        _publicLinksPanel.setWidget(_grid4PublicLinks);
        TextButton importBtn = new TextButton("Import", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                importSelectedWebLink();
            }
        });
        importBtn.setToolTip("Import selected web link into your private links");
        _publicLinksPanel.addTool(importBtn);
        
    }
    protected void importSelectedWebLink() {
        WebLinkModel model = _grid4PublicLinks.getSelectionModel().getSelectedItem();
        if(model == null) {
            CmMessageBox.showAlert("Please select a public link first");
            return;
        }
        importWebLink(model);        
   }

    private void loadGrid4Public() {
        new RetryAction<CmList<WebLinkModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetWebLinksForAdminAction action = new GetWebLinksForAdminAction(TypeOfGet.PUBLIC,0);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            @Override
            public void oncapture(CmList<WebLinkModel> value) {
                CmBusyManager.setBusy(false);
                _grid4PublicLinks.getStore().clear();
                _grid4PublicLinks.getStore().addAll(value);
                forceLayout();
            }
        }.register();
    }
    
    private Grid<WebLinkModel> createPrivateLinkGrid() {
        ListStore<WebLinkModel> store = new ListStore<WebLinkModel>(gridProps.id());
        List<ColumnConfig<WebLinkModel, ?>> cols = new ArrayList<ColumnConfig<WebLinkModel, ?>>();
        cols.add(new ColumnConfig<WebLinkModel, String>(gridProps.name(), 160, "Name"));
        cols.add(new ColumnConfig<WebLinkModel, String>(gridProps.url(), 300, "URL"));
        ColumnModel<WebLinkModel> cm = new ColumnModel<WebLinkModel>(cols);
        Grid<WebLinkModel> grid = new Grid<WebLinkModel>(store,cm);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        grid.getView().setAutoExpandColumn(grid.getColumnModel().getColumn(1));
        grid.getView().setAutoFill(true);
        
        return grid;
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
        _grid4PrivateLinks.getStore().clear();
        _grid4PrivateLinks.getStore().addAll(filtered);
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
        new WebLinkEditorDialog(EditType.NEW_OR_EDIT, adminId, webLink, new CallbackOnComplete() {
            
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
                WebLinkModel webLinkModel = new WebLinkModel(0,adminId, "New Web Link", "http://", "", AvailableOn.DESKTOP_AND_MOBILE,false);
                new WebLinkEditorDialog(EditType.NEW_OR_EDIT, adminId, webLinkModel, new CallbackOnComplete() {
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
        WebLinkModel model = _grid4PrivateLinks.getSelectionModel().getSelectedItem();
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
                            _grid4PrivateLinks.getStore().remove(webLink);
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
