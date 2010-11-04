package hotmath.gwt.cm_activity.client;


import hotmath.gwt.cm_activity.client.AnswerInputPanel.AnswerCallback;
import hotmath.gwt.cm_activity.client.model.WordProblem;
import hotmath.gwt.cm_activity.client.rpc.GetWordProblemSetAction;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.CmEventListener;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class WordProblemsPanel extends Composite {

    interface MyUiBinder extends UiBinder<Widget, WordProblemsPanel> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    @UiField FlowPanel questionHtmlPanel;
    @UiField AnswerInputPanel answerPanel;
    @UiField FlowPanel questionDescribe;
    @UiField Button answerButton;
    @UiField Button explainButton;
    @UiField Label statusLabel;
    
    
    WordProblemSet _problemSet;
    UserScore _userScore = new UserScore();
    FlowPanel _mainPanel = new FlowPanel();
    Widget _questionPanel;
    ExplainPanel _explainPanel;
    ResultsPanel _resultsPanel;
    
    public WordProblemsPanel() {
        
        _mainPanel.setStyleName("activity");
        _questionPanel = uiBinder.createAndBindUi(this);
        _explainPanel = new ExplainPanel();
        _resultsPanel = new ResultsPanel();
        
        _mainPanel.add(_questionPanel);
        initWidget(_mainPanel);
        
        answerButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                checkAnswer();
            }
        });
        
        explainButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showExplain();
            }
        });
        
        answerPanel.setCallbackOnAnswer(new AnswerCallback() {
            @Override
            public void doComplete(AnswerInputPanel o) {
                checkAnswer();
            }
        });
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventTypes.EVENT_INITIALIZE) {
                    initializeData((WordProblemSet)event.getEventData());
                }
                else if(event.getEventType() == EventTypes.EVENT_MOVE_TO_NEXT_QUESTION) {
                    loadQuestion(_currentQuestion+1);
                }
                else if(event.getEventType() == EventTypes.EVENT_SHOW_INPUT_SUBMIT) {
                    checkAnswer();
                }
            }
        });
        
        getProblemSetData();
    }
    
    
    
    private void getProblemSetData() {
        GetWordProblemSetAction action = new GetWordProblemSetAction();
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<WordProblemSet>() {
            @Override
            public void onSuccess(WordProblemSet problemSet) {
                initializeData(problemSet);
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error logging in: " + caught);
            }
        });
    }

    
    
    private void showExplain() {
        if(_problemSet == null) {
            showPopup(explainButton, "No questions loaded", null);
            return;
        }
        _mainPanel.clear();
        _explainPanel.setExplaination(_problemSet.getProblems().get(_currentQuestion));
        _mainPanel.add(_explainPanel);
    }
    
    private native boolean validateQuestion(String expected,String vars,String value1, String value2, String value3, String value3p)/*-{
        return $wnd.validateWordProblem1(expected,vars,value1,value2,value3,value3p);
    }-*/;
    private void checkAnswer() {
        QuestionAnswer answer = getAnswer();
        
        WordProblem problem = _problemSet.getProblems().get(_currentQuestion);
        
        String vars = problem.getVars();
        String expected=problem.getAnswer();
        String value1=answer.value1;
        String value2 = answer.value2;
        String value3 = answer.value3;
        String value3p = answer.value3p;
        
        if(value1.length() == 0 || value1.equals("?")) {
            showPopup(answerPanel,"Enter a valid expression.",null);
        }
        else {
            ;
            if(validateQuestion(expected, vars, value1,value2,value3,value3p)) {
                showCorrectAnswer();
            }
            else {
                showIncorrectAnswer();
            }
        }
    }
    
    private boolean isCorrect(String value) {
       return value.equals("1+1");    
    }
    
    private void showIncorrectAnswer() {
        showPopup(answerPanel, "<div style='text-align: center'>Incorrect</b>",new Callback() {
            @Override
            public void doCallback() {
                showExplain();
            }
        }); 
    }
    
    private void showPopup(Widget source, String message, final Callback callBack) {
        // Create a basic popup widget
        final DecoratedPopupPanel simplePopup = new DecoratedPopupPanel(true);
        simplePopup.ensureDebugId("cwBasicPopup-simplePopup");
        
        message = "<div style='padding:15px;color: red;' class='message_popup'>" + message + "</div>";
        
        FlowPanel popupWidget = new FlowPanel();
        simplePopup.setWidget( popupWidget );
        
        popupWidget.add( new HTML(message));
        
        // Reposition the popup relative to the button
        int left = source.getAbsoluteLeft() - 10;
        int top = source.getAbsoluteTop() - 10;
        simplePopup.setPopupPosition(left, top);

        simplePopup.setAnimationEnabled(true);
        // Show the popup
        simplePopup.show();
        if(callBack != null) {
            
            final Button btn = new Button("Continue", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    simplePopup.hide(true);
                }
            });
            popupWidget.add(btn);
            simplePopup.addCloseHandler(new CloseHandler<PopupPanel>() {
                
                @Override
                public void onClose(CloseEvent<PopupPanel> event) {
                    callBack.doCallback();                
                }
            });
            new Timer() {
                @Override
                public void run() {
                    btn.setFocus(true);
                }
            }.schedule(500);
        }
        else { 
            new Timer() {
                @Override
                public void run() {
                    simplePopup.hide(true);
                }
            }.schedule(5000);
        }
    }
    
    
    private void showCorrectAnswer() {
        showPopup(answerPanel, "<div style='text-align: center;color: green;font-size: 1.2em;'>Correct!</b>",new Callback() {
            @Override
            public void doCallback() {
                loadQuestion(_currentQuestion+1);
            }
        }); 
    }
    
    private void showResults() {
        _mainPanel.clear();
        _resultsPanel.setScore(_userScore);
        _mainPanel.add(_resultsPanel);
    }
    
    public QuestionAnswer getAnswer() {
        String text = answerPanel.getText();
        return new QuestionAnswer(text);
    }
    
    int _currentQuestion=0;
    private void loadQuestion(int which) {
        if(which > _problemSet.getProblems().size()-1) {
            showResults();
            return;
        }
        _currentQuestion=which;
        WordProblem problem = _problemSet.getProblems().get(which);
        
        statusLabel.setText((which+1) + " of 10");
        String describe = "Write an algebraic expression for the statement.";        
        questionDescribe.getElement().setInnerHTML(describe);
        questionHtmlPanel.getElement().setInnerHTML(problem.getQuestion());
        answerPanel.resetPanel();
        
        _mainPanel.clear();
        _mainPanel.add(_questionPanel);
    }
    
    
    private void initializeData(WordProblemSet problemSet) {

        if(problemSet != null)
            _problemSet = problemSet;
        _currentQuestion = 0;
        
        _mainPanel.clear();
        _mainPanel.add(_questionPanel);

        loadQuestion(0);

        answerPanel.resetPanel();
        answerPanel.setFocus(true);
    }
    
    CmActivityWordProblemsResources resources = GWT.create(CmActivityWordProblemsResources.class);
    public CmActivityWordProblemsResources getRes() {
        return resources;
    }
    

    static public interface Callback {
        void doCallback();
    }
    
    static public class QuestionAnswer {
        public String value1;
        public String value2;
        public String value3;
        public String value3p;
        
        public QuestionAnswer(String text) {
            try {
            String p[] = text.split("\\+");
            if(p.length > 3) {
                value3p = p[3];
            }
            if(p.length > 2) {
                value3 = p[2];
            }
            if(p.length > 1) {
                value2 = p[1];
            }
            value1 = p[0];
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        public QuestionAnswer(String v1, String v2, String v3, String v3p) {
            this.value1 = v1;
            this.value2 = v2;
            this.value3 = v3;
            this.value3p = v3p;            
        }
    }
}

