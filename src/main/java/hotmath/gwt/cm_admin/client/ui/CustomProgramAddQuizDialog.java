package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizId;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction.ActionType;
import hotmath.gwt.shared.client.rpc.action.GetCustomQuizAction;
import hotmath.gwt.shared.client.rpc.action.GetLessonQuestionsAction;
import hotmath.gwt.shared.client.rpc.action.SaveCustomQuizAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Widget;

public class CustomProgramAddQuizDialog extends Window {

    ListView<CustomLessonModel> _listLessons = new ListView<CustomLessonModel>();
    ListView<QuizQuestionModel> _listQuestions = new ListView<QuizQuestionModel>();
    ListView<QuizQuestionModel> _listCustomQuiz = new ListView<QuizQuestionModel>();
    
    LayoutContainer _mainPanel = new LayoutContainer();
    TabPanel _tabPanel;
    TabItem _tabLessons;
    TabItem _tabQuestions;
    CustomLessonModel _selectedLesson;
    ContentPanel _panelQuestions;
    Callback callback;
    TextField<String> _textQuizName;
    Button _saveButton;
    CustomQuizDef _customQuiz;
    
    int adminId;

    public CustomProgramAddQuizDialog(Callback callback, CustomQuizDef quiz) {
        this.callback = callback;
        this._customQuiz = quiz;
        
        adminId = StudentGridPanel.instance._cmAdminMdl.getId();
        
        setId("custom_quiz_design");
        
        setHeading("Define Custom Quiz");
        setSize(800,700);
        
        addStyleName("custom-program-add-quiz-dialog");
        
        _saveButton = new Button("Save", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                saveCustomQuiz();
            }
        });
        
        addButton(_saveButton);
        
        addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        _mainPanel = createBodyPanel();
        
        _listCustomQuiz.setStore(new ListStore<QuizQuestionModel>());
        _listCustomQuiz.setTemplate("<tpl for=\".\"><div style='padding: 15px;white-space: normal' class='x-view-item'>{question}</div></tpl>");

        ListStore<CustomLessonModel> storeAll = new ListStore<CustomLessonModel>();
        storeAll.setStoreSorter(new StoreSorter<CustomLessonModel>() {
            @Override
            public int compare(Store<CustomLessonModel> store, CustomLessonModel m1, CustomLessonModel m2,
                    String property) {
                    if (property != null) {
                        String v1 = m1.getLesson();
                        String v2 = m2.getLesson();
                        return comparator.compare(v1, v2);
                    }            
                    return super.compare(store, m1, m2, property);
                }            
        });

        _listLessons.setStore(storeAll);
        String template = "<tpl for=\".\"><div class='x-view-item'><span style='font-size:.5em;width: 5px;' class='{subjectStyleClass}'>&nbsp;</span>&nbsp;{" + "customProgramItem" + "}</div></tpl>";
        _listLessons.setTemplate(template);
        _tabLessons.add(_listLessons);
        _tabLessons.setScrollMode(Scroll.AUTO);
        
        _listLessons.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                // loadQuestionsFor(_listLessons.getSelectionModel().getSelectedItem());
                _tabPanel.setSelection(_tabQuestions);
            }
        });
        
        _listQuestions.setStore(new ListStore<QuizQuestionModel>());
        _listQuestions.setTemplate("<tpl for=\".\"><div style='padding: 15px;white-space: normal' class='x-view-item'>{question}</div></tpl>");
        _panelQuestions = createQuestionListPanel(_listQuestions);
        _tabQuestions.add(_panelQuestions);
        _tabQuestions.setEnabled(false);
        _tabQuestions.setLayout(new FitLayout());
        
        _listQuestions.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                addSelectedQuestionToCustomQuiz();
            }
        });
        
        
        _tabPanel.addListener(Events.Select, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if(_tabPanel.getSelectedItem().getText().equalsIgnoreCase("questions")) {
                    loadQuestionsFor(_listLessons.getSelectionModel().getSelectedItem());
                }
            }
        });
        
        _listLessons.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<CustomLessonModel>() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent<CustomLessonModel> se) {
                _tabQuestions.setEnabled(true);
            }
        });
        
        setLayout(new BorderLayout());
        add(createTopPanel(), new BorderLayoutData(LayoutRegion.NORTH, 40));
        add(_mainPanel, new BorderLayoutData(LayoutRegion.CENTER));
        
        CustomProgramDesignerDialog.createButtonBarLedgend(getButtonBar());
        
        _listCustomQuiz.getStore().addStoreListener(new StoreListener<QuizQuestionModel>() {
            @Override
            public void handleEvent(StoreEvent<QuizQuestionModel> e) {
                showQuestionsInCustomQuiz(_listCustomQuiz.getStore().getModels(), _listCustomQuiz.getId());
            }
        });
        

        getAllLessonData();
        
        if(_customQuiz != null) {
            loadCustomQuiz(_customQuiz);
        }
        else {
            _customQuiz = new CustomQuizDef(0, "New Custom Quiz",adminId);
        }
        _textQuizName.setValue(_customQuiz.getQuizName());
        
        setVisible(true);
    }
    
    private void addSelectedQuestionToCustomQuiz() {
        QuizQuestionModel question = _listQuestions.getSelectionModel().getSelectedItem();
        if(question != null) {
            _listCustomQuiz.getStore().add(new QuizQuestionModel(question));
            markSelectedItem(_listCustomQuiz, question);
        }
    }
    
    private void removeSelectedQuestionFromCustomQuiz() {
        QuizQuestionModel question = _listCustomQuiz.getSelectionModel().getSelectedItem();
        if(question != null) {
            _listCustomQuiz.getStore().remove(question);
        }
    }
    
    private void removeAllSelectedQuestionFromCustomQuiz() {
        MessageBox.confirm("Remove Questions", "Remove all questions from custom quiz?",new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if(!be.isCancelled()) {
                    _listCustomQuiz.getStore().removeAll();
                }
            }
        });
    }
    
    
    private void loadCustomQuiz(final CustomQuizDef quiz) {
        new RetryAction<CmList<QuizQuestion>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                GetCustomQuizAction action = new GetCustomQuizAction(quiz.getQuizId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<QuizQuestion> questions) {
                CmBusyManager.setBusy(false);
                
                _listCustomQuiz.getStore().removeAll();
                List<QuizQuestionModel> models = new ArrayList<QuizQuestionModel>();
                for(int i=0,t=questions.size();i<t;i++) {
                    QuizQuestion q = questions.get(i);
                    models.add(new QuizQuestionModel(q));
                }
                _listCustomQuiz.getStore().add(models);
            }
        }.register();                
    }
    
    private void saveCustomQuiz() {
        try {
            saveCustomQuizAux();
        }
        catch(Exception e) {
            com.google.gwt.user.client.Window.alert("Validation Error: " + e.getMessage());
        }
    }
    
    private void saveCustomQuizAux() throws Exception {
        
        final String cpName = _textQuizName.getValue();
        if(cpName.length() == 0) {
            throw new Exception("Name must be specified");
        }
        final List<CustomQuizId> ids = getCustomQuizIds();
        if(ids.size() == 0) {
            throw new Exception("No questions in quiz");
        }

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                SaveCustomQuizAction action = new SaveCustomQuizAction(adminId, cpName, ids);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData rpcData) {
                CmBusyManager.setBusy(false);
                callback.quizCreated();
                hide();
            }
        }.register();        
    }
    
    private List<CustomQuizId> getCustomQuizIds() {
        List<QuizQuestionModel> models = _listCustomQuiz.getStore().getModels();
        
        List<CustomQuizId> quizIds = new ArrayList<CustomQuizId>();
        for(int i=0,t=models.size();i<t;i++) {
            QuizQuestionModel question = models.get(i);
            quizIds.add(new CustomQuizId(question.getPid(), i));
        }
        
        return quizIds;
    }
    
    private void showQuestionHtml(QuizQuestionModel quizModel) {
        CmWindow questionWindow = new CmWindow();
        questionWindow.setHeading("Question: " + quizModel.getQuestionId());
        questionWindow.setModal(true);
        questionWindow.setSize(500, 300);
        
        Html html = new Html(quizModel.getQuestion());
        questionWindow.add(html);
        
        questionWindow.addCloseButton();
        
        questionWindow.setVisible(true);
    }
    
    static CmList<CustomLessonModel> __allLessons;
    private void getAllLessonData() {
        if(__allLessons != null) {
            _listLessons.getStore().removeAll();
            _listLessons.getStore().add(__allLessons);
            return;
        }
        
        new RetryAction<CmList<CustomLessonModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramAction action = new CustomProgramAction(ActionType.GET_ALL_LESSONS);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomLessonModel> allLessons) {
                CmBusyManager.setBusy(false);                
                __allLessons = allLessons;
                _listLessons.getStore().removeAll();
                _listLessons.getStore().add(__allLessons);
            }
        }.register();
    }
    
    
    CmList<QuizQuestion> _questions;
    
    private void loadQuestionsFor(final CustomLessonModel lesson) {
        if(lesson == null) {
            InfoPopupBox.display("No Questions", "Choose a lesson and then click this tab");
            return;
        }
        
        CmLogger.info("Loading questions for: " + lesson);
        
        _listQuestions.getStore().removeAll();
        _panelQuestions.setHeading("Questions For: " + lesson.getLesson());
        new RetryAction<CmList<QuizQuestion>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetLessonQuestionsAction action = new GetLessonQuestionsAction(lesson.getFile(), lesson.getSubject());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<QuizQuestion> questions) {
                CmBusyManager.setBusy(false);    
                _questions = questions;
                
                showQuestionsAll(_questions);
            }
        }.register();        
    }

    /** replace loaded questions in list
     * 
     * @param questions
     */
    private void showQuestionsAll(CmList<QuizQuestion> questions) {
        _listQuestions.getStore().removeAll();
        List<QuizQuestionModel> models = new ArrayList<QuizQuestionModel>();
        JsArrayInteger answers = createArray();
        for(int i=0,t=questions.size();i<t;i++) {
            QuizQuestion question = questions.get(i);
            models.add(new QuizQuestionModel(question));
            
            answers.push(question.getCorrectAnswer());
        }
        _listQuestions.getStore().add(models);
        
        String id = _listQuestions.getId();
        hideAnswerResults(id, answers);
    }
    
    private void showQuestionsInCustomQuiz(List<QuizQuestionModel> questions, String id) {
        JsArrayInteger answers = createArray();
        for(int i=0,t=questions.size();i<t;i++) {
            QuizQuestionModel question = questions.get(i);
            answers.push(question.getCorrectAnswer());
        }
        hideAnswerResults(id, answers);
    }
    
    private native JsArrayInteger  createArray() /*-{
        return [];
    }-*/;

    private native void hideAnswerResults(String id, JsArrayInteger answers) /*-{
        try {
            var questionList = $doc.getElementById(id);
            $wnd.prepareCustomQuizForDisplay(questionList, answers);
        }
        catch(e) {
            alert(e);
        }
   }-*/;
    
    private LayoutContainer createBodyPanel() {
        LayoutContainer lc = new LayoutContainer();
        lc.addStyleName("custom-program-questions-container");

        lc.setLayout(new BorderLayout());
        
        ContentPanel cpLeft = new ContentPanel();
        //cpLeft.setHeading("Available Questions");
        cpLeft.getHeader().setVisible(false);
        cpLeft.setLayout(new FitLayout());
        
        _tabPanel = new TabPanel();
        
        _tabLessons = new TabItem("All Lessons");
        _tabLessons.setScrollMode(Scroll.AUTO);
        _tabPanel.add(_tabLessons);
        
        
        _tabQuestions = new TabItem("Questions");
        _tabQuestions.setScrollMode(Scroll.AUTO);
        _tabPanel.add(_tabQuestions);
        cpLeft.add(_tabPanel);
        BorderLayoutData ld = new BorderLayoutData(LayoutRegion.WEST, 400);
        ld.setSplit(true);
        lc.add(cpLeft, ld);
        
        
        ContentPanel cpRight = new ContentPanel();
        cpRight.setHeading("Questions in Custom Quiz");
        cpRight.add(_listCustomQuiz);
        cpRight.setLayout(new FitLayout());
        lc.add(cpRight, new BorderLayoutData(LayoutRegion.CENTER));
        
        cpRight.getHeader().addTool(new MyButtonWithTip("Remove","Remove the selected question from the custom quiz",  new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                removeSelectedQuestionFromCustomQuiz();
            }
        }));
        cpRight.getHeader().addTool(new MyButtonWithTip("Remove All","Remove all questions from the custom quiz",  new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                removeAllSelectedQuestionFromCustomQuiz();
            }
        }));
        
        cpRight.getHeader().addTool(new MyButtonWithTip("Move Down","Move the selected question down in the custom quiz",  new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                moveSelectedQuestionInProgramDown();
            }
        }));
        
        cpRight.getHeader().addTool(new MyButtonWithTip("Move Up","Move the selected question up in the custom quiz",  new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                moveSelectedQuestionInProgramUp();
            }
        }));
        return lc;  
    }
    
    private void moveSelectedQuestionInProgramDown() {
        if(_listCustomQuiz.getStore().getCount() > 1) {
            ListStore<QuizQuestionModel> str = _listCustomQuiz.getStore();
            QuizQuestionModel question = _listCustomQuiz.getSelectionModel().getSelectedItem();
            if(question != null) {
                int num = getQuestionNum(str.getModels(), question);
                if(num+1 < str.getCount()) {
                    _listCustomQuiz.getStore().remove(question);
                    _listCustomQuiz.getStore().insert(question, num+1);
                
                    markSelectedItem(_listCustomQuiz, question);
                }
            }
        }
    }

    private void moveSelectedQuestionInProgramUp() {
        if(_listCustomQuiz.getStore().getCount() > 1) {
            ListStore<QuizQuestionModel> str = _listCustomQuiz.getStore();
            QuizQuestionModel question = _listCustomQuiz.getSelectionModel().getSelectedItem();
            if(question != null) {
                int num = getQuestionNum(str.getModels(), question);
                if(num > 0) {
                    _listCustomQuiz.getStore().remove(question);
                    _listCustomQuiz.getStore().insert(question, num-1);
                    markSelectedItem(_listCustomQuiz, question);
                }
            }
        }        
    }

    private int getQuestionNum(List<QuizQuestionModel> models,QuizQuestionModel question ) {
        for(int i=0, t=models.size();i<t;i++) {
            QuizQuestionModel qqm = models.get(i);
            if(qqm == question) {
                return i;
            }
        }    
        return -1;
    }
    
    /** Select the named question in given listview
     * 
     * @param list
     * @param question
     */
    private void markSelectedItem(ListView<QuizQuestionModel> list, QuizQuestionModel question) {
        List<QuizQuestionModel> selection = new ArrayList<QuizQuestionModel>();
        selection.add(question);
        list.getSelectionModel().setSelection(selection);
    }
    
    private ContentPanel createQuestionListPanel(Widget widget) {
        ContentPanel cp = new ContentPanel();
        cp.setLayout(new BorderLayout());
        cp.setHeading("Questions");
        cp.setLayout(new FitLayout());
        cp.add(widget);
        
        
        cp.getHeader().addTool(new MyButtonWithTip("Add", "Add the selected lesson question to custom program", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                addSelectedQuestionToCustomQuiz();
            }
        }));
        return cp;
    }
    

    
    private Widget createTopPanel() {
        FormPanel form = new FormPanel();
        form.setFrame(false);
        form.setBodyBorder(false);
        form.setHeaderVisible(false);
        form.setLabelWidth(75);
        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new FitLayout());
        lc.addStyleName("custom-quiz-top-panel");
        _textQuizName = new TextField<String>();
        _textQuizName.setAllowBlank(false);
        
        //loadExistingCustomQuizNames();
        
        _textQuizName.setFieldLabel("Quiz Name");
        form.add(_textQuizName);
        lc.add(form);
        
        FormButtonBinding binding = new FormButtonBinding(form);  
        binding.addButton(_saveButton);         
        
        return lc;
    }
    
    static public interface Callback {
        void quizCreated();
    }
}


