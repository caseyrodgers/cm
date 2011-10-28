package hotmath.gwt.cm_mobile3.client.view;


import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.HasWhiteboard;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class PrescriptionLessonResourceResultsViewImpl extends AbstractPagePanel implements PrescriptionLessonResourceResultsView, HasWhiteboard{

	ProblemNumber problem;
	
    FlowPanel contentPanel;
    
	public PrescriptionLessonResourceResultsViewImpl() {
	    contentPanel = new FlowPanel();
	    initWidget(contentPanel);
	    setStyleName("prescriptionLessonResourceResultsViewImpl");
	    
	    setupTriggers(this);
	}

	Presenter presenter;

	@Override
	public String getTitle() {
	   return "Quiz Results";
	}
	
	@Override
    public void setPresenter(Presenter p) {
		presenter = p;
		p.setupView(this);
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
    public void setQuizResults(String title, String resultHtml, String resultJson, int questionCount, int correctCount) {
        contentPanel.clear();
        contentPanel.add(new HTML("<h1>" + title + "</h1>"));
        contentPanel.add(new HTML(resultHtml));
        
        markAnswers(resultJson);
        
    }
    
    private void showWhiteboard_Gwt(String pid) {
        ShowWorkActivity activity = new ShowWorkActivity(CatchupMathMobile3.__clientFactory.getEventBus(), pid,0);
        ShowWorkView view = CatchupMathMobile3.__clientFactory.getShowWorkView();
        view.setPresenter(activity);
        CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)view));
    }
    

    private native void setupTriggers(PrescriptionLessonResourceResultsViewImpl x) /*-{
    
        $wnd.showWhiteboard_Gwt = function (pid) {
            x.@hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsViewImpl::showWhiteboard_Gwt(Ljava/lang/String;)(pid);
        };
        
    }-*/;
    
    /**
     * Parse JSONized array of HaTestRunResult objects
     * 
     * and mark each question with result state (correct,incorrect,unanswered)
     * and disable each radio button to disable each.
     * 
     * @param resultJson
     */
    static public void markAnswers(String resultJson) {
        try {
            JSONValue jsonValue = JSONParser.parse(resultJson);
            JSONArray a = jsonValue.isArray();

            for (int i = 0; i < a.size(); i++) {
                JSONObject o = a.get(i).isObject();
                String pid = o.get("pid").isString().stringValue();
                String correct = o.get("result").isString().stringValue();
                int answerIndex = (int) o.get("responseIndex").isNumber().doubleValue();

                setQuizQuestionResult(pid, correct);
//                if (!correct.equals("Unanswered"))
//                    setSolutionQuestionAnswerIndex(pid, Integer.toString(answerIndex), true);
//                else
//                    setSolutionQuestionAnswerIndex(pid, "-1", true);
            }
        } catch (Exception e) {
            Log.error("Error marking question answers");
        }
    }
    
    static private native void setQuizQuestionResult(String pid, String result) /*-{
        $wnd.setQuizQuestionResult(pid, result);
    }-*/;    
}
