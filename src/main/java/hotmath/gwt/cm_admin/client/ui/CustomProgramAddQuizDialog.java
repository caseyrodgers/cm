package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction.ActionType;
import hotmath.gwt.shared.client.rpc.action.GetLessonQuestionsAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.Widget;

public class CustomProgramAddQuizDialog extends Window {

    ListView<CustomLessonModel> _listAll = new ListView<CustomLessonModel>();
    ListView<QuizQuestionModel> _listQuestions = new ListView<QuizQuestionModel>();
    
    LayoutContainer _mainPanel = new LayoutContainer();
    TabPanel _tabPanel;
    TabItem _tabLessons;
    TabItem _tabQuestions;
    CustomLessonModel _selectedLesson;

    Callback callback;
    public CustomProgramAddQuizDialog(Callback callback) {
        this.callback = callback;
        setHeading("Define Custom Quiz");
        setSize(800,500);
        
        addStyleName("custom-program-add-quiz-dialog");
        _mainPanel = createBodyPanel();
        
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

        _listAll.setStore(storeAll);
        String template = "<tpl for=\".\"><div class='x-view-item'><span style='font-size:.5em;width: 5px;' class='{subjectStyleClass}'>&nbsp;</span>&nbsp;{" + "lesson" + "}</div></tpl>";
        _listAll.setTemplate(template);
        _tabLessons.add(_listAll);
        
        
        _listQuestions.setStore(new ListStore<QuizQuestionModel>());
        _listQuestions.setTemplate("<tpl for=\".\"><div class='x-view-item'>{question}</div></tpl>");
        _tabQuestions.add(_listQuestions);
        
        _listQuestions.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                showQuestionHtml(_listQuestions.getSelectionModel().getSelectedItem());
            }
        });
        
        _tabPanel.addListener(Events.Select, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if(_tabPanel.getSelectedItem().getText().equalsIgnoreCase("questions")) {
                    loadQuestionsFor(_listAll.getSelectionModel().getSelectedItem());
                }
            }
        });
        
        setLayout(new BorderLayout());
        add(createTopPanel(), new BorderLayoutData(LayoutRegion.NORTH, 40));
        add(_mainPanel, new BorderLayoutData(LayoutRegion.CENTER));
        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        addButton(new Button("Save", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        CustomProgramDesignerDialog.createButtonBarLedgend(getButtonBar());
        
        getAllLessonData();
        
        setVisible(true);
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
            _listAll.getStore().removeAll();
            _listAll.getStore().add(__allLessons);
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
                _listAll.getStore().removeAll();
                _listAll.getStore().add(__allLessons);
            }
        }.register();
    }
    
    
    CmList<QuizQuestion> _questions;
    
    private void loadQuestionsFor(final CustomLessonModel lesson) {
        if(lesson == null)
            return;
        
        CmLogger.info("Loading questions for: " + lesson);
        
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
                
                showQuestions(_questions);
            }
        }.register();        
    }

    /** replace loaded questions in list
     * 
     * @param questions
     */
    private void showQuestions(CmList<QuizQuestion> questions) {
        _listQuestions.getStore().removeAll();
        List<QuizQuestionModel> models = new ArrayList<QuizQuestionModel>();
        for(int i=0,t=questions.size();i<t;i++) {
            models.add(new QuizQuestionModel(questions.get(i)));
        }
        _listQuestions.getStore().add(models);
    }

    
    private LayoutContainer createBodyPanel() {
        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new RowLayout(Orientation.HORIZONTAL));
        RowData data = new RowData(.5, 1);
        data.setMargins(new Margins(5));
        
        ContentPanel cpLeft = new ContentPanel();
        cpLeft.setHeading("Available Questions");
        cpLeft.setLayout(new FitLayout());
        
        _tabPanel = new TabPanel();
        
        _tabLessons = new TabItem("All Lessons");
        _tabLessons.setScrollMode(Scroll.AUTO);
        _tabPanel.add(_tabLessons);
        
        
        _tabQuestions = new TabItem("Questions");
        _tabQuestions.setScrollMode(Scroll.AUTO);
        _tabPanel.add(_tabQuestions);
        cpLeft.add(_tabPanel);
        lc.add(cpLeft, data);
        
        
        ContentPanel cpRight = new ContentPanel();
        cpRight.setHeading("Questions in Quiz");
        cpRight.setLayout(new FitLayout());
        lc.add(cpRight, data);
        
        return lc;  
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
        ComboBox<QuizNamesModel> quizNames = new ComboBox<QuizNamesModel>();
        ListStore<QuizNamesModel> store = new ListStore<QuizNamesModel>();
        quizNames.setFieldLabel("Quiz Name");
        quizNames.setStore(store);
        form.add(quizNames);
        lc.add(form);
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
}

