package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.ui.viewer.ResourceViewerContainer;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;


/** Main resource viewer area
 * 
 */
public class ResourceContainer extends LayoutContainer {

	
	public ResourceContainer() {
		// setTitle("Resource Viewer");
		setStyleName("resource-container");
		setScrollMode(Scroll.AUTO);
		
		
		/** @TODO: move toe CmMainPanel (only one)
		 * 
		 */
		Window.addResizeHandler(new ResizeHandler() {
		    @Override
		    public void onResize(ResizeEvent event) {
		        resetChildSize();
		    }
		});
		buildResourceViewerHeader();
	}
	

	/** Make sure container's children are centered and
	 *  have the correct height.
	 *  
	 */
	public void resetChildSize() {
	    int HEADER_OFFSET_TOP=0;
	    int HEADER_OFFSET_LEFT=20;
	    
	    try {
	        // no children
	        if(getItemCount() == 0)
	            return;

	        layout();
	        // do not size the header's height, only position it 
	        // right above the resized content panel
            int top=0;
            int left=0;
            int width=0;
            El header=null;
	        for(int i=0;i<getItemCount();i++) {
                El el = getItem(i).el();
                if(!(getItem(i) instanceof HorizontalPanel)) {
                    
                    boolean setHeight=true;
                    if(getItem(i) instanceof ResourceViewerContainer) {
                       setHeight = ((ResourceViewerContainer)getItem(i)).shouldSetResourceContinerHeight();
                    }
                    
                    if(setHeight) {
                        // set the height of the detail area to 80% of total space
                        int height = CmMainPanel.__lastInstance._mainContent.getOffsetHeight();
                        int elHeight = (int)Math.floor((double)height * .80);
                        el.setHeight(elHeight);
                    }
                    
                    el.center(CmMainPanel.__lastInstance._mainContent.getElement());
                    
                    top = el.getTop();
                    left = el.getLeft();
                    width = el.getWidth();
                }
                else {
                    header = el;
                }
	        }
	        
	        if(header != null) {
	            header.setTop(top - 45);
	            header.setLeft(left);
	            header.setWidth(width);
	        }
	        
            layout();
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	/** Update the header panel for the resource */
	public void addResourceViewerHeader(String title) {
	    _title.setText(title);
	    add(_header);
	    layout();
	    resetChildSize();
	}
	
	HorizontalPanel _header;
	Label _title;
	private void buildResourceViewerHeader() {
        Anchor closeAnchor = new Anchor();
        String closeHtml = "<span>Close <img src='/gwt-resources/images/close_x.png'/></span>";
        closeAnchor.setHTML(closeHtml);
        closeAnchor.setStyleName("resource-viewer-close-button");
        closeAnchor.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                CmMainPanel.__lastInstance.removeResource();
            }
        });
        
        Button closeButton = new Button("Close");
        closeButton.setStyleName("resource-viewer-close-button");
        closeButton.addSelectionListener( new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CmMainPanel.__lastInstance.removeResource();
            }
        });
        
        
        _header = new HorizontalPanel();
        _header.setStyleName("resource-viewer-header");
        _header.add(closeAnchor);
        _title = new Label();
        _title.setStyleName("resource-viewer-title");       
        _header.add(_title);
	}
}