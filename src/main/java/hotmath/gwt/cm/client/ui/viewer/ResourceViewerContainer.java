package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.ui.CmMainPanel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/** A resource viewer container with close button
 *  
 *   set to BorderLayout, so add(cmp, CENTER);
 * @author Casey
 * 
 * Provide a standard panel that provides nice rounded edges
 * and a common look and feel between all resource types.
 * 
 * using html struct like below
 *     div id='res_wrapper'
 *         div class='header'
 *             div class='left'
 *             div class='right'
 *         div class='body'
 *             div class='left'
 *             div class='right'
 *             div class='content'
 *         div class='footer'
 *             div class='left'
 *             div class='right'
 *             
 *             
 *
 */
abstract public class ResourceViewerContainer extends ContentPanel  implements	ResourceViewer{
    HorizontalPanel _header;
    Label _title;
	public ResourceViewerContainer() {
	    getHeader().setVisible(false);
		setStyleName("resource-viewer-container");
		Anchor closeAnchor = new Anchor();
		String closeHtml = "<span>Close <img src='/gwt-resources/images/close_x.png'/></span>";
		closeAnchor.setHTML(closeHtml);
		closeAnchor.setStyleName("resource-viewer-close-button");
		closeAnchor.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent arg0) {
                removeResourcePanel();
                el().fadeOut(FxConfig.NONE);
                CmMainPanel.__lastInstance.removeResource();                
		    }
		});
		Button closeButton = new Button("Close");
		closeButton.setStyleName("resource-viewer-close-button");
		closeButton.addSelectionListener( new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				removeResourcePanel();
				el().fadeOut(FxConfig.NONE);
				CmMainPanel.__lastInstance.removeResource();				
			}
		});
		_header = new HorizontalPanel();
		_header.setStyleName("resource-viewer-header");
		_header.add(closeAnchor);
        _title = new Label();
        _title.setStyleName("resource-viewer-title");		
		_header.add(_title);
		add(_header);		
	}
	
	/** Add the resource to the center
	 * 
	 * @param w
	 */
	public void addResource(Widget w, String title) {
		//add(w, new TableData("100%", "100%"));
	    add(w);
		_title.setText(title);
	}

	abstract public Widget getResourcePanel(InmhItemData resource);

	public void removeResourcePanel() {
		// empty
	}
}
