package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;


/** Simple wrapper for using CkEditor WYSIWYG editor
 * 
 * @author casey
 *
 */
public class CKEditorPanel extends FlowPanel {
    
    private TextArea _textArea;
    private String _id;


    CallbackOnComplete callback;
    public CKEditorPanel(String id, final int height, final String text, final CallbackOnComplete callback) {
        _textArea = new TextArea();
        _id = id;
        this.callback = callback;
        _textArea.setId(_id);
        add(_textArea);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                jsni_setupCkeditor(_id, height,text);
            }
        });
    }
    
    private void gwt_WirisSetFrameSource(String source) {
    	new WirisEditorDialog(source);
    }

    
    private void ckeditorIsReady() {
        callback.isComplete();
    }

    native public void setEditorValue(String text) /*-{
        $wnd.CKEDITOR.instances[id].setData(text);
    }-*/;

    native private void jsni_setupCkeditor(String id, int height, String text) /*-{
        var that = this;
        $wnd.CKEDITOR.replace( id,
            {
                customConfig : '','height' : height
           });                
        $wnd.CKEDITOR.instances[id].setData(text);
        $wnd.CKEDITOR.on("instanceReady", function(event) {
            that.@hotmath.gwt.cm_admin.client.custom_content.problem.CKEditorPanel::ckeditorIsReady()();
        });
    }-*/;
    
    public void resizeEditor(int height) {
        jsni_resizeEditor(_id, height);
    }
    
    native private void jsni_resizeEditor(String id, int height) /*-{
        var o = $wnd.CKEDITOR.instances[id];
        if(!o) {
            alert('could not find CKEDITOR instance ' + id);
        }
        else {
            o.resize( '100%', height, false );
        }
    }-*/;

    native public String jsni_getEditorValue(String id) /*-{
        return $wnd.CKEDITOR.instances[id].getData();
    }-*/;

    public String getEditorValue() {
        return jsni_getEditorValue(_id);
    }

    native public void jsni_releaseResources(String id) /*-{
         $wnd.CKEDITOR.instances[id].destroy(false);
    }-*/;
    
    public void destroyEditor() {
        jsni_releaseResources(_id);
    }

    CenterLayoutContainer _editDisablePanel; 
	public void showClickToEdit(boolean forceShow) {
		
		if(!forceShow) {
			/** only show if CURRENTLY active */
			if(_editDisablePanel == null) {
				return;
			}
		}
		
		if(_editDisablePanel != null) {
			remove(_editDisablePanel);
		}
		_editDisablePanel = new CenterLayoutContainer();
		TextButton closeButton = new TextButton("Click to Edit", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				remove(_editDisablePanel);
				_editDisablePanel = null;
			}
		});
		_editDisablePanel.add(closeButton);
		int height = getElement().getClientHeight();
		int width = getElement().getClientWidth();
		
		_editDisablePanel.setHeight(height);
		_editDisablePanel.setWidth(width);
		_editDisablePanel.getElement().setAttribute("style",  "position: absolute;top: 0;left: 0;z-index: 999;opacity: .5;background: grey");
		add(_editDisablePanel);
	}
}
