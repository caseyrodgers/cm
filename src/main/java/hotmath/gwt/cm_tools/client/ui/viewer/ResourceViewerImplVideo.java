package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.ui.HTML;
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
		clear();
		// HTML html = new HTML(CmShared.FLASH_ALT_CONTENT);
		// addResource(html, getResourceItem().getTitle());

		InmhItemData item = getResourceItem();
		boolean isYouTube = item.getFile().indexOf("youtube.com/") > -1;
		HTML htmlOut = null;
		if (isYouTube) {

			String file = item.getFile();

			htmlOut = new HTML(file);
		} else {
			String prefix = null;
			if (isANumber(item.getFile()))
				prefix = "/help/flvs/tw/";
			else if(item.getFile().startsWith("/") || item.getFile().startsWith("http")) {
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
			
			
			// use mp4 files only
			fileName = fileName.replace(".flv",  ".mp4");
			
			String videoPath = "https://catchupmath.com" + prefix + fileName;
			String videoHTML = "<div style=\"height: 250px\" class='video_wrapper'><video height=\"252px\" controls autoplay disablePictureInPicture disableRemotePlayback playsinline>" + 
					"\r\n" + 
					"    <source src=\"" + videoPath + "\" type=\"video/mp4\">\r\n" + 
					"\r\n" + 
					"    Sorry, your browser doesn't support embedded videos.\r\n" + 
					"</video>";
			htmlOut = new HTML(videoHTML);
		}
		addResource(htmlOut, getResourceItem().getTitle());
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
	    InmhItemData item = getResourceItem();
	    if(item.getFile().indexOf("/hm/") > -1) {
	        return 280;
	    }
	    else {
	        return 290;
	    }
	}

	@Override
	public Integer getOptimalWidth() {
	    InmhItemData item = getResourceItem();
        if(item.getFile().indexOf("/hm/") > -1) {
            return 500;
        }
        else {
            return 340;
        }
	}
}