class QuizQuestionModel extends BaseModelData {
    public QuizQuestionModel(QuizQuestion question) {
        set("lesson", question.getLesson());
        set("programName", question.getProgramName());
        set("question", question.getQuizHtml());
        set("questionId", question.getQuestionId());
        set("pid", question.getPid());
        set("correctAnswer", question.getCorrectAnswer());
    }
    
    /** TODO: DNRY
     * 
     * @param model
     */
    public QuizQuestionModel(QuizQuestionModel model) {
        set("lesson", model.get("lesson"));
        set("programName", model.get("programName"));
        set("questionId", model.get("questionId"));
        set("pid", model.get("pid"));
        set("correctAnswer", model.get("correctAnswer"));
        
        /** and even uglier:
         * 
         * The html that makes up the question has a set
         * of radio buttons, so we have to change the name
         * otherwise, when we set the value it unsets the 
         * the orginal.
         * 
         */
        set("question", createUniqRadioButtonName(model.get("question").toString()));

    }
    
    private String createUniqRadioButtonName(String html) {
        //html = "before name=\"question_256\" after"; 
        String newNameTag = "quiz_question_" + Random.nextInt(1000);
        String ret = html.replaceAll("name=\"question_[0-9]*\"", "\"" + newNameTag + "\"");
        return ret;
    }
    
    public String getQuestion() {
        return get("question");
    }
    
    public String getLesson() {
        return get("lesson");
    }
    
    public String getProgram() {
        return get("programName");
    }
    
    public String getQuestionId() {
        return get("questionId");
    }
    
    public String getPid() {
        return get("pid");
    }
    
    public Integer getCorrectAnswer() {
        return get("correctAnswer");
    }
}

class MyButtonWithTip extends Button {
    MyButtonWithTip(String name, String tip, SelectionListener<ButtonEvent> be) {
        super(name, be);
        setToolTip(tip);
    }
}