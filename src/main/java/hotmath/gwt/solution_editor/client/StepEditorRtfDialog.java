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

public class StepEditorRtfDialog extends Window {
    
    TinyMCE _tinyMce;

    public StepEditorRtfDialog(final StepUnitItem item) {
        _tinyMce = new TinyMCE(700,27);
        setLayout(new FitLayout());
        
        setSize(700,550);
        setScrollMode(Scroll.AUTO);
        setResizable(true);
        setMaximizable(true);
        setAnimCollapse(true);
        setDraggable(false);
        setModal(true);

        String value = item.getEditorText();
        if(value != null) {
            _tinyMce.setText(value);
        }
        
        add(_tinyMce);

        getHeader().addTool(new Button("Resources",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new SolutionResourceListDialog(new Callback() {
                    @Override
                    public void resourceSelected(SolutionResource resource) {
                        String toInsert = "<img class='sol_resource' src='" + resource.getUrlPath() + "'/>";
                        _tinyMce.insertText(toInsert);

                        layout();
                        
                        EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));       
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
   

        getHeader().addTool(new Button("Format", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                formatXml();
            }
        }));    


        addButton(new Button("Save",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                String value = "";
                value = _tinyMce.getText();
                item.setEditorText(value);
                hide();
                
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.POST_SOLUTION_LOAD));
                EventBus.getInstance().fireEvent(new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));                
            }
        }));

        addButton(new Button("Close",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        setVisible(true);
        
        //setFocusWidget(_tinyMce);
        focus();
        
        layout();
    }
    
    private native void testEditor() /*-{
        var ed = $doc.getElementById('html_editor');
        alert(ed.getEditorText());
    }-*/;
    
    private native void testSetEditor(String text) /*-{
       var ed = $doc.getElementById('html_editor');
       ed.setEditorText(text);
}-*/;

    
    private void formatXml() {
        FormatXmlAdminAction action = new FormatXmlAdminAction(_tinyMce.getText());
        SolutionEditor.__status.setBusy("Formatting XML ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                _tinyMce.setText(solutionResponse.getXml());
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
