package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizId;
import hotmath.gwt.shared.client.model.CustomQuizInfoModel;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction.ActionType;
import hotmath.gwt.shared.client.rpc.action.CustomQuizInfoAction;
import hotmath.gwt.shared.client.rpc.action.GetCustomQuizAction;
import hotmath.gwt.shared.client.rpc.action.GetLessonQuestionsAction;
import hotmath.gwt.shared.client.rpc.action.SaveCustomQuizAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
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
    CheckBox _chkAnswersChkBox;
    CheckBoxGroup _chkAnswersChkBoxGrp;
    Button _saveButton;
    CustomQuizDef _customQuiz;
    MyButtonWithTip _addButton;
    Label _readOnlyLabel;
    ContentPanel _questionToolbar;
        
    int adminId;

    public CustomProgramAddQuizDialog(Callback callback, CustomQuizDef quiz, boolean asCopy) {
        this.callback = callback;
        this._customQuiz = quiz;

        adminId = StudentGridPanel.instance._cmAdminMdl.getId();

        setId("custom_quiz_design");

        setHeading("Define Custom Quiz");
        setSize(805, 480);
        setMaximizable(true);
        setModal(true);

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
        _listCustomQuiz.getStore().addStoreListener(new StoreListener<QuizQuestionModel>() {
            @Override
            public void handleEvent(StoreEvent<QuizQuestionModel> e) {
                _totalCount.setText("Count: " + _listCustomQuiz.getStore().getCount());
            }
        });
        _listCustomQuiz
                .setTemplate("<tpl for=\".\"><div style='padding: 15px;white-space: normal' class='x-view-item'>{question}</div></tpl>");

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
        String template = "<tpl for=\".\"><div class='x-view-item'><span style='font-size:.5em;width: 5px;' class='{subjectStyleClass}'>&nbsp;</span>&nbsp;{"
                + "customProgramItem" + "}</div></tpl>";
        _listLessons.setTemplate(template);
        _tabLessons.add(_listLessons);
        _tabLessons.setLayout(new FitLayout());

        _listLessons.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                // loadQuestionsFor(_listLessons.getSelectionModel().getSelectedItem());
                _tabPanel.setSelection(_tabQuestions);
            }
        });

        _listQuestions.setStore(new ListStore<QuizQuestionModel>());
        _listQuestions
                .setTemplate("<tpl for=\".\"><div style='padding: 15px;white-space: normal' class='x-view-item'>{question}</div></tpl>");
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
                if (_tabPanel.getSelectedItem().getText().equalsIgnoreCase("questions")) {
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
        add(createTopPanel(), new BorderLayoutData(LayoutRegion.NORTH, 50));
        add(_mainPanel, new BorderLayoutData(LayoutRegion.CENTER));

        CustomProgramDesignerDialog.createButtonBarLedgend(getButtonBar());

        _listCustomQuiz.getStore().addStoreListener(new StoreListener<QuizQuestionModel>() {
            @Override
            public void handleEvent(StoreEvent<QuizQuestionModel> e) {
                showQuestionsInCustomQuiz(_listCustomQuiz.getStore().getModels(), _listCustomQuiz.getId());
            }
        });

        getAllLessonData();

        if (_customQuiz != null) {
            loadCustomQuiz(_customQuiz);
            if(asCopy) {
                _customQuiz = new CustomQuizDef(0, "Copy of " + _customQuiz.getQuizName(), adminId, _customQuiz.isAnswersViewable(),
                		false, false, null);
            }
        } else {
            _customQuiz = new CustomQuizDef(0, "New Custom Quiz", adminId, true, false, false, null);
        }
        _textQuizName.setValue(_customQuiz.getQuizName());
        
        _chkAnswersChkBox.setValue(_customQuiz.isAnswersViewable());
        
        
        
        /** initially everything is read only, only enable after call to verify
         *  selected custom quiz is not in use or archived
         */
        setIsModifiable(false);
        setIsArchived(true);
        
        
        CustomQuizInfoAction a;
        new RetryAction<CustomQuizInfoModel>() {
            @Override
            public void attempt() {
                CustomQuizInfoAction action = new CustomQuizInfoAction(adminId, new CustomLessonModel(_customQuiz.getQuizId(), null, true, false, false, null));
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(CustomQuizInfoModel value) {
                if(value.getAssignedStudents().size() == 0 && _customQuiz.isArchived() == false) {
                    setIsModifiable(true);
                    setIsArchived(false);
                    _customQuiz.setInUse(false);
                }
                else {
                    _customQuiz.setInUse(value.getAssignedStudents().size() != 0);
                    setIsArchived(_customQuiz.isArchived());
                    if (_customQuiz.isArchived()) {
                    	_readOnlyLabel.setText("<span class='custom-quiz-no-modify-text'>Custom Quiz has been archived and may not be modified.  You can make a copy to customize.</span>");
                    }
                    else {
                    	_readOnlyLabel.setText("<span class='custom-quiz-no-modify-text'>Custom Quiz has been used and questions may not be modified.  You can archive it or make a copy to customize.</span>");
                    }
                }
            };
        }.register();

        setVisible(true);
    }

    /*
     * if isArchived is false, then name and check-answers can be changed and CQ can be saved, otherwise not.
     */
    private void setIsArchived(boolean isArchived) {
    	if (isArchived == false) {
    		_saveButton.enable();
    		_textQuizName.enable();
    		_chkAnswersChkBox.enable();
    	}
    	else {
    		_saveButton.disable();
    		_textQuizName.disable();
    		_chkAnswersChkBox.disable();
    	}
    }
    
    /*
     * if isModifiable is true, then questions can be modified, otherwise not
     */
    private void setIsModifiable(boolean isModifiable) {
        CmLogger.info("CustomProgramAddQuizDialog: Is modifiable: " + isModifiable);

        _addButton.setEnabled(isModifiable);
        _readOnlyLabel.setVisible(!isModifiable);
        
        List<Component> tools = _questionToolbar.getHeader().getTools();
        for (Component tool : tools) {
        	if (isModifiable == true) tool.enable();
        	else tool.disable();
        }
        _panelQuestions.getHeader().setVisible(isModifiable);
    }
    
    private void addSelectedQuestionToCustomQuiz() {
        QuizQuestionModel question = _listQuestions.getSelectionModel().getSelectedItem();
        if (question != null) {
            
            for(int i=0,t=_listCustomQuiz.getStore().getCount();i<t;i++) {
                if(_listCustomQuiz.getStore().getAt(i).equals(question)) {
                    CatchupMathTools.showAlert("This question is already in the custom quiz.");
                    markSelectedItem(_listCustomQuiz, _listCustomQuiz.getStore().getAt(i));
                    return;
                }
            }
            
            _listCustomQuiz.getStore().add(new QuizQuestionModel(question));
            markSelectedItem(_listCustomQuiz, question);
        }
    }

    private void removeSelectedQuestionFromCustomQuiz() {
        QuizQuestionModel question = _listCustomQuiz.getSelectionModel().getSelectedItem();
        if (question != null) {
            _listCustomQuiz.getStore().remove(question);
        }
        else {
            showMustSelectQuestionMessage();
        }
    }

    private void removeAllSelectedQuestionFromCustomQuiz() {
        if(_listCustomQuiz.getStore().getCount() == 0) {
            CatchupMathTools.showAlert("No questions in custom quiz to delete.");
        }
        else {
            MessageBox.confirm("Remove Questions", "Remove all questions from custom quiz?",
                    new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if (!be.isCancelled() && be.getButtonClicked().getText().equalsIgnoreCase("Yes")) {
                                _listCustomQuiz.getStore().removeAll();
                            }
                        }
                    });
        }
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
                for (int i = 0, t = questions.size(); i < t; i++) {
                    QuizQuestion q = questions.get(i);
                    models.add(new QuizQuestionModel(q));
                }
                _listCustomQuiz.getStore().add(models);
            }
        }.register();
    }

    private void saveCustomQuiz() {
        try {
            final List<CustomQuizId> ids = getCustomQuizIds();
            if (ids.size() == 0) {
                CatchupMathTools.showAlert("There are no questions assigned to this custom quiz.");
                return;
            }
            
            saveCustomQuizAux();
        } catch (Exception e) {
            e.printStackTrace();
            CatchupMathTools.showAlert("Could Not Save", e.getMessage());
        }
    }

    private void saveCustomQuizAux() throws Exception {

        final String cpName = _textQuizName.getValue();
        if (cpName.length() == 0) {
            throw new Exception("Name must be specified");
        }
        final List<CustomQuizId> ids = getCustomQuizIds();
        if (ids.size() == 0) {
            throw new Exception("Quizzes must have one or more questions.");
        }
        
        final boolean isAnswersViewable = this._chkAnswersChkBox.getValue();

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);

                CustomQuizDef customQuiz = new CustomQuizDef(_customQuiz.getQuizId(), cpName, adminId, isAnswersViewable,
                		_customQuiz.isInUse(), _customQuiz.isArchived(), _customQuiz.getArchiveDate());
                SaveCustomQuizAction action = new SaveCustomQuizAction(customQuiz, ids);
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
        for (int i = 0, t = models.size(); i < t; i++) {
            QuizQuestionModel question = models.get(i);
            quizIds.add(new CustomQuizId(question.getPid(), i));
        }

        return quizIds;
    }

    private void showQuestionHtml(QuizQuestionModel quizModel) {
        CmWindow questionWindow = new CmWindow();
        questionWindow.setHeading("Question: " + quizModel.getQuestionId());
        questionWindow.setModal(true);
        questionWindow.setSize(525, 300);

        Html html = new Html(quizModel.getQuestion());
        questionWindow.add(html);

        questionWindow.addCloseButton();

        questionWindow.setVisible(true);
    }

    static CmList<CustomLessonModel> __allLessons;

    private void getAllLessonData() {
        if (__allLessons != null) {
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
        if (lesson == null) {
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

    /**
     * replace loaded questions in list
     * 
     * @param questions
     */
    private void showQuestionsAll(CmList<QuizQuestion> questions) {
        _listQuestions.getStore().removeAll();
        List<QuizQuestionModel> models = new ArrayList<QuizQuestionModel>();
        JsArrayInteger answers = createArray();
        for (int i = 0, t = questions.size(); i < t; i++) {
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
        for (int i = 0, t = questions.size(); i < t; i++) {
            QuizQuestionModel question = questions.get(i);
            answers.push(question.getCorrectAnswer());
        }
        hideAnswerResults(id, answers);
    }

    private native JsArrayInteger createArray() /*-{
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

    Label _totalCount = new Label("Count: 0");
    private LayoutContainer createBodyPanel() {
        LayoutContainer lc = new LayoutContainer();
        lc.addStyleName("custom-program-questions-container");

        lc.setLayout(new BorderLayout());

        ContentPanel cpLeft = new ContentPanel();
        // cpLeft.setHeading("Available Questions");
        cpLeft.getHeader().setVisible(false);
        cpLeft.setLayout(new FitLayout());

        _tabPanel = new TabPanel();

        _tabLessons = new TabItem("All Lessons");
        _tabLessons.setLayout(new FitLayout());
        _tabPanel.add(_tabLessons);

        _tabQuestions = new TabItem("Questions");
        _tabQuestions.setLayout(new FitLayout());
        _tabPanel.add(_tabQuestions);
        cpLeft.add(_tabPanel);
        BorderLayoutData ld = new BorderLayoutData(LayoutRegion.WEST, 380);
        ld.setSplit(true);
        lc.add(cpLeft, ld);

        _questionToolbar = new ContentPanel();
        _questionToolbar.setHeading("Questions in Custom Quiz");
        _questionToolbar.setLayout(new BorderLayout());
        _questionToolbar.add(_listCustomQuiz, new BorderLayoutData(LayoutRegion.CENTER));
        
        _totalCount.setStyleAttribute("margin-right", "5px");
        LayoutContainer foot = new LayoutContainer();
        foot.setStyleAttribute("text-align", "right");
        foot.add(_totalCount);
        _questionToolbar.add(foot, new BorderLayoutData(LayoutRegion.SOUTH, 15));
        
        lc.add(_questionToolbar, new BorderLayoutData(LayoutRegion.CENTER));
        

        Button removeBtn = new Button("Remove");
        Menu removeMenu = new Menu();
        removeMenu.add(new MyMenuItemWithTip("Remove", "Remove the selected question from the custom quiz.",
                new SelectionListener<MenuEvent>() {
                    public void componentSelected(MenuEvent ce) {
                        removeSelectedQuestionFromCustomQuiz();
                    }
                }));
        removeMenu.add(new MyMenuItemWithTip("Remove All", "Remove all questions from the custom quiz",
                new SelectionListener<MenuEvent>() {
                    public void componentSelected(MenuEvent ce) {
                        removeAllSelectedQuestionFromCustomQuiz();
                    }
                }));
        removeBtn.setMenu(removeMenu);
        _questionToolbar.getHeader().addTool(removeBtn);

        _questionToolbar.getHeader().addTool(
                new MyButtonWithTip("Move Down", "Move the selected question down in the custom quiz.",
                        new SelectionListener<ButtonEvent>() {
                            public void componentSelected(ButtonEvent ce) {
                                moveSelectedQuestionInProgramDown();
                            }
                        }));

        _questionToolbar.getHeader().addTool(
                new MyButtonWithTip("Move Up", "Move the selected question up in the custom quiz.",
                        new SelectionListener<ButtonEvent>() {
                            public void componentSelected(ButtonEvent ce) {
                                moveSelectedQuestionInProgramUp();
                            }
                        }));
        
        return lc;
    }

    private void moveSelectedQuestionInProgramDown() {
        ListStore<QuizQuestionModel> str = _listCustomQuiz.getStore();
        QuizQuestionModel question = _listCustomQuiz.getSelectionModel().getSelectedItem();
        if (question != null) {
            int num = getQuestionNum(str.getModels(), question);
            if (num + 1 < str.getCount()) {
                _listCustomQuiz.getStore().remove(question);
                 _listCustomQuiz.getStore().insert(question, num + 1);

                 markSelectedItem(_listCustomQuiz, question);
            }
            else {
                CatchupMathTools.showAlert("This is already the last question in the custom quiz.");
            }
        }
        else {
            showMustSelectQuestionMessage();
        }
    }

    private void showMustSelectQuestionMessage() {
        CatchupMathTools.showAlert("Select a question first.");
    }
    
    private void moveSelectedQuestionInProgramUp() {
        ListStore<QuizQuestionModel> str = _listCustomQuiz.getStore();
        QuizQuestionModel question = _listCustomQuiz.getSelectionModel().getSelectedItem();
        if (question != null) {
            int num = getQuestionNum(str.getModels(), question);
            if (num > 0) {
                _listCustomQuiz.getStore().remove(question);
                _listCustomQuiz.getStore().insert(question, num - 1);
                markSelectedItem(_listCustomQuiz, question);
            }
            else {
                CatchupMathTools.showAlert("This is already the first question in the custom quiz.");
            }
        }
        else {
            showMustSelectQuestionMessage();
        }
    }

    private int getQuestionNum(List<QuizQuestionModel> models, QuizQuestionModel question) {
        for (int i = 0, t = models.size(); i < t; i++) {
            QuizQuestionModel qqm = models.get(i);
            if (qqm.equals(question)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Select the named question in given listview
     * 
     * @param list
     * @param question
     */
    private void markSelectedItem(ListView<QuizQuestionModel> list, QuizQuestionModel question) {
        List<QuizQuestionModel> selection = new ArrayList<QuizQuestionModel>();
        selection.add(question);
        list.getSelectionModel().setSelection(selection);
        
        int num = getQuestionNum(list.getStore().getModels(),question);
        list.getElement(num).scrollIntoView();
    }

    private ContentPanel createQuestionListPanel(Widget widget) {
        ContentPanel cp = new ContentPanel();
        cp.setLayout(new BorderLayout());
        cp.setHeading("Questions");
        cp.setLayout(new FitLayout());
        cp.add(widget);

        _addButton =
        	new MyButtonWithTip("Add", "Add the selected question to custom quiz.",
                new SelectionListener<ButtonEvent>() {
                    public void componentSelected(ButtonEvent ce) {
                        addSelectedQuestionToCustomQuiz();
                    }
                });
        _addButton.disable();
        cp.getHeader().addTool(_addButton);
        return cp;
    }

    private Widget createTopPanel() {

        LayoutContainer lc = new LayoutContainer();
        HBoxLayout layout = new HBoxLayout();
        layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
        lc.setLayout(layout);

        FormPanel form = new FormPanel();
        form.setFrame(false);
        form.setBodyBorder(false);
        form.setHeaderVisible(false);
        form.setLabelWidth(70);
        
        _textQuizName = new TextField<String>();
        _textQuizName.setAllowBlank(false);
        _textQuizName.setFieldLabel("Quiz Name");
        form.add(_textQuizName);
        
        FormPanel fp = new FormPanel();
        fp.setFrame(false);
        fp.setBodyBorder(false);
        fp.setHeaderVisible(false);
        fp.setLabelWidth(1);

        _chkAnswersChkBox = new CheckBox();
        _chkAnswersChkBox.setId("check-answers");
        _chkAnswersChkBox.setToolTip("Allow students to check their answers using Help/Student Details.");
        _chkAnswersChkBox.setStyleName("custom-quiz-check-box");
        _chkAnswersChkBox.setBoxLabel("Allow students to check answers");
        _chkAnswersChkBox.setLabelSeparator("");
        _chkAnswersChkBoxGrp = new CheckBoxGroup();
        _chkAnswersChkBoxGrp.setId("check-answers");
        _chkAnswersChkBoxGrp.add(_chkAnswersChkBox);
        _chkAnswersChkBoxGrp.setLabelSeparator("");
        _chkAnswersChkBoxGrp.setStyleName("custom-quiz-check-box-grp");
        fp.add(_chkAnswersChkBoxGrp);

        _readOnlyLabel = new Label("");
        _readOnlyLabel.setStyleName("custom-quiz-no-modify-label");

        lc.add(form);
        lc.add(fp);
        lc.add(_readOnlyLabel);

        return lc;
    }

    static public interface Callback {
        void quizCreated();
    }
}

class QuizQuestionModel extends BaseModelData {
    public QuizQuestionModel(QuizQuestion question) {
        setValues(question);
    }

    private void setValues(QuizQuestion question) {
        set("lesson", question.getLesson());
        set("programName", question.getProgramName());
        set("question", question.getQuizHtml());
        set("questionId", question.getQuestionId());
        set("pid", question.getPid());
        set("correctAnswer", question.getCorrectAnswer());
    }

    
    /** Create a deep copy of an existing model
     * 
     * @param model
     */
    public QuizQuestionModel(QuizQuestionModel model) {
        setValues(new QuizQuestion((String) model.get("questionId"), (String) model.get("lesson"),
                (String) model.get("programName"), (String) model.get("pid"), (String) createUniqRadioButtonName(model
                        .get("question").toString()), (Integer) model.get("correctAnswer")));
    }

    
    /** Update the name='XXX' attribute in the radio buttons that make 
     *  up this question HTML.  That way we can set the value dynamically
     *  via JS without altering any copy.
     *  
     * @param html
     * @return
     */
    private String createUniqRadioButtonName(String html) {
        // html = "before name=\"question_256\" after";
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
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof QuizQuestionModel) {
            return ((QuizQuestionModel)obj).getPid().equals(getPid());
        }
        else {
            return super.equals(obj);
        }
    }
}

class MyMenuItemWithTip extends MenuItem {
    MyMenuItemWithTip(String name, String tip, SelectionListener<MenuEvent> me) {
        super(name, me);
        setToolTip(tip);
    }
}

class MyButtonWithTip extends Button {
    MyButtonWithTip(String name, String tip, SelectionListener<ButtonEvent> be) {
        super(name, be);
        setToolTip(tip);
    }
}