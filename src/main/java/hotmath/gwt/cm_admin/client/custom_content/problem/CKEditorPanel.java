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


    public CKEditorPanel(final String text) {
        _textArea = new TextArea();
        _textArea.setId("editor_div");
        setWidget(_textArea);
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                jsni_setupCkeditor(text);
            }
        });
    }
    
    public void setEditorValue(String text) {
        _textArea.setValue(text);
    }

    native private void jsni_setupCkeditor(String text) /*-{
        $wnd.CKEDITOR.replace('editor_div');
        
        $wnd.CKEDITOR.instances.editor_div.setData(text);
    }-*/;
    
    
    native public String getEditorValue() /*-{
        return $wnd.CKEDITOR.instances.editor_div.getData();
    }-*/;
}
