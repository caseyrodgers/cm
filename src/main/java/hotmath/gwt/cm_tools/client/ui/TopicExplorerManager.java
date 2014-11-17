package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class TopicExplorerManager extends GWindow {
    
    static private TopicExplorerManager __instance;
    static public TopicExplorerManager getInstance() {
        if(__instance == null) {
            __instance = new TopicExplorerManager();
        }
        return __instance;
    }

    TabPanel _tabPanel = new TabPanel();
    SearchPanel _searchPanel = new SearchPanel();
    private TopicExplorerManager() {
        super(true);
        
        setPixelSize(850, 650);
        setHeadingText("Catchup Math Topic Explorer");
        setMaximizable(true);
        
        setModal(false);
        
        // _tabPanel.add(_searchPanel, new TabItemConfig("Search",  false));
        setWidget(_tabPanel);
        
        addTool(new TextButton("Search", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new SearchPanel().showWindow();
            }
        }));
        
        setVisible(true);
    }
    
    
    public void exploreTopic(Topic topic) {
        Widget panel = new TopicExplorer(topic).asWidget();
        _tabPanel.add(panel, new TabItemConfig(topic.getName(),  true));
        _tabPanel.setActiveWidget(panel);
        setVisible(true);
    }


    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                Topic topic = new Topic("Graphing Square Root Functions", "topics/graphing-square-root-functions.html",null);
                new TopicExplorerManager().exploreTopic(topic);
            }
        });
    }

}
