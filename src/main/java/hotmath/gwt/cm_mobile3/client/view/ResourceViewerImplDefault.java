package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class ResourceViewerImplDefault extends Composite implements ResourceViewer {
    
    InmhItemData resourceItem;

    interface MyUiBinder extends UiBinder<Widget, ResourceViewerImplDefault> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public ResourceViewerImplDefault(InmhItemData resourceItem) {
        this.resourceItem = resourceItem;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }
    
}
