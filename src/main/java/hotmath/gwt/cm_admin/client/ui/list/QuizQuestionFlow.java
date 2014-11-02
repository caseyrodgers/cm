package hotmath.gwt.cm_admin.client.ui.list;

import hotmath.gwt.cm_core.client.model.QuizQuestionModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;


public class QuizQuestionFlow extends FlowLayoutContainer   {
    
    QuizQuestionModel selectedQuestion;
    private List<QuizQuestionModel> questions = new ArrayList<QuizQuestionModel>();
    private Callback callback;
    

    static public interface Callback {
        void questionCountChanged(int count);
    }
    
    public QuizQuestionFlow(Callback callback) {
        setCallback(callback);
        setScrollMode(ScrollMode.AUTO);
        addStyleName("quiz-question-flow");
    }
    
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    interface Unselector {
        void unselect();
    }

    Unselector unselector = new Unselector() {
        @Override
        public void unselect() {
            for(int i=0;i< getWidgetCount();i++) {
                Widget w = getWidget(i);
                w.getElement().setAttribute("style", "");
            }
        }
    };
    
    public void setQuestions(List<QuizQuestionModel> questions) {
        this.questions.clear();
        
        clear();
        for(QuizQuestionModel q: questions) {
            addQuestion(q);
        }
    }

    public QuizQuestionModel getSelectedQuestion() {
        return selectedQuestion;
    }
    
    
    class MyFocusPanel extends FocusPanel {
        private QuizQuestionModel questionModel;
        private Unselector unselector;

        public MyFocusPanel(final Unselector unselector, final QuizQuestionModel question) {
            this.unselector =  unselector;
            this.questionModel = question;
            setWidget(new HTML(question.getQuestion()));
            
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    markQuestionAsSelected();
                    selectedQuestion = question;
                }
            });
        }
        
        protected void markQuestionAsSelected() {
            unselector.unselect();
            getElement().setAttribute("style",  "border: 1px solid black;background: #E6E6E6");
        }

        public QuizQuestionModel getQuestionModel() {
            return this.questionModel;
        }
    }


    public List<QuizQuestionModel> getQuestions() {
        // TODO Auto-generated method stub
        return questions;
    }

    public void setSelectedQuestion(QuizQuestionModel q) {
        MyFocusPanel mfp = lookupFocusPanel(q);
        if(mfp != null) {
            mfp.markQuestionAsSelected();
            getScrollSupport().ensureVisible(mfp);
        }
        
        this.selectedQuestion = q;
    }
    
    private MyFocusPanel lookupFocusPanel(QuizQuestionModel q) {
        for(int i=0;i<getWidgetCount();i++) {
            Widget w = getWidget(i);
            if(w instanceof MyFocusPanel) {
                MyFocusPanel mfp = (MyFocusPanel)w;
                
                if(mfp.getQuestionModel() == q) {
                    return mfp;
                }
            }
        }
        return null;
    }

    public void removeAllQuestions() {
        clear();
        this.questions.clear();
        this.selectedQuestion = null;
    }

    public void addQuestion(QuizQuestionModel quizQuestionModel) {
        this.questions.add(quizQuestionModel);
        MyFocusPanel focusPanel = new MyFocusPanel(unselector, quizQuestionModel);
        add(focusPanel);
        

        doCallback();
    }
    
    private void doCallback() {
        if(callback != null) {
            callback.questionCountChanged(this.questions.size());
        }
    }

    public void removeQuestion(QuizQuestionModel question) {
        this.questions.remove(question);
        MyFocusPanel mfp = lookupFocusPanel(question);
        remove(mfp);
        
        if(this.selectedQuestion == question) {
            this.selectedQuestion = null;
        }
        
        doCallback();
    }

    public void refreshList() {
        clear();
        for(QuizQuestionModel q: questions) {
            MyFocusPanel focusPanel = new MyFocusPanel(unselector, q);
            add(focusPanel);
        }
    }
    
}
