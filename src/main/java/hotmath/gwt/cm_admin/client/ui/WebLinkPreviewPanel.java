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

    public WebLinkPreviewPanel(WebLinkModel webLink) {
        super(true);
        setPixelSize(800,  600);
        setResizable(true);
        this.webLink = webLink;
        
        setModal(true);
        setMaximizable(true);
        setHeadingText("Web Link Preview Dialog");
        buildUi();
        
        setVisible(true);
    }
    
    private void buildUi() {
        final BorderLayoutContainer borderPanel = new BorderLayoutContainer();
        FlowPanel header = new FlowPanel();
        
        FramedPanel framedPanel = new FramedPanel();
        framedPanel.setHeaderVisible(false);
        
        
        header.getElement().setAttribute("style",  "font-weight: bold;margin: 15px");
        header.add(new HTML("<div style='margin-bottom: 15px'>Does the page look correct?  If not you might have to show it in a new browser window.</a>"));
        header.add(new TextButton("Open In New Window", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                Window.open(webLink.getUrl(),  "CmWebLink",  null);
                new WebLinkPreviewPanelOptionDialog(webLink);
                hide();
            }
        }));
        framedPanel.setWidget(header);
        borderPanel.setNorthWidget(framedPanel, new BorderLayoutData(90));
        
        
        Frame frame = new Frame(webLink.getUrl());
        borderPanel.setCenterWidget(frame);
        setWidget(borderPanel);
    }

}
