package hotmath.gwt.cm_search.client.view;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class HeaderView extends SimplePanel {
    
    interface MyUiBinder extends UiBinder<Widget, HeaderView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField SpanElement title;
    @UiField Button backButton;

    private HeaderCallback _callBack;
    
    public HeaderView(){
        setWidget(uiBinder.createAndBindUi(this));
        
        
        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                _callBack.goBack();
            }
        });        
    }

    public HeaderView(HeaderCallback callBack) {
        this();
        _callBack = callBack;
        
    }
    
    


    
    
    public interface HeaderCallback {
        void goBack();
    }
    
    public void setCallback(HeaderCallback callback) {
        this._callBack = callback;
    }
    
    public void setHeaderTitle(String title) {
        this.title.setInnerHTML(title);
    }

}
