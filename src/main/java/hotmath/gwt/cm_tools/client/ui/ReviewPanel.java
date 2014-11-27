package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class ReviewPanel extends ContentPanel {
	
	public ReviewPanel() {
		
		setWidget(new DefaultGxtLoadingPanel("No selected topic"));
		//setHeaderVisible(false);
	}

	public void loadReview(String reviewFile, String reviewTitle) {

		ResourceViewerImplReview panel = new ResourceViewerImplReview();
		InmhItemData item = new InmhItemData(CmResourceType.REVIEW, reviewFile, reviewTitle);
		panel.setResourceItem(item);
	
		FlowLayoutContainer flow = new FlowLayoutContainer();
		flow.setScrollMode(ScrollMode.AUTO);
		flow.add(panel.getResourcePanel());
		setWidget(flow);
	}
}
