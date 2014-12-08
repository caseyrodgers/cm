package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ReviewPanel extends ContentPanel {
	
	public ReviewPanel(final CallbackOnComplete callback) {
		setWidget(new DefaultGxtLoadingPanel("No selected topic"));
		addTool(new TextButton("Explore Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                callback.isComplete();
            }
        }));
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
		
		forceLayout();
	}
}
