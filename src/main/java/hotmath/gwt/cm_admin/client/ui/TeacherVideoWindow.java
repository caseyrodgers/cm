package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;


public class TeacherVideoWindow extends CmWindow {
    
    String videoPlayerId = "flowPlayer_" + System.currentTimeMillis();

    public TeacherVideoWindow(String webinar, String heading) {
    	
    	showVideo(webinar, heading);
    }
    
    private void showVideo(String video, String heading) {
    	String location = "http://catchupmath.com/assets/teacher_videos/";
        String swf = location + video;

        setHeading(heading);
        setSize(800,498);
        setResizable(true);
        setMaximizable(true);
        setModal(false);  //TODO: should this be true?
        
        SWFSettings s = new SWFSettings();
        s.setMinPlayerVersion(new PlayerVersion(9));
        
        /** add id to force no cache .. is a bug with flowplayer that 
         *  if in cache it only plays once.
         *  
         */
        String flashVars = "autostart=false&thumb=" + location + heading + "/FirstFrame.png&thumbscale=45&color=0x1A1A1A,0x1A1A1";
        SWFWidget swfWidget = new SWFWidget(swf, "100%", "100%", s);

        swfWidget.addFlashVar("config", flashVars);
        swfWidget.addParam("scale", "showall");
        swfWidget.addParam("allowfullscreen", "true");
        swfWidget.addParam("allowscriptaccess", "always");
        swfWidget.addParam("quality", "best");
        swfWidget.addParam("cachebusting", "true");
        swfWidget.addParam("bgcolor", "#1a1a1a");
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