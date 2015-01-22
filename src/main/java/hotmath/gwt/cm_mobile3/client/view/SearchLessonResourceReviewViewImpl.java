package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.view.SubToolBar;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;

public class SearchLessonResourceReviewViewImpl extends AbstractPagePanel implements SearchLessonResourceReviewView {

    Presenter presenter;
    private SexyButton _languageButton;
    
    public SearchLessonResourceReviewViewImpl() {
        FlowPanel main = new FlowPanel();
        SubToolBar subBar = new SubToolBar();
        main.add(subBar);
        
        
        if(_languageButton == null) {
            _languageButton = new SexyButton("Spanish", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    
                    presenter.loadLesson(SearchLessonResourceReviewViewImpl.this, _languageButton.getText().contains("Spanish"), new CallbackOnComplete() {
                        @Override
                        public void isComplete() {
                            System.out.println("Complete");
                        }
                    
                    });
                    
                    if(_languageButton.getText().contains("Spanish")) {
                        _languageButton.setButtonText("English",null);
                    }
                    else {
                        _languageButton.setButtonText("Spanish",null);
                    }
                }
            });
        }
        subBar.add(_languageButton);
        
        reviewHtml = new HTMLPanel("");
        reviewHtml.addStyleName("prescriptionLessonResourceReviewImpl");
        main.add(reviewHtml);
        initWidget(main);
    }
    
    @Override
    public String getHeaderBackground() {
        return "#7F2909";
    }

    @Override
    public String getViewTitle() {
        return "Search Lesson";
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        presenter.setupView(this);
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
    public void setHeaderTitle(String title) {
        // reviewTitle.setInnerHTML(title);
    }
 
    HTMLPanel reviewHtml;

    @Override   
    public void setReviewHtml(String html) {
        reviewHtml.clear();
        reviewHtml.add(new HTML(html));
    }
    
    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            
            @Override
            public boolean goBack() {
                return true;
            }
        };
    }

    @Override
    public void loadLesson(String title, String lesson) {
        setReviewHtml(lesson);
    }
}