package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelContainer.ResourceViewerState;
import hotmath.gwt.shared.client.CmShared;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;
import sb.util.SbUtilities;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplVideo extends ResourceViewerImplFlash {

	String STYLE_NAME = "resource-viewer-impl-video";

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
		if (!SWFObjectUtil.isVersionIsValid(new PlayerVersion(9))) {
			Html html = new Html(CmShared.FLASH_ALT_CONTENT);
			addResource(html, getResourceItem().getTitle());
		} else {

			InmhItemData item = getResourceItem();

			SWFWidget swfWidget = null;
			
			boolean isYouTube = item.getFile().indexOf("youtube.com/") > -1;
			if (isYouTube) {

				String file = item.getFile();

				SWFSettings s = new SWFSettings();
				s.setMinPlayerVersion(new PlayerVersion(9));

				swfWidget = new SWFWidget(file, "100%", "100%", s);
			} else {
				String prefix = null;
				if (isANumber(item.getFile()))
					prefix = "/help/flvs/tw/";
				else if(item.getFile().startsWith("/")) {
				    /** is absolute .. leave as is
				     * 
				     */
				    prefix = "";
				}
				else {
					prefix = "/help/flvs/mathtv/";
				}
				
				String fileName = item.getFile();
				if(!fileName.endsWith(".flv")) {
				    fileName += ".flv";
				}
				String videoPath = prefix + fileName;

				SWFSettings s = new SWFSettings();
				s.setMinPlayerVersion(new PlayerVersion(9));

				/**
				 * add id to force no cache .. is a bug with flowplayer that if
				 * in cache it only plays once.
				 */
				swfWidget = new SWFWidget(
						"/gwt-resources/flowplayer/flowplayer-3.2.5/flowplayer-3.2.5.swf?id=" + id, "100%",
						"100%", s);

				// cm: $a12fd4b15a588479e9e
				// hm: $852288f15c37539e229
				String flashVars = "{'key':'$a12fd4b15a588479e9e','clip':{'url':'THE_VIDEO'},'playerId':'PLAYER_ID','playlist':[{'url':'THE_VIDEO'}]}";

				flashVars = flashVars.replaceAll("THE_VIDEO", videoPath);
				flashVars = flashVars.replaceAll("PLAYER_ID", id);

				swfWidget.addFlashVar("config", flashVars);
			}

			swfWidget.addParam("wmode", "opaque");
			swfWidget.addParam("scale", "scale");
			swfWidget.addParam("allowfullscreen", "true");
			swfWidget.addParam("allowscriptaccess", "always");
			swfWidget.addParam("quality", "high");
			swfWidget.addParam("cachebusting", "true");
			swfWidget.addParam("bgcolor", "000000");

			addResource(swfWidget, getResourceItem().getTitle());
		}
		return this;
	}

	/**
	 * return true if the string in x can be evaluated as an integer.
	 * 
	 * TODO: how to know without exception? why do this at all?
	 * 
	 * @param x
	 * @return
	 */
	private boolean isANumber(String x) {
		try {
			Integer.parseInt(x);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	@Override
	public Integer getOptimalHeight() {
		return 290;
	}

	@Override
	public Integer getOptimalWidth() {
		return 340;
	}
}
