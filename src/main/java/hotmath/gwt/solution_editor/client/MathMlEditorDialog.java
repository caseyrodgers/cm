package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.solution_editor.client.rpc.MathMlResource;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class MathMlEditorDialog extends GWindow  {
    
    private static MathMlEditorDialog __instance;
    
    Callback callback;
    MathMlResource resource;
    
    static {
        publishNative();
    }
    
    public MathMlEditorDialog(Callback callback, MathMlResource resource, boolean allowCreate) {
        super(false);
        
        __instance = this;
        this.callback = callback;
        this.resource = resource != null?resource:new MathMlResource();
        
        setPixelSize(850,550);
        setHeadingText("Math ML Editor");
        setClosable(false);

        Frame frame = new Frame("/gwt-resources/fmath_wrapper.html");
        frame.setSize("100%", "99%");
        DOM.setElementProperty(frame.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(frame.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(frame.getElement(), "frameSpacing", 0); // disable
        DOM.setElementProperty(frame.getElement(), "scrolling", "no"); // disable

        add(frame);

        
        if(allowCreate) {
            String label = resource!=null?"Update":"Create";
            addButton(new TextButton(label, new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    MathMlEditorDialog.this.resource.setMathMl(getMathMlFromFlash());
                    MathMlEditorDialog.this.callback.resourceUpdated(MathMlEditorDialog.this.resource);
                    
                    hide();
                }
            }));
        }


        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
                MathMlEditorDialog.this.callback.resourceUpdated(null);
            }
        }));

        setVisible(true);
    }
    
    static private String getResourceMathMl_Gwt() {
        String mathMl = __instance.resource.getMathMl();
        return mathMl!=null?mathMl:"<mrow><mtext>...</mtext></mrow>";
    }

    private native String getMathMlFromFlash() /*-{
        try {
             return $wnd.parent.getResourceMathMl();
        }
        catch(e) {
            alert(e);
        }
    }-*/;
    
    
    private native void setMathMlInFlash(String mathMl) /*-{
        try {
            parent.setResourceMathMl(mathMl);
        }
        catch(e) {
             alert(e);
        }
    }-*/;
    
    
    public static interface Callback {
        void resourceUpdated(MathMlResource resource);
    }
    
    static private native void publishNative() /*-{
        $wnd.getResourceMathMl_Gwt = @hotmath.gwt.solution_editor.client.MathMlEditorDialog::getResourceMathMl_Gwt();
    }-*/;
}