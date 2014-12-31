package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_tools.client.ui.TopicExplorer.Callback;

import com.google.gwt.user.client.ui.Widget;

public class TopicExplorerWindow extends GWindow {
    
    public TopicExplorerWindow(Topic topic, boolean isModal) {
        super(true);
        
        setPixelSize(700, 550);
        setHeadingText("Explore Lesson");
        setMaximizable(true);
        //setMinimizable(true);;
        setCollapsible(true);
        
        Widget panel = new TopicExplorer(topic, new Callback() {
            @Override
            public void resourceIsLoaded() {
                forceLayout();
            }
        }).asWidget();
        setWidget(panel);

        setModal(isModal);

        setVisible(true);
    }

    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                // Topic topic = new Topic("Graphing Square Root Functions", "topics/graphing-square-root-functions.html",null);
                Topic topic = new Topic("Multiplying Decimals","topics/multiplying-decimals.html", null);
                new TopicExplorerWindow(topic, true);
            }
        });
    }
}
