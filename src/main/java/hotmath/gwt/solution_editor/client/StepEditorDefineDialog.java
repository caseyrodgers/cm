package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class StepEditorDefineDialog extends GWindow {

    HtmlEditorApplet _textArea;

    public StepEditorDefineDialog(final String tutorDefine) {


    	super(true);
    	
        setPixelSize(700, 550);
        setResizable(true);
        setMaximizable(true);
        setAnimCollapse(true);
        setDraggable(false);
        setModal(true);

        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setScrollMode(ScrollMode.AUTO);

        _textArea = new HtmlEditorApplet();
        flow.add(_textArea);
        setWidget(flow);
        
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                _textArea.setValue(tutorDefine);
            }
        });
        
        _textArea.setCallback(new HtmlEditorApplet.Callback() {

            @Override
            public void saveAndCloseWindow(String text) {
                hide();
                
                SolutionStepEditor.__instance.getMeta().setTutorDefine(text);
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));
            }
        });

        setVisible(true);
        focus();
    }
}
