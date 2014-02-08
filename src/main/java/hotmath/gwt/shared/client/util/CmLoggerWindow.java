package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.QueueMessage;
import hotmath.gwt.shared.client.rpc.RetryActionManagerQueueWatcher;
import hotmath.gwt.shared.client.rpc.action.SaveCmLoggerTextAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;


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
    
    static private CmLoggerWindow __instance;
    static public CmLoggerWindow getInstance() {
        if(__instance == null)
            __instance = new CmLoggerWindow();
        return __instance;
    }
    
    TextField _filter = new TextField();
    List<QueueMessage> _messages = new ArrayList<QueueMessage>();
    boolean _follow=true;
    RetryActionManagerQueueWatcher _watcher;
    
    Grid<QueueMessage> _grid;
    
    
    interface LoggerProps extends PropertyAccess<String> {
        @Path("id")
        ModelKeyProvider<QueueMessage> key();
        ValueProvider<QueueMessage, String> timeStamp();
        ValueProvider<QueueMessage, String> message();
    }
    LoggerProps props = GWT.create(LoggerProps.class);
    
    private CmLoggerWindow() {
        super(false);
        
        setMaximizable(true);
        setModal(false);
        setHeadingText("Catchup Logger");
        setPixelSize(500,300);
        setPosition(0, 350);

        BorderLayoutContainer main = new BorderLayoutContainer();
        ToolBar tb = new ToolBar();
        
        ListStore<QueueMessage> store = new ListStore<QueueMessage>(props.key());
        _grid = defineGrid(store, defineColumns());

        setWidget(main);
        main.setCenterWidget(_grid);
        
        final ToggleButton btn = new ToggleButton("Follow");
        btn.setToolTip("Should the focus follow new log message?");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _follow = btn.getValue();
            }
        });
        addButton(btn);
        
        _filter.setWidth(150);
        _filter.setToolTip("Filter text");
        _filter.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                filterAllMessages();
            }
        });
        tb.add(_filter);
        getHeader().addTool(new TextButton("Clear", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
               clearLog();
            } 
        }));
        main.setNorthWidget(tb, new BorderLayoutData(30));
        
        addButton(new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();
            }
        }));
        
        
        getHeader().addTool(new TextButton("Save", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                String msgs = "";
                for(int i=0,t=_grid.getStore().size();i<t;i++) {
                    QueueMessage msg = _grid.getStore().get(i);
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
        
        /** 
        getHeader().addTool(new TextButton("Queue Watcher", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                if(_watcher == null)
                    _watcher = new RetryActionManagerQueueWatcher();
                //_watcher.setVisible(true);
            }
        }));
        */
    }
    
    public void clearLog() {
        _messages.clear();
        _grid.getStore().clear();
    }
    
    private Grid<QueueMessage> defineGrid(final ListStore<QueueMessage> store, ColumnModel<QueueMessage> cm) {
        final Grid<QueueMessage> grid = new Grid<QueueMessage>(store, cm);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getView().setAutoFill(true);
        grid.getView().setAutoExpandColumn(cm.getColumn(1));
        return grid;
    }
    
    private ColumnModel<QueueMessage> defineColumns() {
        List<ColumnConfig<QueueMessage,?>> configs = new ArrayList<ColumnConfig<QueueMessage, ?>>();

        configs.add(new ColumnConfig<QueueMessage, String>(props.timeStamp(), 40, "Time"));
        ColumnConfig<QueueMessage, String> msgCol = new ColumnConfig<QueueMessage, String>(props.message(), 140, "Message");
        msgCol.setCell(new AbstractCell<String>() {
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
                sb.appendHtmlConstant(value);
            }
        });
        configs.add(msgCol);

        ColumnModel<QueueMessage> cm = new ColumnModel<QueueMessage>(configs);
        return cm;
    }    
    
    public void filterAllMessages() {
        setVisible(true);
        String f = _filter.getCurrentValue();
        
        _grid.getStore().clear();
        
        for(int i=0,t=_messages.size();i<t;i++) {
            QueueMessage message = _messages.get(i);
            String s = message.getMessage();
            if(f == null || f.length() == 0 || s.indexOf(f) > -1) {
                _grid.getStore().add(message);        
            }
        }
    }
    
    
    public void _info(String msg) {
        if(!isVisible())
            return;
        
        _messages.add(new QueueMessage(msg));
        
        String fv = _filter.getValue();
        if((fv == null || fv.length() == 0) ||
           msg.indexOf(fv) > -1) {
           _grid.getStore().add(new QueueMessage(msg));
        }
        
        if(_follow)
            _grid.getView().focusRow(_grid.getStore().size()-1);
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
