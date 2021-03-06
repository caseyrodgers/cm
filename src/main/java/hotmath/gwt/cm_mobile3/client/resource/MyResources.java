package hotmath.gwt.cm_mobile3.client.resource;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface MyResources extends ClientBundle {
    public static final MyResources INSTANCE = GWT.create(MyResources.class);

    @Source("MyResources.css")
    public CssResource css();
    
    @Source("icon-info.png")
    ImageResource infoIcon();
    
    @Source("assignment-no.png")
    ImageResource assignmentNo();
    
    @Source("assignment-has.png")
    ImageResource assignmentHas();
    
    @Source("assignment-has-annotation.png")
    ImageResource assignmentHasAnnotation();
}