package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sencha.gxt.widget.core.client.form.TextArea;


/** Simple wrapper for using CkEditor WYSIWYG editor
 * 
 * @author casey
 *
 */
public class CKEditorPanel extends SimplePanel {
    
    private TextArea _textArea;
    private String _id;


    CallbackOnComplete callback;
    public CKEditorPanel(String id, final String text, final CallbackOnComplete callback) {
        _textArea = new TextArea();
        _id = id;
        this.callback = callback;
        _textArea.setId(_id);
        setWidget(_textArea);
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                jsni_setupCkeditor(_id, text);
            }
        });
    }
    
    private void ckeditorIsReady() {
        callback.isComplete();
    }

    native public void setEditorValue(String text) /*-{
        $wnd.CKEDITOR.instances[id].setData(text);
    }-*/;

    native private void jsni_setupCkeditor(String id, String text) /*-{
        var that = this;
        $wnd.CKEDITOR.replace( id,{__customConfig : '','height' : '170'});
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
        if(o) {
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
}
