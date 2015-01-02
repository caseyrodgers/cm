package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author bob
 *
 */

public class TopicExplorerPanel extends GWindow {
    
    static private TopicExplorerPanel __instance;
    static public TopicExplorerPanel getInstance() {
        if(__instance == null) {
            __instance = new TopicExplorerPanel();
        }
        return __instance;
    }

    static public boolean isInitialized() {
        return __instance != null;
    }
    
    private TopicExplorerPanel() {
        super(true);
        
        setPixelSize(950, 650);
        setMaximizable(true);
        setCollapsible(true);
        setModal(true);
    }
    
    public void exploreTopic(Topic topic) {
        Widget topicPanel = new TopicExplorer(topic, new TopicExplorerCallback() {
            @Override
            public void resourceIsLoaded() {
                forceLayout();
            }
        }).asWidget();
        setHeadingText(topic.getName());
        clear();
        add(topicPanel);
        setVisible(true);
        if (isCollapsed() == true) {
        	expand();
        }
    }

    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                Topic topic = new Topic("Graphing Square Root Functions", "topics/graphing-square-root-functions.html", null);
                new TopicExplorerPanel().exploreTopic(topic);
            }
        });
    }

}
