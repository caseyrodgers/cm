package hotmath.gwt.cm_admin.client.custom_content.problem;

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


    public CKEditorPanel(String id, final String text) {
        _textArea = new TextArea();
        _id = id;
        _textArea.setId(_id);
        setWidget(_textArea);
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                jsni_setupCkeditor(_id, text);
            }
        });
    }

    native public void setEditorValue(String text) /*-{
        $wnd.CKEDITOR.instances[id].setData(text);
    }-*/;

    native private void jsni_setupCkeditor(String id, String text) /*-{
        $wnd.CKEDITOR.replace( id,{__customConfig : '','height' : '170'});
        $wnd.CKEDITOR.instances[id].setData(text);
    }-*/;
    

    native public String jsni_getEditorValue(String id) /*-{
        return $wnd.CKEDITOR.instances[id].getData();
    }-*/;

    public String getEditorValue() {
        return jsni_getEditorValue(_id);
    }
}
