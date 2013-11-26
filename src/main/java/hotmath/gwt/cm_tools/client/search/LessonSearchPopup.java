package hotmath.gwt.cm_tools.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class LessonSearchPopup extends Composite {

    private static final Binder binder = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, LessonSearchPopup> {
    }

    public LessonSearchPopup() {
        initWidget(binder.createAndBindUi(this));
    }
    
    @UiField 
    DialogBox theDialog;

    public void show() {
        theDialog.show();
    }

}