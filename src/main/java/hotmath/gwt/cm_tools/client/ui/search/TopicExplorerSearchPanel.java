package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_tools.client.ui.ShowWhiteboardWindow;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.CalculatorWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class TopicExplorerSearchPanel extends ContentPanel {

    SearchPanel _searchPanel = new SearchPanel();
    public TopicExplorerSearchPanel() {
        
        setWidget(_searchPanel);
        
//        addTool(new TextButton("Show Whiteboard", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//                ShowWhiteboardWindow.getInstance().setVisible(true);
//            }
//        }));
//        addTool(new TextButton("Calculator", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//                CalculatorWindow.getInstance().setVisible(true);
//            }
//        }));
        

        setVisible(true);
        
      
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                _searchPanel.setInputFocus();
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
                new TopicExplorerSearchPanel();
                //new TopicExplorerManager().exploreTopic(topic);
            }
        });
    }


    public void setFocus() {
        _searchPanel.setInputFocus();
    }
}
