package hotmath.gwt.cm_search.client.view;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_search.client.view.HeaderView.HeaderCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class VideoViewImpl extends AbstractPagePanel implements VideoView {
    
    Presenter presenter;
    
    @UiField HTMLPanel mainPanel;
    @UiField HeaderView headerPanel;
    
    interface MyUiBinder extends UiBinder<Widget, VideoViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    
    public VideoViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        
        headerPanel.setCallback(new HeaderCallback() {
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
    public void loadVideo(String title, Widget viewShared) {
        headerPanel.setHeaderTitle(title);
        mainPanel.add(viewShared);
    }
    
}
