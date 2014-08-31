package hotmath.gwt.cm_mobile3.client.view;


import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.HasWhiteboard;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkSubToolBar;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkSubToolBar.Callback;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;

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
	
    FlowPanel _mainContainer;
    
    ShowWorkPanel2 _showWork;
    ShowWorkSubToolBar _subBar;
    private FlowPanel _whiteboardWrapper;
    private String _lastPid;
    
	public PrescriptionLessonResourceResultsViewImpl() {
	    _mainContainer = new FlowPanel();
	    initWidget(_mainContainer);
	    setStyleName("prescriptionLessonResourceResultsViewImpl");
	    
	    
	    _subBar = new ShowWorkSubToolBar(false,  false,  new Callback() {
            
            @Override
            public void whiteboardSubmitted() {
            }
            
            @Override
            public void showWhiteboard() {
            }
            
            @Override
            public void showProblem(boolean b) {
                _showWork.setBackground(b);
            }
            
            @Override
            public List<LessonModel> getProblemLessons() {
            	return null;
            }
            @Override
            public void hideWhiteboard() {
                PrescriptionLessonResourceResultsViewImpl.this.hideWhiteboard();
            }
        });
	}

	Presenter presenter;

	@Override
	public String getViewTitle() {
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
        _mainContainer.clear();
        _mainContainer.add(new HTML("<h1>" + title + "</h1>"));
        _mainContainer.add(new HTML(resultHtml));
        
        markAnswers(resultJson);
        
    }


    
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

    @Override
    public void showWhiteboard(String pid) {
        CmGwtUtils.jsni_positionQuestionToTopOfViewable(pid);
        
        _lastPid = pid;
        
        if(_showWork != null) {
            hideWhiteboard();
        }
        _subBar.setupWhiteboardTools(true);
        
         _whiteboardWrapper = new FlowPanel();
         _whiteboardWrapper.add(_subBar);
        _showWork = new ShowWorkPanel2(new ShowWorkPanelCallbackDefault() {
            @Override
            public void windowResized() {
            }

            @Override
            public void showWorkIsReady(ShowWorkPanel2 showWork) {
                presenter.loadWhiteboard(_showWork, _lastPid);                
            }

            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                SaveWhiteboardDataAction action = new SaveWhiteboardDataAction(SharedData.getMobileUser().getUserId(),0, _lastPid, commandType, data);
                return action;
            }
        });
        _whiteboardWrapper.addStyleName("static_whiteboard");
        _whiteboardWrapper.add(_showWork);
        _mainContainer.add(_whiteboardWrapper);
        
        _showWork.setBackground(_subBar.getShowProblem());
        
        CmGwtUtils.addDoubleTapRemoveEvent(_showWork.getElement());
        
        // position to top of document so toolbar is visible on open.
        //Window.scrollTo(0, 0);

        CmGwtUtils.resizeElement(_mainContainer.getElement());
        CmGwtUtils.moveToTopOfViewableScreen(_whiteboardWrapper.getElement(), _whiteboardWrapper.getElement());
        
    }

    private void hideWhiteboard() {
        if(_whiteboardWrapper != null) {
            _mainContainer.remove(_whiteboardWrapper);
        }
    }    
}
