package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_tools.client.CatchupMathTools;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public class ShowWorkViewImpl extends AbstractPagePanel implements ShowWorkView {
    

    Presenter presenter;
    
    String title;
    public ShowWorkViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    interface MyUiBinder extends UiBinder<Widget, ShowWorkViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        
        showProblem.setValue(false);
        setProblemStatement();
        presenter.prepareShowWorkView(this);
    }

    @Override
    public String getTitle() {
        return "Show Work for " + title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
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
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                return true;
            }
        };
    }
    
    @UiHandler("showProblem")
    protected void handleShowProblem(ClickEvent ce) {
        setProblemStatement();
    }
    

    private void setProblemStatement() {
        if(showProblem.getValue()) {
            canvasBackground.setInnerHTML("<div>" + presenter.getProblemStatementHtml() + "</div>");
            canvasBackground.setAttribute("style", "display: block");
            
            initializeWidgets();
        }
        else {
            canvasBackground.setAttribute("style", "display: none");
            canvasBackground.setInnerHTML("");
        }        
    }
    
    native private void initializeWidgets() /*-{
        AuthorApi.initializeWidgets();
    }-*/; 
    
    @UiField
    CheckBox showProblem;
    
    @UiField
    Element canvasBackground;
}