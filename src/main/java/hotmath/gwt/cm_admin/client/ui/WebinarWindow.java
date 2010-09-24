package hotmath.gwt.cm_admin.client.ui;

import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;


public class WebinarWindow extends CmWindow {
    
    String videoPlayerId = "flowPlayer_" + System.currentTimeMillis();
    
    public WebinarWindow() {
        String swf = "/resources/webinar/CM Webinar draft_controller.swf";

        setHeading("Catchup Math Webinar");
        setSize(800,498);
        setResizable(true);
        setModal(false);
        
        SWFSettings s = new SWFSettings();
        s.setMinPlayerVersion(new PlayerVersion(9));
        
        /** add id to force no cache .. is a bug with flowplayer that 
         *  if in cache it only plays once.
         *  
         */
        String flashVars = "autostart=false&thumb=/resources/webinar/FirstFrame.png&thumbscale=45&color=0x1A1A1A,0x1A1A1";
        SWFWidget swfWidget = new SWFWidget(swf, "100%", "100%", s);
        swfWidget.addFlashVar("config", flashVars);
        swfWidget.addParam("scale", "scale");
        swfWidget.addParam("allowfullscreen", "true");
        swfWidget.addParam("allowscriptaccess", "always");
        swfWidget.addParam("quality", "high");
        swfWidget.addParam("cachebusting", "true");
        swfWidget.addParam("bgcolor", "000000");
        add(swfWidget);
        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));        
        
        setVisible(true);
    }
}