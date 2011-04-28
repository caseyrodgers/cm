package hotmath.gwt.cm_test.client;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class CatchupMathTest implements EntryPoint {
    
    
 public void onModuleLoad() {
        
        LayoutContainer main = new LayoutContainer();
        main.setLayout(new FitLayout());
        
        TabPanel tabPanel = new TabPanel();
        
        TabItem tabItem = new TabItem("TEST");
        tabItem.setLayout(new FitLayout());        
        ListView<MyModel> listView = new ListView<MyModel>();
        listView.setStore(createListStore());

        tabItem.add(listView);
        tabPanel.add(tabItem);
        
        Viewport viewPort = new Viewport();
        viewPort.setLayout(new FitLayout());
        viewPort.add(tabPanel);
        
        RootPanel.get().add(viewPort);
    }

    
    private ListStore<MyModel> createListStore() {
        ListStore<MyModel> myStore = new ListStore<MyModel>();
        for(int i=0;i<100;i++) {
            myStore.add(new MyModel("Test: " + i));
        }
        return myStore;
    }
}


class MyModel extends BaseModel {
    public MyModel(String data) {
        set("text", data);
    }
}

