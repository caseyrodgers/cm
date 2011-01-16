package hotmath.gwt.solution_editor.client;

public class HtmlEditorAppletWrapper {
    
    HtmlEditorApplet applet;
    
    public HtmlEditorAppletWrapper(HtmlEditorApplet applet) {
        this.applet = applet;
    }
    
    public String getValue() {
        return applet.getValue(); 
    }

    public void setValue(String text) {
        applet.setValue(text); 
    }

}
