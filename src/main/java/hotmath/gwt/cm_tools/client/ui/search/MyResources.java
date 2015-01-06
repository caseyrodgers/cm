package hotmath.gwt.cm_tools.client.ui.search;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface MyResources extends ClientBundle {
    public static final MyResources INSTANCE = GWT.create(MyResources.class);
    
    @Source("search_disabled.png")
    ImageResource searchDisabled();
    
    @Source("search_enabled.png")
    ImageResource searchEnabled();
}
