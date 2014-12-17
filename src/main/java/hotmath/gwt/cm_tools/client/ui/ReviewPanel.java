package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplReview.ReviewCallback;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ReviewPanel extends ContentPanel {
    static public interface ReviewPanelCallback {
        void exporeTopic(InmhItemData item);
        
        /** call when a new lesson review has been loaded
         * 
         */
        void newTopicLoaded();
    }
    
    ReviewPanelCallback _callback;
    
	public ReviewPanel(final ReviewPanelCallback callback) {
	    this._callback = callback;
		setWidget(new DefaultGxtLoadingPanel("No selected lesson"));
		addTool(new TextButton("Explore", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _callback.exporeTopic(_item);
            }
        }));
		//setHeaderVisible(false);
	}

	InmhItemData _item=null;
	public void loadReview(final InmhItemData item) {
	    
	    this._item = item;
		ResourceViewerImplReview panel = new ResourceViewerImplReview(new ReviewCallback() {
		    public void newTopicLoaded(String file, String title) {
		        setHeadingText(title);
		        
		        if(!title.equals(item.getTitle())) {
		            _callback.newTopicLoaded();
		        }
		        _item = new InmhItemData(CmResourceType.REVIEW, file, title);
		        
		    }
		});
		
		setHeadingText(item.getTitle());
		
		panel.setResourceItem(item);
	
		FlowLayoutContainer flow = new FlowLayoutContainer();
		flow.setScrollMode(ScrollMode.AUTO);
		flow.add(panel.getResourcePanel());
		setWidget(flow);
		
		forceLayout();
	}
}
