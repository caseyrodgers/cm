package hotmath.gwt.cm_qa.client;

import hotmath.gwt.cm_rpc.client.model.QaEntryModel;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetQaItemsAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQaItemAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveQaItemProblemAction;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;

public class CmQaBody extends ContentPanel {
    
    static CmQaBody __instance;
    static boolean __isReady;
    Grid<QaEntryModelGxt> _grid;
    public CmQaBody() {
        
        __instance = this;
        
        ListStore<QaEntryModelGxt> store = new ListStore<QaEntryModelGxt>();
        _grid = defineGrid(store);
        
        setLayout(new FitLayout());
        add(_grid);
        getQaItems();
    }
    
    
    private Grid<QaEntryModelGxt> defineGrid(final ListStore<QaEntryModelGxt> store) {
        
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        
        CheckColumnConfig verifiedColumn = new CheckColumnConfig("verified", "Verified", 55);
        CellEditor checkBoxEditor = new CellEditor(new CheckBox());  
        verifiedColumn.setEditor(checkBoxEditor);  
        configs.add(verifiedColumn);
        
        CheckColumnConfig problemColumn = new CheckColumnConfig("problem", "Problem", 55);
        
        CellEditor problemEditor = new CellEditor(new CheckBox());  
        problemColumn.setEditor(problemEditor);  
        configs.add(problemColumn);  
        
        ColumnConfig addProblemConfig = new ColumnConfig();
        addProblemConfig.setDataIndex("");
        addProblemConfig.setWidth(100);
        addProblemConfig.setRenderer(new GridCellRenderer<QaEntryModelGxt>() {
            @Override
            public Object render(final QaEntryModelGxt model, String property, ColumnData config, int rowIndex,int colIndex, ListStore<QaEntryModelGxt> store, Grid<QaEntryModelGxt> grid) {
                Button editButton = new Button("Problem", new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        
                        MessageBox.prompt("QA Item Problem", "Describe problem: ",true, new Listener<MessageBoxEvent>() {
                            @Override
                            public void handleEvent(MessageBoxEvent be) {
                                if(!be.isCancelled() && be.getValue() != null) {
                                    addProblemComment(model, be.getValue());
                                }
                            }
                        });
                    }
                });
                return editButton;
            }
        });
        configs.add(addProblemConfig);

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
                System.out.println("Test");
            }
        });
        grid.setWidth("500px");
        grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);
        grid.addPlugin(verifiedColumn);
        grid.addPlugin(problemColumn);
        
        
        grid.addListener(Events.CellMouseDown, new Listener<GridEvent>() {
            public void handleEvent(GridEvent e) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        saveChangesToServer(grid.getSelectionModel().getSelectedItem());
                    }
                });
            }
          });


        return grid;
    }
    
    private void addProblemComment(final QaEntryModelGxt model, final String problemText) {
        new RetryAction<RpcData>() {
            
            @Override
            public void attempt() {
                model.set("problem", Boolean.TRUE);
                model.set("verified", Boolean.FALSE);
                _grid.getStore().update(model);
                QaEntryModel imodel = model.convertTo();
                SaveQaItemProblemAction action = new SaveQaItemProblemAction(CmQa.__userName,imodel.getItem(),problemText);
                setAction(action);
                CmQa.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(RpcData data) {
                if(!data.getDataAsString("status").equals("OK")) {
                    Window.alert("Error adding problem record");
                }
            }
        }.register();       
    }
    
    protected void saveChangesToServer(final QaEntryModelGxt model) {

        new RetryAction<RpcData>() {
            
            @Override
            public void attempt() {
                QaEntryModel imodel = model.convertTo();
                SaveQaItemAction action = new SaveQaItemAction(CmQa.__userName,imodel.getItem(),imodel.isVerified(), imodel.isProblem());
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
    
    
    protected void getQaItems() {
        
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
                __isReady = true;
            }
        }.register();       
    }
}


class QaEntryModelGxt extends BaseModel {
    
    public QaEntryModelGxt(String item, String description, Boolean verified,Boolean problem) {
        set("item",item );
        set("description", description);
        set("verified", verified);
        set("problem", problem);
    }
    
    public <X extends Object> X set(String name, X value) {
        X x = super.set(name, value);
        return x;
    }
    
    public QaEntryModel convertTo() {
        return new QaEntryModel((String)get("item"), (String)get("description"), (Boolean)get("verified"), (Boolean)get("problem"));
    }
    static List<QaEntryModelGxt> convert(CmList<QaEntryModel> fromServer) {
        List<QaEntryModelGxt> models = new ArrayList<QaEntryModelGxt>();
        for(int i=0,t=fromServer.size();i<t;i++) {
            QaEntryModel o = fromServer.get(i);
            models.add(new QaEntryModelGxt(o.getItem(), o.getDescription(), o.isVerified(), o.isProblem()));
        }
        return models;
    }
    
    
}