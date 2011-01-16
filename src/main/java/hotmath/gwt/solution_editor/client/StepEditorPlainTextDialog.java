package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.solution_editor.client.SolutionResourceListDialog.Callback;
import hotmath.gwt.solution_editor.client.rpc.FormatXmlAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionAdminResponse;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Encapsulates the Java Plugin editor used to 
 *  allowing plain editing.  
 *  
 *  The Java-2-Javascript communication works reliably.  However,
 *  the Javascript-2-Java communication is very flaky.  So, we have
 *  work around these issues by using a 'post-office' type of pattern.
 *  
 *  Where we set a value, load the applet ... the applet then reads the value
 *  from JavaScript.  When text is saved using button on the Java app.  The 
 *  JS side will save the correct value and close window.
 *     
 *  
 *  TODO: get reliable way to do JS-2Java.
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
        
        setSize(700,550);
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
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));                
            }
        });
        
        getHeader().addTool(new Button("Resources",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new SolutionResourceListDialog(new Callback() {
                    @Override
                    public void resourceSelected(SolutionResource resource) {
//                        String toInsert = "<img class='sol_resource' src='" + resource.getUrlPath() + "'/>";
//                        
//
//                        if(_tabPanel.getSelectedItem().getText().equals("HTML")) {
//                            // int index = _textArea.getCursorPos();
//                            String text = _textArea.getValue();
//                            text = (text != null?text:"");
//                            //_textArea.setText(text.substring(0, index) + toInsert + text.substring(index));                            
//                        }
//                        else {
//                            //_tinyMce.insertText(toInsert);
//                        }
//                        layout();
//                        
//                        
//                        EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));       
                    }
                },SolutionEditor.__pidToLoad);
            }
        }));
        
//        getHeader().addTool(new Button("Insert Widget",new SelectionListener<ButtonEvent>() {
//            @Override
//            public void componentSelected(ButtonEvent ce) {
//                WidgetListDialog.showWidgetListDialog(new WidgetListDialog.Callback() {
//                    @Override
//                    public void resourceSelected(WidgetDefModel resource) {
//                        String toInsert = resource.getJson();
//                        
//                        if(_tabPanel.getSelectedItem().getText().equals("HTML")) {
//                            int index = _textArea.getCursorPos();
//                            String text = _textArea.getValue();
//                            text = (text != null?text:"");
//                            _textArea.setValue(text.substring(0, index) + toInsert + text.substring(index));                            
//                        }
//                        else {
//                            _tinyMce.insertText(toInsert);
//                        }
//                        layout();
//                    }
//                });
//            }
//        }));        
//        getHeader().addTool(new Button("Format", new SelectionListener<ButtonEvent>() {
//            @Override
//            public void componentSelected(ButtonEvent ce) {
//                formatXml();
//            }
//        }));    
        
        setVisible(true);
        
        //setFocusWidget(_tinyMce);
        focus();
        
        layout();
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
