package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class RetryActionManagerQueueWatcher extends CmWindow {

    Grid<QueueMessage> _grid;
        
    public RetryActionManagerQueueWatcher() {
        setHeading("CM Request Queue Watcher");
        setSize(200,300);
        setPosition(0, 400);
    
        setLayout(new FitLayout());
        ListStore<QueueMessage> store = new ListStore<QueueMessage>();
        _grid = defineGrid(store, defineColumns());
        add(_grid);
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));
        
        getHeader().addTool(new Button("Show Log", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                CmLogger.getInstance().filterAllMessages();
            }
        }));


        new com.google.gwt.user.client.Timer() {
            @Override
            public void run() {
                updateDisplay();
            }
        }.scheduleRepeating(2000);
        
        setVisible(true);
    }
    
    
    public void updateDisplay() {
        _grid.getStore().removeAll();
        for(RetryAction ra: RetryActionManager.getInstance().getQueue()) {
            _grid.getStore().add(new QueueMessage(ra.toString()));
        }
    }
    
    private Grid<QueueMessage> defineGrid(final ListStore<QueueMessage> store, ColumnModel cm) {
        final Grid<QueueMessage> grid = new Grid<QueueMessage>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.setWidth("300px");
        grid.setHeight("100%");
        grid.setStateful(true);
        grid.setLoadMask(true);
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
        column.setWidth(235);
        column.setSortable(true);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
}


