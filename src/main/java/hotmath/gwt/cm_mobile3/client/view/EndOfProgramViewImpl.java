package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewEvent;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class EndOfProgramViewImpl extends AbstractPagePanel implements EndOfProgramView {

    Presenter presenter;
    
    
    private static EndOfProgramViewImpllUiBinder uiBinder = GWT.create(EndOfProgramViewImpllUiBinder.class);

    interface EndOfProgramViewImpllUiBinder extends UiBinder<Widget, EndOfProgramViewImpl> {
    }

    public EndOfProgramViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        addStyleName("endOfProgramViewImpl");
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
    public String getTitle() {
        return "End Of Program";
    }

    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            
            @Override
            public boolean goBack() {
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new ShowLoginViewEvent());
                return false;
            }
        };
    }
}
