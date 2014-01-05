package hotmath.gwt.cm_admin.client.custom_content.problem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class ProblemDesignerListDialog extends Composite {
    
    ContentPanel _main = new ContentPanel();
    public ProblemDesignerListDialog() {
        buildUI();
    }
    
    
    protected void buildUI() {
        
        _main.addTool(new TextButton("View Problem"));
        
        final WhiteboardRow whiteboardRow = new WhiteboardRow();
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(whiteboardRow);
        flow.add(new WidgetRowWidgetRow());
        _main.setWidget(flow);
        
        initWidget(_main);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                whiteboardRow.initializeWhiteboard();
            }
        });
    }

    class WidgetRowWidgetRow extends SimpleContainer {
        public WidgetRowWidgetRow() {
            setHeight(200);
            getElement().setAttribute("style",  "background: blue");
        }
    }


}
