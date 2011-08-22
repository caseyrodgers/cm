package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionLessonResourceReviewViewImpl extends AbstractPagePanel implements PrescriptionLessonResourceReviewView {

    Presenter presenter;
    
    public PrescriptionLessonResourceReviewViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    interface MyUiBinder extends UiBinder<Widget, PrescriptionLessonResourceReviewViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @Override
    public String getTitle() {
        return "Lesson Review";
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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
    public void setTitle(String title) {
        // reviewTitle.setInnerHTML(title);
    }
 
    @UiField
    DivElement reviewHtml;

    @Override
    public void setReviewHtml(String html) {
        reviewHtml.setInnerHTML(html);
    }
}