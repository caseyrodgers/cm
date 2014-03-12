package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;

import com.google.gwt.user.client.Window;
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
    public CpEditingArea(String editKey) {
        
        super(false);
        
        setPixelSize(800,  600);
        setHeadingText("Edit Solution Information");
        
        _main = new BorderLayoutContainer();
        
        _editorKey = "ckedit_id_" + editKey.trim(); 
        _wbKey = "wb_" + _editorKey;
        buildUi();
        
        
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
    
    
    private void doSave() {
        String textPart = ckEditorPanel.getEditorValue();
        String whiteboard = _showWork.getWhiteboardCommandsAsJson();
        
        Window.alert("Text: " + textPart + ", ' WB: " + whiteboard);
        
    }
    
    private void buildUi() {
        ckEditorPanel = new CKEditorPanel(_editorKey,"");
        
        
        _showWork = new ShowWorkPanel2(new ShowWorkPanelCallbackDefault() {
            
            @Override
            public void windowResized() {
            }
            
            @Override
            public void showWorkIsReady(ShowWorkPanel2 showWork) {
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return null;
            }
        },true,true,_wbKey,290, getWidget());
        
        _main.setSouthWidget(_showWork,new BorderLayoutData(290));
        _main.setCenterWidget(ckEditorPanel);
    }
    
    public void setValue(String hint) {
    }

    public String getCurrentValue() {
        return null;
    }

}
