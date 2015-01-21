package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.QuizPanel;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.ArrayList;
import java.util.List;


public class QuizPage implements IPage {
    
    QuizPanel quizPanel;
    
    public QuizPage() {
    }
    
    @Override
    public void isNowActive() {
        /** empty */
    }

    public void setQuizPanel(QuizPanel quizPanel) {
        this.quizPanel = quizPanel;
    }
    
	@Override
	public String getViewTitle() {
		return "Quiz Page";
	}

	@Override
	public String getBackButtonText() {
		return "Logout";
	}

    @Override
    public List<ControlAction> getControlFloaterActions() {
        List<ControlAction> actions = new ArrayList<ControlAction>();
        actions.add(new ControlAction("Check Quiz") {
            @Override
            public void doAction() {
                quizPanel.checkTest();
            }
        });
        return actions;
    }
    
    
    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }    
    
    
    @Override
    public BackAction getBackAction() {
    	return null;
    }
    

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.PROGRAM;        
    }

    @Override
    public String getHeaderBackground() {
        return null;
    }

}
