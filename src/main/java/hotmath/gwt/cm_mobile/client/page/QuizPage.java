package hotmath.gwt.cm_mobile.client.page;

import hotmath.gwt.cm_mobile.client.CatchupMathMobile;
import hotmath.gwt.cm_mobile.client.ControlAction;
import hotmath.gwt.cm_mobile.client.ControlPanel;
import hotmath.gwt.cm_mobile.client.Controller;
import hotmath.gwt.cm_mobile.client.QuizPanel;

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
        ControlPanel cp = CatchupMathMobile.__instance.getControlPanel();
        List<ControlAction> actions = new ArrayList<ControlAction>();
        actions.add(new ControlAction("Logout") {
            @Override
            public void doAction() {
                Controller.navigateBack();
            }
        });
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
