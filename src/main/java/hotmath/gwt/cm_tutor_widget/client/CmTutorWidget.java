package hotmath.gwt.cm_tutor_widget.client;

import hotmath.gwt.cm_activity.client.WordProblemsPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provide a factory for tutor widgets
 * 
 * Clients will load this module, which provides 
 * a high level interface to load selected widgets
 * and provide the proper validation.
 * 
 * 
 * After this module is loaded, a global JS method
 * gwtTutorWidgetsEmbed() which will look in current 
 * document for a 'hm_flash_widget_def' element.  If 
 * not found, then nothing is done.  If it is found
 * then the JS is extracted and the proper widget is
 * dynamically inserted into the proper location and 
 * initialized.
 * 
 * 
 * 
 * @author casey
 * 
 */
public class CmTutorWidget implements EntryPoint {
    static private CmTutorWidget __instance;
    public void onModuleLoad() {
        this.__instance = this;
        
        initializeExternalJs();
        System.out.println("Tutor Widgets Initialized");
    }
    
    public void findAndEmbedWidget() {
        Element el = DOM.getElementById("hm_flash_widget");
        if(el != null) {
            Widget fp = new WordProblemsPanel();
            //el.setInnerHTML(fp.getElement().getInnerHTML());
            
            RootPanel.get().add(fp);
        }
        else {
            Window.alert("Does not have widget");
        }
    }
    
    
    static public void gwt_tutorWidgetEmbed() {
        __instance.findAndEmbedWidget();
    }
    
    /** call global JS function to initialize any external resources
     * 
     */
    private native void initializeExternalJs()/*-{
        $wnd.gwt_tutorWidgetEmbed = @hotmath.gwt.cm_tutor_widget.client.CmTutorWidget::gwt_tutorWidgetEmbed();
        $wnd.initalizeTutorWidgetEmbed();
    }-*/; 
}
