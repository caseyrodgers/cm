package hotmath.gwt.cm_tools.client.search;

import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LessonSearchField extends FlowPanel {
    private TextBox _lessonText = new TextBox();
    protected CmList<Topic> _allLessons;

    interface MyUiBinder extends UiBinder<Widget, LessonSearchField> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    public LessonSearchField() {
        add(uiBinder.createAndBindUi(this));
    }
    
    
    @UiHandler("doSearch")
    public void doSearch(ClickEvent ce) {
        LessonSearchPopup popup = new LessonSearchPopup();
        popup.show();
    }

    @UiField
    TextBox searchField;
    
    @UiField
    Button doSearch;
}