package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;


public class StudentHowToFlashWindow extends CmWindow {
    
    String URL="http://catchupmath.com/assets/teacher_videos/Student%20How%20To%20video/Student%20How%20To%20video_controller.swf";
    String title = "Video: How to use Catchup Math";
    
    public StudentHowToFlashWindow() {
        
        setHeading(title);
        
        addCloseButton();
        setSize(775,640);
        drawFlashFrame();
        setVisible(true);
    }
    
    
    private void drawFlashFrame() {
        if(!SWFObjectUtil.isVersionIsValid(new PlayerVersion(CmShared.FLASH_MIN_VERSION))) {
            CatchupMathTools.showAlert("Please upgrade your Flash version to at least " + CmShared.FLASH_MIN_VERSION);
        }
        else {
            SWFSettings s = new SWFSettings();
            s.setMinPlayerVersion(new PlayerVersion(CmShared.FLASH_MIN_VERSION));
            SWFWidget swfWidget = new SWFWidget(URL, "100%", "100%", s);
            swfWidget.addParam("wmode", "opaque");
            swfWidget.addFlashVar("thumb", "http://catchupmath.com/assets/teacher_videos/Student%20How%20To%20video/FirstFrame.png");
            swfWidget.addFlashVar("autostart", "true");
            
            swfWidget.setStyleName("activity-widget");
            add(swfWidget);
        }
    }
}