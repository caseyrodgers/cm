package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.event.CmQuizModeActivatedEvent;
import hotmath.gwt.cm_core.client.event.CmQuizModeActivatedEventHandler;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.ui.TopicExplorer.Callback;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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

    static public boolean isInitialized() {
        return __instance != null;
    }
    
    TabPanel _tabPanel = new TabPanel();
    SearchPanel _searchPanel = new SearchPanel();
    
    
    private TopicExplorerManager() {
        super(true);
        
        setPixelSize(800, 600);
        setHeadingText("Catchup Math Lesson Explorer");
        setMaximizable(true);
        //setMinimizable(true);;
        // setCollapsible(true);
        
        setModal(false);
        
        _tabPanel.add(_searchPanel, new TabItemConfig("Search",  false));
        setWidget(_searchPanel);
        
//        addTool(new TextButton("Search", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//                new SearchPanel().showWindow();
//            }
//        }));
        
        addTool(new TextButton("Close Search", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        setVisible(true);
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                _searchPanel._inputBox.focus();
            }
        });
        
        
        CmRpcCore.EVENT_BUS.addHandler(CmQuizModeActivatedEvent.TYPE, new CmQuizModeActivatedEventHandler() {
            @Override
            public void quizModeActivated(boolean yesNo) {
                
                if(yesNo) {
                    setVisible(false);
                }
                else {
                    // don't show automatically
                }
            }
        });
    }

    
    public void showSearch() {
        _tabPanel.setActiveWidget(_searchPanel);
        setVisible(true);
    }
   

    public void exploreTopic(final Topic topic) {
        // Window.open("/loginServer?explore=" + topic.getFile(),"_new", "");
        Widget panel = new TopicExplorer(topic, new Callback() {
            @Override
            public void resourceIsLoaded() {
                // update ui?
            }
        }).asWidget();
        _tabPanel.add(panel, new TabItemConfig(topic.getName(),  true));
        _tabPanel.setActiveWidget(panel);
        setVisible(true);
    }



    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                Topic topic = new Topic("Graphing Square Root Functions", "topics/graphing-square-root-functions.html",null);
                new TopicExplorerManager();
                //new TopicExplorerManager().exploreTopic(topic);
            }
        });
    }
}
