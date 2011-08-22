package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionLessonResourceViewImpl extends AbstractPagePanel implements PrescriptionLessonResourceView{

    Presenter presenter;
    
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    interface MyUiBinder extends UiBinder<Widget, PrescriptionLessonResourceViewImpl> {
    }
    
    public PrescriptionLessonResourceViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public String getTitle() {
        return "Resource Viewer";
    }
    
    @Override
    public String getBackButtonText() {
        return "Back";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter; 
        presenter.setupView(this);
    }
    
    @Override
    public void setResourceTitle(String title) {
        resourceTitle.setInnerHTML(title);
    }
    
    @UiField 
    HeadingElement resourceTitle;
    
    @UiField
    HTMLPanel innerContainer;

    @Override
    public void setResourceWidget(Widget widget) {
        innerContainer.clear();
        innerContainer.add(widget);
    }
}
