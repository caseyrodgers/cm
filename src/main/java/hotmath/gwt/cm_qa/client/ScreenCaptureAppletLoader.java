package hotmath.gwt.cm_qa.client;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class ScreenCaptureAppletLoader extends LayoutContainer {
    
    static ScreenCaptureAppletLoader __lastInstance;
    static String _lastText;
    
    Callback callback;
    public ScreenCaptureAppletLoader() {
        __lastInstance=this;
        System.out.println("Starting up Screen Capture");
        registerCallback();
        
        _isReady = false;
        String appletHtml  = "<applet MAYSCRIPT width='100%' height='100%' " + 
        "id='html_editor' archive='screen_capture.jar'  " +
        "code='hotmath.capture.ScreenCaptureApplet.class'  " +
        "codebase='/gwt-resources/screen_capture'> " +
        "<param value='16' name='fontSize'> " +
        "<param value='false' name='codebase_lookup'> " + 
        "</applet>";
        
        Html html = new Html(appletHtml);
            
        add(html);
        Label label = new Label("Screen Capture Running ..");
        label.setToolTip("Screen capture is in a different window.  Do not close the window.  If you do you will need to refresh this page.");
        
        add(label);
    }
    
    public void setCallback(ScreenCaptureAppletLoader.Callback callback) {
        this.callback = callback;
    }
    
    
    static boolean _isReady;
    static public String appletIsReady() {
        System.out.println("applet is ready!");
        _isReady = true;
        return _lastText;
    }

    static public void saveAndClose(String text) {
        _lastText = text;
        System.out.println("applet save and close!");
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
        $wnd.htmlEditorAppletIsReady = @hotmath.gwt.cm_qa.client.ScreenCaptureAppletLoader::appletIsReady();
        $wnd.htmlEditorSaveAndClose = @hotmath.gwt.cm_qa.client.ScreenCaptureAppletLoader::saveAndClose(Ljava/lang/String;);        
    }-*/;
    
    
    public interface Callback {
        void saveAndCloseWindow(String text);
    }
}
