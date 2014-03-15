package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Allow editing a text area with two tools:
 * 
 *   a rich text editor and the whiteboard.
 *   
 *    Each may be empty
 *   
 * @author casey
 *
 */
public class CpEditingArea extends GWindow {

    ShowWorkPanel2 _showWork;
    CKEditorPanel ckEditorPanel;
    BorderLayoutContainer _main;
    String _editorKey;
    private String _wbKey;
    
    interface Callback {
        void editingComplete(String partText);
    }
    Callback _callback;
    public CpEditingArea(String editKey, String text, Callback callback) {
        
        super(false);
        this._callback = callback;
        setPixelSize(800,  600);
        setHeadingText("Edit Solution Information");
        
        _main = new BorderLayoutContainer();
        
        _editorKey = "ckedit_id_" + editKey.trim(); 
        _wbKey = "wb_" + _editorKey;
        
        
        AreaData areaData = extractAreaData(text);
        
        buildUi(areaData);
        
        
        addButton(new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSave();
            }
        }));
        addCloseButton();
        
        setWidget(_main);
        
        setVisible(true); 
    }
    
    private AreaData extractAreaData(String text) {
        String checkFor = "<div class='wb_json'>";
        int p = text.indexOf(checkFor);
        String wbJson = null;
        String textPart = null;
        if(p > -1) {
            textPart = text.substring(0, p);
            wbJson = text.substring(p);
            wbJson = wbJson.substring(checkFor.length(), wbJson.indexOf("</div>"));
        }
        else {
            textPart = text;
        }
        AreaData aData = new AreaData(textPart,wbJson);
        return aData;
    }

    class AreaData {
        String textPart;
        String wbJson;
        
        public AreaData(String textPart, String whiteboardJson) {
            this.textPart = textPart;
            this.wbJson = whiteboardJson;
        }
        
    }
    
    
    private void doSave() {
        String partText = 
                "<div class='step_part'>" + ckEditorPanel.getEditorValue() + 
                "   <div class='wb_json'>" + _showWork.getWhiteboardCommandsAsJson() + "</div>" +
                "</div>";
        _callback.editingComplete(partText);
        hide();
    }
    
    private void buildUi(final AreaData areaData) {
        ckEditorPanel = new CKEditorPanel(_editorKey,areaData.textPart);
        
        
        _showWork = new ShowWorkPanel2(new ShowWorkPanelCallbackDefault() {
            
            @Override
            public void windowResized() {
            }
            
            @Override
            public void showWorkIsReady(ShowWorkPanel2 showWork) {
                if(areaData.wbJson != null) {
                    showWork.loadWhiteboardFromJson(areaData.wbJson);
                }
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return null;
            }
        },true,true,_wbKey,290, getWidget());
        
        _main.setSouthWidget(_showWork,new BorderLayoutData(290));
        _main.setCenterWidget(ckEditorPanel);
    }


    public String getCurrentValue() {
        return null;
    }

}
