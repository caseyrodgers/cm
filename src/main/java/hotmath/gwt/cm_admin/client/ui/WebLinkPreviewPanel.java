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
    
    private WebLinkModel webLink;
    private boolean showAlternative;

    public WebLinkPreviewPanel(WebLinkModel webLink, boolean showAlternative) {
        super(true);
        setPixelSize(800,  600);
        setResizable(true);
        this.webLink = webLink;
        this.showAlternative=showAlternative;
        
        setModal(true);
        setMaximizable(true);
        setHeadingText("View Link Preview");
        buildUi();
        
        setVisible(true);
    }
    
    private void buildUi() {
        Frame frame = new Frame(webLink.getUrl());
        if(showAlternative) {
            final BorderLayoutContainer borderPanel = new BorderLayoutContainer();
            FlowPanel header = new FlowPanel();
            
            FramedPanel framedPanel = new FramedPanel();
            framedPanel.setHeaderVisible(false);
            
            
            header.getElement().setAttribute("style",  "font-weight: bold;margin: 15px");
            header.add(new HTML("<div style='margin-bottom: 15px'>Does the page look correct?</a>"));
            header.add(new TextButton("Alternate Display Method", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    Window.open(webLink.getUrl(),  "CmWebLink",  null);
                    if(!webLink.isPublicLink()) {
                        new WebLinkPreviewPanelOptionDialog(webLink);
                    }
                    hide();
                }
            }));
            framedPanel.setWidget(header);
            borderPanel.setNorthWidget(framedPanel, new BorderLayoutData(90));
            
            borderPanel.setCenterWidget(frame);
            setWidget(borderPanel);
        }
        else {
            setWidget(frame);
        }
    }

}
