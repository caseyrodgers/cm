package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class StepEditorDefineDialog extends Window {

    HtmlEditorApplet _textArea;

    public StepEditorDefineDialog(final String tutorDefine) {
        setLayout(new FitLayout());

        _textArea = new HtmlEditorApplet();
        add(_textArea);

        setSize(700, 550);
        setScrollMode(Scroll.AUTO);
        setResizable(true);
        setMaximizable(true);
        setAnimCollapse(true);
        setDraggable(false);
        setModal(true);
        _textArea.setValue(tutorDefine);
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

        layout();
    }
}
