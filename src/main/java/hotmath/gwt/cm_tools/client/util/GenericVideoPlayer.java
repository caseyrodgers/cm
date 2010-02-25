package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;

public class GenericVideoPlayer extends CmWindow {
    
    String videoPlayerId = "flowPlayer_" + System.currentTimeMillis();
    
    public GenericVideoPlayer(String videoToPlay, String title) {
        

        setHeading(title);
        setSize(330,320);
        setResizable(true);
        setModal(true);
        
        SWFSettings s = new SWFSettings();
        s.setMinPlayerVersion(new PlayerVersion(9));
        
        /** add id to force no cache .. is a bug with flowplayer that 
         *  if in cache it only plays once.
         */
        SWFWidget swfWidget = new SWFWidget("flowplayer-3.1.5.swf?id=" + videoPlayerId, "100%", "100%", s);

        String flashVars = "{'key':'$852288f15c37539e229','clip':{'url':'THE_VIDEO'},'playerId':'PLAYER_ID','playlist':[{'url':'THE_VIDEO'}]}";
        
        flashVars = flashVars.replaceAll("THE_VIDEO", videoToPlay);
        flashVars = flashVars.replaceAll("PLAYER_ID", videoPlayerId);
        
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