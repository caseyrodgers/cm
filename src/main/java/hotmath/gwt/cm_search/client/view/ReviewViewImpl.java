package hotmath.gwt.cm_search.client.view;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_search.client.view.HeaderView.HeaderCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReviewViewImpl extends AbstractPagePanel implements ReviewView {
    
    Presenter presenter;
    
    @UiField HTMLPanel lessonText;
    @UiField HeadingElement lessonTitle;
    @UiField HeaderView headerView;
    
    interface MyUiBinder extends UiBinder<Widget, ReviewViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    
    public ReviewViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        
        headerView.setCallback(new HeaderCallback() {
            @Override
            public void goBack() {
                History.back();
            }
        });
    }


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void loadLesson(String title, String lesson) {
        //lessonTitle.setInnerHTML(title);
        lessonText.add(new HTML(lesson));
    }
    
}
