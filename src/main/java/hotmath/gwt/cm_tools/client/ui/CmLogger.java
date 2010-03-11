package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.rpc.QueueMessage;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class CmLogger extends CmWindow {
    
    static private CmLogger __instance;
    static public CmLogger getInstance() {
        if(__instance == null)
            __instance = new CmLogger();
        return __instance;
    }
    
    TextField<String> _filter = new TextField<String>();
    List<String> _messages = new ArrayList<String>();
    
    Grid<QueueMessage> _grid;
    TextArea _textArea = new TextArea();
    public CmLogger() {
        setHeading("Catchup Logger");
        setSize(500,300);
        setPosition(0, 450);
        setLayout(new FitLayout());
        setScrollMode(Scroll.AUTO);
        setStyleAttribute("z-index", "100");
        ToolBar tb = new ToolBar();
        
        ListStore<QueueMessage> store = new ListStore<QueueMessage>();
        _grid = defineGrid(store, defineColumns());

        TabPanel tp = new TabPanel();
        TabItem ti = new TabItem("Grid");
        ti.setLayout(new FitLayout());
        ti.add(_grid);
        tp.add(ti);
        
        
        
        ti = new TabItem("Text");
        ti.addListener(Events.Select, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                String msgs = "";
                for(int i=0,t=_messages.size();i<t;i++) {
                    msgs += _messages + "\n";
                }
                _textArea.setValue(msgs);
                
            }
        });
        ti.setLayout(new FitLayout());
        ti.add(_textArea);
        tp.add(ti);
        
        
        add(tp);
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
               _messages.clear();
               _textArea.clear();
               _grid.getStore().removeAll();
            } 
        }));
        setTopComponent(tb);
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));
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
    
    static public void info(String msg) {
        getInstance()._info(msg);
    }
    static public void debug(String msg) {
        getInstance()._debug(msg);
    }
    static public void error(String msg,Throwable th) {
        getInstance()._error(msg, th);
    }
    
    
    private void _info(String msg) {
        _messages.add(msg);
        
        if(!isVisible())
            return;
        
        String fv = _filter.getValue();
        if((fv == null || fv.length() == 0) ||
           msg.indexOf(fv) > -1) {
           _grid.getStore().add(new QueueMessage(msg));
        }
        
        _grid.getView().focusRow(_grid.getStore().getCount()-1);
    }
    
    private void _error(String msg, Throwable th) {
        _info(msg + "\n" + th.getStackTrace());
    }
    
    private void _debug(String msg) {
        _info(msg);
    }
}
