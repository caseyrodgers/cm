package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class WebLinkPreviewPanel extends GWindow {
    
    private boolean showAlternative;
    private String webLinkUrl;
    private WebLinkModel webLinkModel;
    public WebLinkPreviewPanel(WebLinkModel webLinkModel, String url, boolean showAlternative) {
        super(false);
        this.webLinkModel = webLinkModel;
        this.webLinkUrl = url;
        setPixelSize(800,  600);
        setResizable(true);
        this.showAlternative=showAlternative;
        
        setModal(true);
        setMaximizable(true);
        setHeadingText("View Link Preview");
        buildUi();
        
        setVisible(true);
    }
    
    private void buildUi() {
        Frame frame = new Frame(webLinkUrl);
        if(showAlternative) {
            final BorderLayoutContainer borderPanel = new BorderLayoutContainer();
            FlowPanel header = new FlowPanel();
            
            FramedPanel framedPanel = new FramedPanel();
            framedPanel.setHeaderVisible(false);
            
            header.getElement().setAttribute("style",  "font-weight: bold;margin: 15px");
            header.add(new HTML("<div style='margin-bottom: 15px'>Does the page look correct?</a>"));
            header.add(new TextButton("OK", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    hide();
                }
            }));
            header.add(new TextButton("Alternate Display Method", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    Window.open(webLinkUrl,  "CmWebLink",  null);
                    if(!webLinkModel.isPublicLink()) {
                        new WebLinkPreviewPanelOptionDialog(webLinkModel);
                    }
                    hide();
                }
            }));
            framedPanel.setWidget(header);
            borderPanel.setNorthWidget(framedPanel, new BorderLayoutData(90));

            borderPanel.setCenterWidget(frame);
            
            
            addButton(new TextButton("Cancel", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    hide();
                }
            }));
            

            setWidget(borderPanel);
        }
        else {
            
            addButton(new TextButton("Close", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    hide();
                }
            }));
            
            
            setWidget(frame);
        }
        
        
    }

}
