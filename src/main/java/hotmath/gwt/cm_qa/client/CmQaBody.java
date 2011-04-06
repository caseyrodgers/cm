package hotmath.gwt.cm_qa.client;

import hotmath.gwt.cm_rpc.client.model.QaEntryModel;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetQaItemsAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQaItemAction;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;

public class CmQaBody extends ContentPanel {
    
    Grid<QaEntryModelGxt> _grid;
    public CmQaBody() {
        
        ListStore<QaEntryModelGxt> store = new ListStore<QaEntryModelGxt>();
        _grid = defineGrid(store);
        
        setLayout(new FitLayout());
        add(_grid);
        getQaItems();
    }
    
    
    private Grid<QaEntryModelGxt> defineGrid(final ListStore<QaEntryModelGxt> store) {
        
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        
        CheckColumnConfig checkColumn = new CheckColumnConfig("verified", "Verified", 55) {
            protected void onMouseDown(com.extjs.gxt.ui.client.event.GridEvent<com.extjs.gxt.ui.client.data.ModelData> ge) {
                super.onMouseDown(ge);
                
                saveChangesToServer((QaEntryModelGxt)ge.getModel());
            }
        };
        CellEditor checkBoxEditor = new CellEditor(new CheckBox());  
        checkColumn.setEditor(checkBoxEditor);  
        configs.add(checkColumn);  

        ColumnConfig column = new ColumnConfig();
        column.setId("item");
        column.setHeader("QA Item");
        column.setWidth(240);
        column.setSortable(true);
        configs.add(column);

        ColumnConfig desc = new ColumnConfig();
        desc.setId("description");
        desc.setHeader("Description");
        desc.setWidth(400);
        desc.setSortable(true);
        configs.add(desc);
        
        
        final EditorGrid<QaEntryModelGxt> grid = new EditorGrid<QaEntryModelGxt>(store, new ColumnModel(configs));
        grid.setAutoExpandColumn("item");
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModelExt>>() {
            public void handleEvent(final SelectionEvent<StudentModelExt> se) {
                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                }
            }
        });
        grid.setWidth("500px");
        grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);
        grid.addPlugin(checkColumn);

        return grid;
    }
    
    private void saveChangesToServer(final QaEntryModelGxt model) {

        new RetryAction<RpcData>() {
            
            @Override
            public void attempt() {
                QaEntryModel imodel = model.convertTo();
                SaveQaItemAction action = new SaveQaItemAction(CmQa.__userName,imodel.getItem(),imodel.isVerified());
                setAction(action);
                CmQa.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(RpcData data) {
                if(!data.getDataAsString("status").equals("OK")) {
                    Window.alert("Error updating QA item");
                }
            }
        }.register();       
    }
    
    
    private void getQaItems() {
        
        new RetryAction<CmList<QaEntryModel>>() {
            
            @Override
            public void attempt() {
                GetQaItemsAction action = new GetQaItemsAction(CmQa.__category);
                setAction(action);
                CmQa.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(CmList<QaEntryModel> items) {
                _grid.getStore().add(QaEntryModelGxt.convert(items));
            }
        }.register();       
    }
}


class QaEntryModelGxt extends BaseModel {
    
    public QaEntryModelGxt(String item, String description, Boolean verified) {
        set("item",item );
        set("description", description);
        set("verified", verified);
    }
    
    public <X extends Object> X set(String name, X value) {
        return super.set(name, value);
    }
    
    public QaEntryModel convertTo() {
        return new QaEntryModel((String)get("item"), (String)get("description"), (Boolean)get("verified"));
    }
    static List<QaEntryModelGxt> convert(CmList<QaEntryModel> fromServer) {
        List<QaEntryModelGxt> models = new ArrayList<QaEntryModelGxt>();
        for(int i=0,t=fromServer.size();i<t;i++) {
            QaEntryModel o = fromServer.get(i);
            models.add(new QaEntryModelGxt(o.getItem(), o.getDescription(), o.isVerified()));
        }
        return models;
    }
    
    
}