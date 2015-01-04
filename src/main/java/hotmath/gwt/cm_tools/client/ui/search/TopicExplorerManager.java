package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_core.client.event.CmQuizModeActivatedEvent;
import hotmath.gwt.cm_core.client.event.CmQuizModeActivatedEventHandler;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.ShowWhiteboardWindow;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
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
    
    SearchPanel _searchPanel = new SearchPanel();
    
    private TopicExplorerManager() {
        super(true);
        
        setPixelSize(800, 600);
        setHeadingText("Catchup Math Lesson Explorer");
        setMaximizable(true);
        //setMinimizable(true);;
        // setCollapsible(true);
        
        setModal(false);
        
        setWidget(_searchPanel);
        
        addTool(new TextButton("Show Whiteboard", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                ShowWhiteboardWindow.getInstance().setVisible(true);
            }
        }));
        
        
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
        setWidget(_searchPanel);
        forceLayout();
        setVisible(true);
    }
   

    public void exploreTopic(final Topic topic) {
        // Window.open("/loginServer?explore=" + topic.getFile(),"_new", "");
        Widget panel = new TopicExplorer(topic, new TopicExplorerCallback() {
            @Override
            public void resourceIsLoaded() {
                // update ui?
            }
        }).asWidget();
        
        
        BorderLayoutContainer blc = new BorderLayoutContainer();
        setWidget(panel);
        forceLayout();
        
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
