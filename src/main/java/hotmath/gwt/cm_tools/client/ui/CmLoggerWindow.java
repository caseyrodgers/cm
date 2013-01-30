package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.QueueMessage;
import hotmath.gwt.shared.client.rpc.RetryActionManagerQueueWatcher;
import hotmath.gwt.shared.client.rpc.action.SaveCmLoggerTextAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.UmbrellaException;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;


/** 
 * NO LONGER USED 
 * 
 * DELETE?
 * 
 * 
 * @author casey
 *
 */
public class CmLoggerWindow extends GWindow {
    
    static private CmLogger __instance;
    static public CmLogger getInstance() {
        if(__instance == null)
            __instance = new CmLogger();
        return __instance;
    }
    
    TextField<String> _filter = new TextField<String>();
    List<String> _messages = new ArrayList<String>();
    boolean _follow=true;
    RetryActionManagerQueueWatcher _watcher;
    
    Grid<QueueMessage> _grid;
    private CmLoggerWindow() {
        super(true);
        setHeadingText("Catchup Logger");
        setPixelSize(500,300);
        setPosition(0, 350);
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setScrollMode(ScrollMode.AUTO);
        ToolBar tb = new ToolBar();
        
        ListStore<QueueMessage> store = new ListStore<QueueMessage>();
        _grid = defineGrid(store, defineColumns());

        add(_grid);
        
        final ToggleButton btn = new ToggleButton("Follow");
        btn.setToolTip("Should the focus follow new log message?");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                _follow = btn.isPressed();
            }
        });
        addButton(btn);
        
        _filter.setWidth(150);
        _filter.setToolTip("Filter text");
        _filter.addKeyListener(new KeyListener() {
            @Override
            public void componentKeyUp(ComponentEvent event) {
                super.componentKeyUp(event);
                filterAllMessages();
            }
        });
        tb.add(_filter);
        getHeader().addTool(new Button("Clear", new SelectionListener<ButtonEvent>() {
           @Override
            public void componentSelected(ButtonEvent ce) {
               clearLog();
            } 
        }));
        add(tb);
        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));
        
        
        getHeader().addTool(new Button("Save", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                String msgs = "";
                for(int i=0,t=_grid.getStore().getCount();i<t;i++) {
                    QueueMessage msg = _grid.getStore().getAt(i);
                    msgs += msg.toString() + "\n\n";
                }
                SaveCmLoggerTextAction action = new SaveCmLoggerTextAction(UserInfo.getInstance().getUid(),msgs);
                CmShared.getCmService().execute(action,new CmAsyncCallback<RpcData>() {
                    public void onSuccess(RpcData result) {
                        clearLog();
                        CatchupMathTools.showAlert("Log messages saved on server");
                    }
                });
            }
        }));
        
        getHeader().addTool(new Button("Queue Watcher", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if(_watcher == null)
                    _watcher = new RetryActionManagerQueueWatcher();
                _watcher.setVisible(true);
            }
        }));
    }
    
    public void clearLog() {
        _messages.clear();
        _grid.getStore().removeAll();
    }
    
    private Grid<QueueMessage> defineGrid(final ListStore<QueueMessage> store, ColumnModel cm) {
        final Grid<QueueMessage> grid = new Grid<QueueMessage>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.setWidth("500px");
        grid.setHeight("100%");
        
        return grid;
    }
    
    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("time_stamp");
        column.setHeader("Time");
        column.setWidth(50);
        column.setSortable(true);
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("log_message");
        column.setHeader("Log Message");
        column.setWidth(450);
        column.setSortable(true);
        column.setRenderer(new GridCellRenderer<QueueMessage>() {
            @Override
            public Object render(QueueMessage model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<QueueMessage> store, Grid<QueueMessage> grid) {
                
                String msg = (String)model.get("log_message");
                Label l = new Label(msg);
                l.setToolTip(msg);
                return l; 
            }
        });
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }    
    
    public void filterAllMessages() {
        setVisible(true);
        String f = _filter.getValue();
        
        _grid.getStore().removeAll();
        
        for(int i=0,t=_messages.size();i<t;i++) {
            String s = _messages.get(i);
            if(f == null || f.length() == 0 || s.indexOf(f) > -1) {
                _grid.getStore().add(new QueueMessage(s));        
            }
        }
    }
    
    
    public void _info(String msg) {
        if(!isVisible())
            return;
        
        _messages.add(msg);
        
        String fv = _filter.getValue();
        if((fv == null || fv.length() == 0) ||
           msg.indexOf(fv) > -1) {
           _grid.getStore().add(new QueueMessage(msg));
        }
        
        if(_follow)
            _grid.getView().focusRow(_grid.getStore().getCount()-1);
    }
    
    public void _error(String msg, Throwable th) {
        _info(msg + "\n" + th.getStackTrace());
        
        if(th instanceof UmbrellaException) {
            _info("UmbrellaException: " + ((UmbrellaException)th).getCauses());
        }
    }
    
    public void _debug(String msg) {
        _info(msg);
    }
}
