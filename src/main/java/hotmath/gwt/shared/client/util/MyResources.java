package hotmath.gwt.shared.client.util;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface MyResources extends ClientBundle {
    public static final MyResources INSTANCE = GWT.create(MyResources.class);

    @Source("MyResources.css")
    public CssResource css();
    
    
    @Source("assignment-blue-5-24.png")
    ImageResource assignmentBlue();
    
    @Source("assignment-red-5-24.png")
    ImageResource assignmentRed();
}