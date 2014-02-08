package hotmath.gwt.shared.client.rpc;



/** Watches the RetryRequestMananagers queue of 
 * RetryActions.   Is the pool is backed up, then
 * something is running too long and might crash
 * the entire app.
 * 
 * @author casey
 *
 */
public class RetryActionManagerQueueWatcher  {

//    Grid<QueueMessage> _grid;
//    Label _countLab = new Label();
//        
//    public RetryActionManagerQueueWatcher() {
//        super(true);
//        
//        BorderLayoutContainer main = new BorderLayoutContainer();
//        setHeadingText("CM Request Queue Watcher");
//        setPixelSize(300,250);
//        setPosition(0, 200);
//        
//        
//        ListStore<QueueMessage> store = new ListStore<QueueMessage>();
//        _grid = defineGrid(store, defineColumns());
//        add(_grid,new BorderLayoutData(LayoutRegion.CENTER));
//        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
//            @Override
//            public void componentSelected(ButtonEvent ce) {
//                close();
//            }
//        }));
//
//        add(_countLab,new BorderLayoutData(LayoutRegion.SOUTH,20));
//        
//        new com.google.gwt.user.client.Timer() {
//            @Override
//            public void run() {
//                updateDisplay();
//            }
//        }.scheduleRepeating(2000);
//        
//        setVisible(true);
//    }
//    
//    
//    public void updateDisplay() {
//        _grid.getStore().removeAll();
//        for(RetryAction ra: RetryActionManager.getInstance().getQueue()) {
//            String msg = (String)(ra.getAction() != null?ra.getAction().toString():ra.toString());
//            
//            
//            _grid.getStore().add(new QueueMessage(msg));
//        }
//        _countLab.setText(("Count: " + _grid.getStore().getCount()));
//    }
//    
//    private Grid<QueueMessage> defineGrid(final ListStore<QueueMessage> store, ColumnModel cm) {
//        final Grid<QueueMessage> grid = new Grid<QueueMessage>(store, cm);
//        grid.setBorders(true);
//        grid.setStripeRows(true);
//        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        grid.getSelectionModel().setFiresEvents(true);
//        grid.setWidth("400px");
//        grid.setHeight("100%");
//        grid.setStateful(true);
//        grid.setLoadMask(true);
//        return grid;
//    }
//    
//    private ColumnModel defineColumns() {
//        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
//
//        ColumnConfig column = new ColumnConfig();
//        column.setId("time_stamp");
//        column.setHeader("Time");
//        column.setWidth(50);
//        column.setSortable(true);
//        configs.add(column);
//        
//        column = new ColumnConfig();
//        column.setId("log_message");
//        column.setHeader("Log Message");
//        column.setWidth(350);
//        column.setSortable(true);
//        configs.add(column);
//
//        ColumnModel cm = new ColumnModel(configs);
//        return cm;
//    }
}


