package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewer;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerContainer;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


/** Main resource viewer area
 * 
 */
public class ResourceContainer extends LayoutContainer {

	
	public ResourceContainer() {
		// setTitle("Resource Viewer");
		setStyleName("resource-container");
		
		String bgStyle = UserInfo.getInstance().getBackgroundStyle();
		
		if(bgStyle != null && bgStyle.length() != 0) {
		    Log.info("ResourceContainer: Background style set to: " + bgStyle);
		    addStyleName(bgStyle);
		}
		
		setScrollMode(Scroll.AUTOY);
	}
	

	/** Make sure container's children are centered and
	 *  have the correct height.
	 *  
	 */
	public void resetChildSize() {
	    int HEADER_SIZE=25;
	    int HEADER_OFFSET_LEFT=20;
	    
	    Log.info("ResourceContainer: resizing children");
	    
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
            int height=0;
            El header=null;
            El footer=null;
            double allowedVerticalSpace=ResourceViewerContainer.ALLOWED_VERTICAL_SPACE;
	        for(int i=0;i<getItemCount();i++) {
                El el = getItem(i).el();
                
                // defer the setting of the header and footer
                // util after we have determined the size
                if(getItem(i) == _header) {
                    header = el;
                }
                else if(getItem(i) == _footer) {
                    footer = el;
                }
                else {
                    
                    boolean setHeight=true;
                    if(getItem(i) instanceof ResourceViewerContainer) {
                       setHeight = ((ResourceViewerContainer)getItem(i)).shouldSetResourceContinerHeight();
                       allowedVerticalSpace = ((ResourceViewerContainer)getItem(i)).getAllowedVerticalSpace();
                    }
                    else {
                        continue;
                    }
                    
                    if(setHeight) {
                        // set the height of the detail area to 80% of total space
                        int cheight = CmMainPanel.__lastInstance._mainContent.getOffsetHeight();
                        int elHeight = (int)Math.floor((double)cheight * allowedVerticalSpace);
                        el.setHeight(elHeight);
                        
                        int cHeight = elHeight - 10; // offset
                        ((ResourceViewerContainer)getItem(i)).setResourceContinerHeight(cHeight);
                    }
                    
                    el.center(CmMainPanel.__lastInstance._mainContent.getElement());
                    
                    top = el.getTop();
                    left = el.getLeft();
                    width = el.getWidth();
                    height = el.getHeight();
                }
	        }
	        
	        if(header != null) {
	            header.setTop(top - HEADER_SIZE);
	            header.setLeft(left);
	            header.setWidth(width);
	        }
	        if(footer != null) {
	            footer.setTop(top + height);
	            footer.setLeft(left);
	        }
	        
            layout();
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/** Update the header panel for the resource
	 * 
	 * 
	 * @param title  The title show in title arr
	 * @param styleName If not null, then use to customize header/footer
	 */
	public void addResourceViewerHeader(String title,String styleName) {
	    noHeaderOrFooter=false;
	    buildResourceViewerHeader();
	    
	    if(styleName != null) {
	        _header.setStyleName(styleName + "-header");
	        _footer.setStyleName(styleName + "-footer");
	    }
	    add(_header);
	    add(_footer);
	    _title.setText(title);
	    layout();
	    resetChildSize();
	}
	
	HorizontalPanel _header;
	LayoutContainer _footer;
	Label _title;
	private void buildResourceViewerHeader() {
        Anchor closeAnchor = new Anchor();
        String closeHtml = "<span>Close <img src='/gwt-resources/images/close_x.png'/></span>";
        closeAnchor.setHTML(closeHtml);
        closeAnchor.setStyleName("resource-viewer-close-button");
        closeAnchor.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                CmMainPanel.__lastInstance.removeResource();
                

                /** Fire event only when the resource is manually closed
                 * 
                 */
                EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_RESOURCE_VIEWER_CLOSE));
            }
        });

        
        _header = new HorizontalPanel();
        _header.setStyleName("resource-viewer-header");
        _header.add(closeAnchor);
        _title = new Label();
        _title.setStyleName("resource-viewer-title");       
        _header.add(_title);
        
        _footer = new LayoutContainer();
        _footer.setStyleName("resource-viewer-footer");

	}
	
	/** Do not show the header and footer
	 * 
	 */
	boolean noHeaderOrFooter;
	public void setNoHeaderOrFooter() {
	    noHeaderOrFooter=true;
        if(_header != null) {
            _header.setVisible(false);
            _footer.setVisible(false);
            layout();
        }
	}
	
	/** Add a widget to the resource viewer header
	 * 
	 * @param w
	 */
	public void addControl(Widget w) {
	    if(_header != null) {
	        _header.add(w);
	        layout();
	    }
	}
	
   public void removeControl(Widget w) {
        _header.remove(w);
    }
   
   
   
   public void removeResource() {
       removeAll();
   }

   
   
   /** Display a single resource, remove any previous
    * 
    * Do not  track its view
    * 
    * @param resourceItem
    */
   public void showResource(final InmhItemData resourceItem) {
       try {
           ResourceViewer viewer = ResourceViewerFactory.create(resourceItem.getType());

           CmMainPanel.__lastInstance._mainContent.removeAll();

           //CmMainPanel.__lastInstance._mainContent.setLayout(new FitLayout());

           // must be a ContentPanel
           LayoutContainer cp = (LayoutContainer) viewer.getResourcePanel(resourceItem);

           CmMainPanel.__lastInstance._mainContent.add(cp);
           CmMainPanel.__lastInstance._mainContent.layout();
           CmMainPanel.__lastInstance._mainContent.resetChildSize();
           
           
           EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_RESOURCE_VIEWER_OPEN, viewer));
           
       } catch (Exception hme) {
           hme.printStackTrace();
           CatchupMathTools.showAlert("Error: " + hme.getMessage());
       }
   }

}