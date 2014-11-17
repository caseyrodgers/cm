package hotmath.gwt.cm_tools.client.ui;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TopicExplorerGuiContext implements CmGuiDefinition {
    private Topic topic;
    SimplePanel _westPanel = new SimplePanel();

    public TopicExplorerGuiContext(Topic topic) {
        this.topic = topic;
    }

    @Override
    public Widget getWestWidget() {
        return _westPanel;
    }

    @Override
    public String getTitle() {
        return "The Title";
    }

    @Override
    public CmContext getContext() {
        return new CmContext() {

            @Override
            public void runAutoTest() {
            }

            @Override
            public void resetContext() {
            }

            @Override
            public List<Widget> getTools() {
                ArrayList<Widget> tools = new ArrayList<Widget>();
                //tools.add(new Label("This is the Label"));
                
                return tools;
            }

            @Override
            public String getStatusMessage() {
                return null;
            }

            @Override
            public String getContextTitle() {
                return "Context Title";
            }

            @Override
            public String getContextSubTitle() {
                return "Context Subtitle";
            }

            @Override
            public String getContextHelp() {
                return "Context help";
            }

            @Override
            public int getContextCompletionPercent() {
                return 0;
            }

            @Override
            public void doPrevious() {
                CmMessageBox.showAlert("Do Previous not implemented");
            }

            @Override
            public void doNext() {
                CmMessageBox.showAlert("Do Next not implemented");
            }
        };
    }

    @Override
    public Widget getCenterWidget() {
        return new Label("");
    }
}
