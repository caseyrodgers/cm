package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.QuizPanel;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage.BackAction;

import java.util.ArrayList;
import java.util.List;


public class QuizPage implements IPage {
    QuizPanel quizPanel;
    
    public QuizPage() {
    }
    
    public void setQuizPanel(QuizPanel quizPanel) {
        this.quizPanel = quizPanel;
    }
    
	@Override
	public String getTitle() {
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
}
