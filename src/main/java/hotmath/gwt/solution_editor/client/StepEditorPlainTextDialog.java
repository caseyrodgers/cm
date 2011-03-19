package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.solution_editor.client.SolutionResourceListDialog.Callback;
import hotmath.gwt.solution_editor.client.rpc.FormatXmlAdminAction;
import hotmath.gwt.solution_editor.client.rpc.MathMlResource;
import hotmath.gwt.solution_editor.client.rpc.SolutionAdminResponse;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Encapsulates the Java Plugin editor used to allowing plain editing.
 * 
 * The Java-2-Javascript communication works reliably. However, the
 * Javascript-2-Java communication is very flaky. So, we have work around these
 * issues by using a 'post-office' type of pattern.
 * 
 * Where we set a value, load the applet ... the applet then reads the value
 * from JavaScript. When text is saved using button on the Java app. The JS side
 * will save the correct value and close window.
 * 
 * 
 * TODO: get reliable way to do JS-2Java.
 * 
 * @author casey
 * 
 */
public class StepEditorPlainTextDialog extends Window {

    HtmlEditorApplet _textArea;

    public StepEditorPlainTextDialog(final StepUnitItem item) {
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
        _textArea.setValue(item.getEditorText());
        _textArea.setCallback(new HtmlEditorApplet.Callback() {

            @Override
            public void saveAndCloseWindow(String text) {
                item.setEditorText(text);

                text = item.getEditorText();
                hide();
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.POST_SOLUTION_LOAD));
                EventBus.getInstance().fireEvent(
                        new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));
            }
        });

        getHeader().addTool(new Button("MathML Editor", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                setVisible(false);
                new MathMlEditorDialog(new MathMlEditorDialog.Callback() {
                    @Override
                    public void resourceUpdated(MathMlResource resource) {
                        setVisible(true);
                    }
                }, null, false);            }
        }));

        getHeader().addTool(new Button("Resources", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                setVisible(false);
                new SolutionResourceListDialog(new Callback() {
                    @Override
                    public void resourceSelected(SolutionResource resource) {
                        setVisible(true);
                    }
                }, SolutionEditor.__pidToLoad);
            }
        }));


        setVisible(true);

        focus();

        layout();
    }

    
    private String getWidgetDefinitionJson(String html) {
        String startToken = "<div style='display: none' id='hm_flash_object'>";
        String endToken = "</div></div>";
        int startPos = html.indexOf(startToken);
        if(startPos > -1) {
            /** extract the widget HTML */
            int endPos = html.indexOf(endToken,startPos);
            String h1 = html.substring(0, startPos);
            String h2 = html.substring((endPos + 6));  // just first div
            html = h1 + h2;
        }
        return html;
    }   
    
    private void formatXml() {
        FormatXmlAdminAction action = new FormatXmlAdminAction(_textArea.getValue());
        SolutionEditor.__status.setBusy("Formatting XML ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                _textArea.setValue(solutionResponse.getXml());
                SolutionEditor.__status.clearStatus("");
            }

            @Override
            public void onFailure(Throwable arg0) {
                SolutionEditor.__status.clearStatus("");
                arg0.printStackTrace();
                com.google.gwt.user.client.Window.alert(arg0.getLocalizedMessage());
            }
        });
    }

}
