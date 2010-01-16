package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.shared.client.CmShared;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplVideo extends ResourceViewerImplFlash {

    String STYLE_NAME="resource-viewer-impl-video";
    
    String id;
    public ResourceViewerImplVideo() {
        addStyleName(STYLE_NAME);
        id = "flowPlayer_" + System.currentTimeMillis();
    }

    @Override
    public String getContainerStyleName() {
        return STYLE_NAME;
    }
    
    @Override
    public Boolean allowMaximize() {
        return false;
    }
    
    public ResourceViewerState getInitialMode() {
        return ResourceViewerState.OPTIMIZED;
    }

    public Widget getResourcePanel() {
        removeAll();
        if(!SWFObjectUtil.isVersionIsValid(new PlayerVersion(9))) {
            Html html = new Html(CmShared.FLASH_ALT_CONTENT);
            addResource(html,getResourceItem().getTitle());
        }
        else {
            String videoPath = "http://hotmath.com/help/flvs/tw/" + getResourceItem().getFile() + ".flv";
            
            SWFSettings s = new SWFSettings();
            s.setMinPlayerVersion(new PlayerVersion(9));
            
            /** add id to force no cache .. is a bug with flowplayer that 
             *  if in cache it only plays once.
             */
            SWFWidget swfWidget = new SWFWidget("flowplayer-3.1.5.swf?id=" + id, "100%", "100%", s);

            String flashVars = "{'key':'$852288f15c37539e229','clip':{'url':'THE_VIDEO'},'playerId':'PLAYER_ID','playlist':[{'url':'THE_VIDEO'}]}";
            
            flashVars = flashVars.replaceAll("THE_VIDEO", videoPath);
            flashVars = flashVars.replaceAll("PLAYER_ID", id);
            
            swfWidget.addFlashVar("config", flashVars);                
            swfWidget.addParam("scale", "scale");
            swfWidget.addParam("allowfullscreen", "true");
            swfWidget.addParam("allowscriptaccess", "always");
            swfWidget.addParam("quality", "high");
            swfWidget.addParam("cachebusting", "true");
            swfWidget.addParam("bgcolor", "000000");
            
            addResource(swfWidget,getResourceItem().getTitle());
        }
        
        return this;
    }
    
    
    /** for debugging IE
     * 
     * @return
     */
    private String getFlashObject() {
        String html = "" +
        "   <OBJECT id=swfID_" + id + " style='VISIBILITY: visible' height=206 width=318 classid=clsid:D27CDB6E-AE6D-11cf-96B8-444553540000>" +
        "     <PARAM NAME='FlashVars' VALUE=\"config={'clip':{'url':'http://hotmath.kattare.com:8080/help/flvs/tw/10008.flv'},'playerId':'" + id + "','playlist':[{'url':'http://hotmath.kattare.com/help/flvs/tw/10008.flv?a=1'}]}\">" +
        "     <PARAM NAME='Src' VALUE='flowplayer-3.1.5.swf?id=" + id + "'>" +
        "     <PARAM NAME='Scale' VALUE='Scale'>" +
        "     <PARAM NAME='AllowFullScreen' VALUE='true'>" +
        "   </OBJECT>";        
        
        return html;
    }
    
    /** Initialize the FlowPlayer flash video player. 
     *  
     *  flowPlayer_Gwt lives in CatchupMath.js
     *  
     * @param video
     * @return
     */
    private native String initializeVideoPlayer(String el, String video) /*-{
    
        var d = $doc.getElementById(el);
        alert('element: ' + d);
        return;
        
        $wnd.flowPlayer_Gwt(el, video);
    }-*/;    
    
    @Override
    public Integer getOptimalHeight() {
        return 290;
    }
    
    @Override
    public Integer getOptimalWidth() {
        return 340;
    }
}
