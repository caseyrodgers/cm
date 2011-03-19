package hotmath.gwt.solution_editor.client;

import hotmath.gwt.solution_editor.client.rpc.MathMlResource;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;

public class MathMlEditorDialog extends Window  {
    
    private static MathMlEditorDialog __instance;
    
    Callback callback;
    MathMlResource resource;
    
    static {
        publishNative();
    }
    
    public MathMlEditorDialog(Callback callback, MathMlResource resource, boolean allowCreate) {
        __instance = this;
        this.callback = callback;
        this.resource = resource != null?resource:new MathMlResource();
        
        setSize(850,550);
        setHeading("Math ML Editor");
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
            addButton(new Button(label, new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent be) {
                    MathMlEditorDialog.this.resource.setMathMl(getMathMlFromFlash());
                    MathMlEditorDialog.this.callback.resourceUpdated(MathMlEditorDialog.this.resource);
                    
                    hide();
                }
            }));
        }


        addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent be) {
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