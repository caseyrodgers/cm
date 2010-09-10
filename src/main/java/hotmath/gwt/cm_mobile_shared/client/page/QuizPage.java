package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ControlPanel;
import hotmath.gwt.cm_mobile_shared.client.QuizPanel;

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
    public void setupControlFloater() {
        ControlPanel cp = CatchupMathMobileShared.__instance.getControlPanel();
        List<ControlAction> actions = new ArrayList<ControlAction>();
        actions.add(new ControlAction("Check Quiz") {
            @Override
            public void doAction() {
                quizPanel.checkTest();
            }
        });        
        cp.setControlActions(actions);
        cp.showControlPanelFloater();
    }
}
