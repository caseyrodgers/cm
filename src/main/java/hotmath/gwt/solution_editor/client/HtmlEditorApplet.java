package hotmath.gwt.solution_editor.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class HtmlEditorApplet extends SimplePanel {
    
    static HtmlEditorApplet __lastInstance;
    static String _lastText;
    
    Callback callback;
    public HtmlEditorApplet() {
        __lastInstance=this;
        System.out.println("Starting up Editor Applet");
        registerCallback();
        /*test*/
        _isReady = false;
        String appletHtml  = 
                "<applet MAYSCRIPT width='100%' height='100%' " + 
                    "id='html_editor' archive='html_editor.jar'  " +
                    "code='hotmath.editor.HtmlEditorApp.class'  " +
                    "codebase='/gwt-resources/solution_editor'> " +
                    "<param value='16' name='fontSize'> " +
                    "<param value='false' name='codebase_lookup'> " + 
                 "</applet>";
        
        HTML html = new HTML(appletHtml);
        
        setVisible(true);
        setWidget(html);
    }
    
    public void setCallback(HtmlEditorApplet.Callback callback) {
        this.callback = callback;
    }
    
    
    static boolean _isReady;
    static public String htmlEditorAppletIsReady() {
        System.out.println("applet is ready!");
        _isReady = true;
        return _lastText;
    }

    static public void htmlEditorSaveAndClose(String text) {
        _lastText = text;
        Log.debug("applet save and close!");
        __lastInstance.callback.saveAndCloseWindow(text);
    }
    
    public void setValue(final String text) {
        _lastText = text;
    }
    
    public native void setValue2Js(String text) /*-{
    try {
        var el = $doc.getElementById('html_editor');
        el.setEditorText(text);
     }
     catch(e) {
         alert('error setting applet value: ' + e);
     }
    }-*/;
    
    public native String getValue() /*-{
        try {
            return $doc.getElementById('html_editor').getEditorText();
         }
         catch(e) {
             alert('error reading applet value: ' + e);
         }
    }-*/;
     
    private native void registerCallback() /*-{
        $wnd.htmlEditorAppletIsReady = @hotmath.gwt.solution_editor.client.HtmlEditorApplet::htmlEditorAppletIsReady();
        $wnd.htmlEditorSaveAndClose = @hotmath.gwt.solution_editor.client.HtmlEditorApplet::htmlEditorSaveAndClose(Ljava/lang/String;);        
    }-*/;
    
    
    public interface Callback {
        void saveAndCloseWindow(String text);
    }
}
