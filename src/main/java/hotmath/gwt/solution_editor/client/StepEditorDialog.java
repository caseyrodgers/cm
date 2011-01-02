package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_core.client.EventTypes;
import hotmath.gwt.solution_editor.client.SolutionResourceListDialog.Callback;
import hotmath.gwt.solution_editor.client.rpc.FormatXmlAdminAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionAdminResponse;
import hotmath.gwt.solution_editor.client.rpc.SolutionResource;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StepEditorDialog extends Window {
    
    TinyMCE _tinyMce;
    TextArea _textArea;
    TabPanel _tabPanel;
    
    public StepEditorDialog(final StepUnitItem item) {
        _tabPanel = new TabPanel();
        
        _tinyMce = new TinyMCE(700,27);
        _tinyMce.setText(item.getEditorText());
        setLayout(new FitLayout());
        
        _textArea = new TextArea();
        _textArea.setValue(item.getEditorText());
        TabItem tItem = new TabItem("HTML");
        tItem.add(_textArea);
        tItem.setLayout(new FitLayout());
        _tabPanel.add(tItem);

        
        tItem = new TabItem("Rich Text");
        tItem.add(_tinyMce);
        _tabPanel.add(tItem);
        
        setSize(700,550);
        setScrollMode(Scroll.AUTO);
        setResizable(true);
        setMaximizable(true);
        setAnimCollapse(true);
        setModal(true);
        
        
        _tabPanel.addListener(Events.Select, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if(_tabPanel.getSelectedItem().getText().equals("HTML")) {
                    String value = _tinyMce.getText();
                    if(value != null) {
                        _textArea.setValue(value);
                    }
                }
                else {
                    String value = _textArea.getValue();
                    if(value != null) {
                        _tinyMce.setText(value);
                        _tinyMce.updateContent(_tinyMce.getID());
                    }
                }
            }
        });
        
        add(_tabPanel);

        getHeader().addTool(new Button("Insert Resource",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new SolutionResourceListDialog(new Callback() {
                    @Override
                    public void resourceSelected(SolutionResource resource) {
                        String toInsert = "<img class='sol_resource' src='" + resource.getUrlPath() + "'/>";
                        

                        if(_tabPanel.getSelectedItem().getText().equals("HTML")) {
                            int index = _textArea.getCursorPos();
                            String text = _textArea.getValue();
                            text = (text != null?text:"");
                            _textArea.setValue(text.substring(0, index) + toInsert + text.substring(index));                            
                        }
                        else {
                            _tinyMce.insertText(toInsert);
                        }
                        layout();
                        
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
                if(_tabPanel.getSelectedItem().getText().equals("HTML")) {
                    value = _textArea.getValue();
                }
                else {
                    value = _tinyMce.getText();
                }
                item.setEditorText(value);
                hide();
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.POST_SOLUTION_LOAD));                
            }
        }));

        addButton(new Button("Close",new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        setVisible(true);
        
        setFocusWidget(_tinyMce);
        focus();
        
        layout();
    }
    
    private void formatXml() {
        FormatXmlAdminAction action = new FormatXmlAdminAction(_tinyMce.getText());
        SolutionEditor._status.setBusy("Formatting XML ...");
        SolutionEditor.getCmService().execute(action, new AsyncCallback<SolutionAdminResponse>() {
            public void onSuccess(SolutionAdminResponse solutionResponse) {
                _tinyMce.setText(solutionResponse.getXml());
                SolutionEditor._status.clearStatus("");
            }
            @Override
            public void onFailure(Throwable arg0) {
                SolutionEditor._status.clearStatus("");
                arg0.printStackTrace();
                com.google.gwt.user.client.Window.alert(arg0.getLocalizedMessage());
            }
        });
    }    
    
}
