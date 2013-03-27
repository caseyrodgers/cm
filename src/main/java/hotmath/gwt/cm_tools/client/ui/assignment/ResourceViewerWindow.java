package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_tools.client.ui.GWindow;

public class ResourceViewerWindow extends GWindow {
    
    private ResourceView view;

    public ResourceViewerWindow(ResourceView view) {
        super(true);
        this.view = view;
        addStyleName("resource-viewer-window");
        this.view = view;
        setHeadingText(view.getResourceTitle());
        setPixelSize(640,480);
        setWidget(view.asWidget());
        
        setVisible(true);
    }
    
    

}
