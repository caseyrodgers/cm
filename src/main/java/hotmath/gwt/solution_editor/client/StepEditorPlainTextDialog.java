package hotmath.gwt.solution_editor.client;


import hotmath.gwt.cm_rpc.client.model.SolutionAdminResponse;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.solution_editor.client.rpc.FormatXmlAdminAction;
import hotmath.gwt.solution_editor.client.rpc.MathMlResource;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

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
public class StepEditorPlainTextDialog extends GWindow {

    TextArea _textArea;
	private EditCallback callback;

    public interface EditCallback {
        
    	String getTextToEdit();
    	void saveTextToEdit(String editedText);
    }
    
    public StepEditorPlainTextDialog(EditCallback callbackIn) {
        super(false);
        
    	this.callback = callbackIn;

        _textArea = new TextArea(); // new HtmlEditorApplet();
        _textArea.getElement().setId("solution-editor-area");
        setWidget(_textArea);
        setPixelSize(700, 390);
        setResizable(false);
        setMaximizable(false);
        setAnimCollapse(true);
        setDraggable(false);
        setModal(true);
        _textArea.setValue(callback.getTextToEdit());
        
        setHeadingText("Text Editor (F11 for full screen toggle)");
        
        getHeader().addTool(new TextButton("Format", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
				formatXml();
			}
		}));
        
        addButton(new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
        		String text = getTextEditorValue();
        		callback.saveTextToEdit(text);
        		hide();
        	}
		}));
        
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
				hide();
			}
		}));


        getHeader().addTool(new TextButton("MathML Editor", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                setVisible(false);
                new MathMlEditorDialog(new MathMlEditorDialog.Callback() {
                    @Override
                    public void resourceUpdated(MathMlResource resource) {
                        setVisible(true);
                    }
                }, null, false);            }
        }));

        getHeader().addTool(new TextButton("Resources", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                setVisible(false);
                new SolutionResourceListDialog(new SolutionResourceListDialog.Callback() {
                    @Override
                    public void resourceSelected(SolutionResource resource) {
                        setVisible(true);
                    }
                }, SolutionEditor.__pidToLoad);
            }
        }));

        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				jsni_setupJavascriptEditor();
			}
		});
        

        setVisible(true);

        focus();

        forceLayout();
    }
    
    public String getTextEditorValue() {
    	return jsni_getTextEditorValue();
    }

    public void setTextEditorValue(String text) {
    	jsni_setTextEditorValue(text);
    }
    
    
    native private void jsni_setTextEditorValue(String text) /*-{
        $wnd._theCodeMirror.setValue(text);
    }-*/;

	native private String jsni_getTextEditorValue() /*-{
        return $wnd._theCodeMirror.getValue();
    }-*/;

	native protected void jsni_setupJavascriptEditor() /*-{
        var myTextArea = $doc.getElementById("solution-editor-area");
        if(myTextArea == null) {
            alert('solution-editor-area was not found!');
            return;
        }
       $wnd._theCodeMirror = $wnd.CodeMirror.fromTextArea(myTextArea, {smartIndent: false, electricChars: false,extraKeys: {"F11": function(cm) {cm.setOption("fullScreen", !cm.getOption("fullScreen"));},"EscNOT": function(cm) {if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);}}});
       //$wnd.__theCodeMirror.setSize('100%', '100%');
    }-*/;


	private String getWidgetDefinitionJson(String html) {
        String startToken = "<div style='display: none' name='hm_flash_object'>";
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
        FormatXmlAdminAction action = new FormatXmlAdminAction(getTextEditorValue());
        SolutionEditor.__status.setBusy("Formatting XML ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                setTextEditorValue(solutionResponse.getXml());
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
