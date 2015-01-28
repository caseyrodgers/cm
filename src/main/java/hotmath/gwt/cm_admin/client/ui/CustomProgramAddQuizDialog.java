package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.list.ListCustomLesson;
import hotmath.gwt.cm_admin.client.ui.list.ListCustomLesson.CallbackOnDoubleClick;
import hotmath.gwt.cm_admin.client.ui.list.QuizQuestionFlow;
import hotmath.gwt.cm_core.client.model.QuizQuestionModel;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.rpc.GetAllCustomQuizLessonsAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizId;
import hotmath.gwt.shared.client.model.IntValueHolder;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomQuizUsageCountAction;
import hotmath.gwt.shared.client.rpc.action.GetCustomQuizAction;
import hotmath.gwt.shared.client.rpc.action.GetLessonQuestionsAction;
import hotmath.gwt.shared.client.rpc.action.SaveCustomQuizAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

public class CustomProgramAddQuizDialog extends GWindow {
	
    ListView<CustomLessonModel, String> _listLessons = null;
    QuizQuestionFlow _listQuestions = new QuizQuestionFlow(null);
    QuizQuestionFlow _listCustomQuiz = new QuizQuestionFlow(null);

    SimpleContainer _mainPanel = new SimpleContainer();
    TabPanel _tabPanel;
    TabItemConfig _tabLessons;
    TabItemConfig _tabQuestions;
    CustomLessonModel _selectedLesson;
    ContentPanel _panelQuestions;
    Callback callback;
    TextField _textQuizName;
    //CheckBox _chkAnswersChkBox;
    //CheckBoxGroup _chkAnswersChkBoxGrp;
    TextButton _saveButton;
    CustomQuizDef _customQuiz;
    MyButtonWithTip _addButton;
    Label _readOnlyLabel;
    ContentPanel _questionToolbar;
        
    int adminId;

    public CustomProgramAddQuizDialog(int adminId, Callback callback, CustomQuizDef quiz, boolean asCopy) {
    	
    	super(false);

    	this.adminId = adminId;
        this.callback = callback;
        this._customQuiz = quiz;


        setId("custom_quiz_design");
        
        setHeadingText("Define Custom Quiz");
        setPixelSize(805, 480);
        setMaximizable(true);
        setModal(true);

        addStyleName("custom-program-add-quiz-dialog");

        _saveButton = new TextButton("Save", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
                saveCustomQuiz();
            }
        });
        addButton(_saveButton);

        addButton(new TextButton("Cancel", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        
        _listLessons = new ListCustomLesson(new CallbackOnDoubleClick() {
            @Override
            public void doubleClicked(CustomLessonModel lessonModel) {
                _tabPanel.setActiveWidget(_panelQuestions);
            }
        });

        _mainPanel = createBodyPanel();

        _listCustomQuiz.setCallback(new QuizQuestionFlow.Callback() {
            @Override
            public void questionCountChanged(int count) {
                _totalCount.setText("Count: " + count);
            }
        });
        
//        final DragSource s = new DragSource(_listQuestions);
//        s.addDropHandler(new DndDropHandler() {
//            
//            @Override
//            public void onDrop(DndDropEvent event) {
//                Info.display("Info", "drag drop: " + event);
//            }
//        });
//        
//        s.addDragStartHandler(new DndDragStartHandler() {
//            @Override
//            public void onDragStart(DndDragStartEvent event) {
//                Info.display("Info", "drag start: " + event);
//            }
//        });
//        DropTarget target = new DropTarget(_listCustomQuiz) {
//            @SuppressWarnings("unchecked")
//            protected void showFeedback(com.sencha.gxt.dnd.core.client.DndDragMoveEvent event) {
//                super.showFeedback(event);
//            }
//        };
//        target.setFeedback(Feedback.BOTH);
//        target.setAllowSelfAsSource(true);        
        
        
        
//        ListStore<CustomLessonModel> storeAll = new ListStore<CustomLessonModel>();
//        storeAll.setStoreSorter(new StoreSorter<CustomLessonModel>() {
//            @Override
//            public int compare(Store<CustomLessonModel> store, CustomLessonModel m1, CustomLessonModel m2,
//                    String property) {
//                if (property != null) {
//                    String v1 = m1.getLesson();
//                    String v2 = m2.getLesson();
//                    return comparator.compare(v1, v2);
//                }
//                return super.compare(store, m1, m2, property);
//            }
//        });
//
//        _listLessons.setStore(storeAll);
//        String template = "<tpl for=\".\"><div class='x-view-item'><span style='font-size:.5em;width: 5px;' class='{subjectStyleClass}'>&nbsp;</span>&nbsp;{" + "customProgramItem" + "}</div></tpl>";
//        _listLessons.setTemplate(template);
                

        BorderLayoutContainer blc = new BorderLayoutContainer();
        blc.setCenterWidget(_listLessons);
        blc.setSouthWidget(CustomProgramDesignerDialog.getLegend(), new BorderLayoutData(30));

        _tabPanel.add(blc, _tabLessons);
        
//        _listLessons.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
//            @Override
//            public void handleEvent(BaseEvent be) {
//                // loadQuestionsFor(_listLessons.getSelectionModel().getSelectedItem());
//                _tabPanel.setSelection(_tabQuestions);
//            }
//        });

        
        
//        _listQuestions.setStore(new ListStore<QuizQuestionModel>());
//        _listQuestions.setTemplate("<tpl for=\".\"><div style='padding: 15px;white-space: normal' class='x-view-item'>{question}</div></tpl>");
        
        
        
        _panelQuestions = createQuestionListPanel(_listQuestions);
        _tabQuestions.setEnabled(false);

        _listQuestions.addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
            	addSelectedQuestionToCustomQuiz();            }
        }, DoubleClickEvent.getType());
        
        _tabPanel.add(_panelQuestions, _tabQuestions);


        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
                if (_tabPanel.getActiveWidget() == _panelQuestions) {
                    loadQuestionsFor(_listLessons.getSelectionModel().getSelectedItem());
                }
            }
        });

        _listLessons.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<CustomLessonModel>() {
        	@Override
        	public void onSelectionChanged(
        			SelectionChangedEvent<CustomLessonModel> event) {
        		_tabQuestions.setEnabled(true);
        		_tabPanel.forceLayout();
        	}
		});
		

        BorderLayoutContainer borderLayout = new BorderLayoutContainer();
        
        borderLayout.setNorthWidget(createTopPanel(), new BorderLayoutData(50));
        borderLayout.setCenterWidget(_mainPanel);
        // borderLayout.setSouthWidget(CustomProgramDesignerDialog.getLegend(), new BorderLayoutData(40));
        
        
        setWidget(borderLayout);


//        _listCustomQuiz.getStore().addStoreListener(new StoreListener<QuizQuestionModel>() {
//            @Override
//            public void handleEvent(StoreEvent<QuizQuestionModel> e) {
//                showQuestionsInCustomQuiz(_listCustomQuiz.getStore().getModels(), _listCustomQuiz.getId());
//            }
//        });

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
        
        //_chkAnswersChkBox.setValue(_customQuiz.isAnswersViewable());
        
        
        
        /** initially everything is read only, only enable after call to verify
         *  selected custom quiz is not in use or archived
         */
        setIsModifiable(false);
        setIsArchived(true);
        
        new RetryAction<IntValueHolder>() {
            @Override
            public void attempt() {
                CustomQuizUsageCountAction action = new CustomQuizUsageCountAction(_customQuiz.getQuizId());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }
            
            @Override
            public void oncapture(IntValueHolder count) {
                if(count.getValue() == 0 && _customQuiz.isArchived() == false) {
                    setIsModifiable(true);
                    setIsArchived(false);
                    _customQuiz.setInUse(false);
                }
                else {
                    _customQuiz.setInUse(count.getValue() > 0);
                    setIsArchived(_customQuiz.isArchived());
                    
                    _readOnlyLabel.addStyleName("custom-quiz-no-modify-text");
                    if (_customQuiz.isArchived()) {
                    	_readOnlyLabel.setText("Custom Quiz has been archived and may not be modified.  You can make a copy to customize.");
                    }
                    else {
                    	_readOnlyLabel.setText("Custom Quiz has been used and questions may not be modified.  You can archive it or make a copy to customize.");
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
    		//_chkAnswersChkBox.enable();
    	}
    	else {
    		_saveButton.disable();
    		_textQuizName.disable();
    		//_chkAnswersChkBox.disable();
    	}
    }
    
    /*
     * if isModifiable is true, then questions can be modified, otherwise not
     */
    private void setIsModifiable(boolean isModifiable) {
        CmLogger.info("CustomProgramAddQuizDialog: Is modifiable: " + isModifiable);

        _addButton.setEnabled(isModifiable);
        _readOnlyLabel.setVisible(!isModifiable);
        
        List<Widget> tools = _questionToolbar.getHeader().getTools();
        for (Widget tool : tools) {
        	if(tool instanceof Component) {
        		Component c = (Component)tool;
        		if (isModifiable == true) {
        			c.enable();
        		}
        		else  {
        			c.disable();
        		}
        	}
        }
        //_panelQuestions.getHeader().setVisible(isModifiable);
    }
    
    private void addSelectedQuestionToCustomQuiz() {
        QuizQuestionModel question = _listQuestions.getSelectedQuestion();
        if (question != null) {
            
            for(int i=0,t=_listCustomQuiz.getQuestions().size();i<t;i++) {
                if(_listCustomQuiz.getQuestions().get(i).equals(question)) {
                    CmMessageBox.showAlert("This question is already in the custom quiz.");
                    markSelectedItem(_listCustomQuiz, _listCustomQuiz.getQuestions().get(i));
                    return;
                }
            }
            _listCustomQuiz.addQuestion(question);
            markSelectedItem(_listCustomQuiz, question);
        }
    }

    private void removeSelectedQuestionFromCustomQuiz() {
        QuizQuestionModel question = _listCustomQuiz.getSelectedQuestion();
        if (question != null) {
            _listCustomQuiz.removeQuestion(question);
        }
        else {
            showMustSelectQuestionMessage();
        }
    }

    private void removeAllSelectedQuestionFromCustomQuiz() {
        if(_listCustomQuiz.getQuestions().size() == 0) {
            CmMessageBox.showAlert("No questions in custom quiz to delete.");
        }
        else {
        	CmMessageBox.confirm("Remove Questions", "Remove all questions from custom quiz?",
        			new ConfirmCallback() {
						@Override
						public void confirmed(boolean yesNo) {
							if(yesNo) {
                                _listCustomQuiz.removeAllQuestions();
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
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<QuizQuestion> questions) {
                CmBusyManager.setBusy(false);

                _listCustomQuiz.removeAllQuestions();
                List<QuizQuestionModel> models = new ArrayList<QuizQuestionModel>();
                for (int i = 0, t = questions.size(); i < t; i++) {
                    QuizQuestion q = questions.get(i);
                    models.add(new QuizQuestionModel(q));
                }
                _listCustomQuiz.setQuestions(models);
            }
        }.register();
    }

    private void saveCustomQuiz() {
        try {
            final List<CustomQuizId> ids = getCustomQuizIds();
            if (ids.size() == 0) {
                CmMessageBox.showAlert("There are no questions assigned to this custom quiz.");
                return;
            }
            
            saveCustomQuizAux();
        } catch (Exception e) {
            e.printStackTrace();
            CmMessageBox.showAlert("Could Not Save", e.getMessage());
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
        
        final boolean isAnswersViewable = true; //this._chkAnswersChkBox.getValue();

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);

                CustomQuizDef customQuiz = new CustomQuizDef(_customQuiz.getQuizId(), cpName, adminId, isAnswersViewable,
                		_customQuiz.isInUse(), _customQuiz.isArchived(), _customQuiz.getArchiveDate());
                SaveCustomQuizAction action = new SaveCustomQuizAction(customQuiz, ids);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
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
        List<QuizQuestionModel> models = _listCustomQuiz.getQuestions();

        List<CustomQuizId> quizIds = new ArrayList<CustomQuizId>();
        for (int i = 0, t = models.size(); i < t; i++) {
            QuizQuestionModel question = models.get(i);
            quizIds.add(new CustomQuizId(question.getPid(), i));
        }

        return quizIds;
    }

    private void showQuestionHtml(QuizQuestionModel quizModel) {
        GWindow questionWindow = new GWindow(false);
        questionWindow.setHeadingText("Question: " + quizModel.getQuestionId());
        questionWindow.setModal(true);
        questionWindow.setPixelSize(525, 300);

        HTML html = new HTML(quizModel.getQuestion());
        questionWindow.add(html);

        questionWindow.addCloseButton();

        questionWindow.setVisible(true);
    }

    static CmList<CustomLessonModel> __allLessons;

    private void getAllLessonData() {
        if (__allLessons != null) {
            _listLessons.getStore().clear();
            _listLessons.getStore().addAll(__allLessons);
            return;
        }

        new RetryAction<CmList<CustomLessonModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetAllCustomQuizLessonsAction action = new GetAllCustomQuizLessonsAction();
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomLessonModel> allLessons) {
                CmBusyManager.setBusy(false);
                __allLessons = allLessons;
                _listLessons.getStore().clear();
                _listLessons.getStore().addAll(__allLessons);
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

        _listQuestions.clear();
        _panelQuestions.setHeadingText("Questions For: " + lesson.getLesson());
        new RetryAction<CmList<QuizQuestion>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetLessonQuestionsAction action = new GetLessonQuestionsAction(lesson.getFile(), lesson.getSubject());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
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
        _listQuestions.clear();
        List<QuizQuestionModel> models = new ArrayList<QuizQuestionModel>();
        JsArrayInteger answers = createArray();
        for (int i = 0, t = questions.size(); i < t; i++) {
            QuizQuestion question = questions.get(i);
            models.add(new QuizQuestionModel(question));

            answers.push(question.getCorrectAnswer());
        }
        _listQuestions.setQuestions(models);

        String id = _listQuestions.getId();
        jsni_hideAnswerResults(id, answers);
    }

    private void showQuestionsInCustomQuiz(List<QuizQuestionModel> questions, String id) {
        JsArrayInteger answers = createArray();
        for (int i = 0, t = questions.size(); i < t; i++) {
            QuizQuestionModel question = questions.get(i);
            answers.push(question.getCorrectAnswer());
        }
        jsni_hideAnswerResults(id, answers);
    }

    private native JsArrayInteger createArray() /*-{
                                                return [];
                                                }-*/;

    private native void jsni_hideAnswerResults(String id, JsArrayInteger answers) /*-{
        try {
            var questionList = $doc.getElementById(id);
            $wnd.prepareCustomQuizForDisplay(questionList, answers);
            
            
            $wnd.processMathJax();
        }
        catch(e) {
            alert(e);
        }
   }-*/;

    Label _totalCount = new Label("Count: 0");
    private SimpleContainer createBodyPanel() {
    	
    	BorderLayoutContainer blc = new BorderLayoutContainer();
    	
        blc.addStyleName("custom-program-questions-container");


        ContentPanel cpLeft = new ContentPanel();
        // cpLeft.setHeading("Available Questions");
        cpLeft.getHeader().setVisible(false);

        _tabPanel = new TabPanel();
        
        _tabLessons = new TabItemConfig("All Lessons", false);
        // _tabPanel.add(_tabLessons);

        _tabQuestions = new TabItemConfig("Questions", false);
        // _tabQuestions.setLayout(new FitLayout());
        // _tabPanel.add(_tabQuestions);
        
        cpLeft.add(_tabPanel);
        
        BorderLayoutData ld = new BorderLayoutData(380);
        ld.setSplit(true);
        blc.setWestWidget(cpLeft, ld);

        _questionToolbar = new ContentPanel();
        _questionToolbar.setHeadingText("Questions in Custom Quiz");
        
        BorderLayoutContainer blc2 = new BorderLayoutContainer();
        _questionToolbar.setWidget(blc);
        
        blc2.setCenterWidget(_listCustomQuiz);
        
        _totalCount.getElement().setAttribute("style", "margin-right;5px");
        
        FlowLayoutContainer foot = new FlowLayoutContainer();
        foot.getElement().setAttribute("style",  "text-align;right");
        foot.add(_totalCount);
        blc2.setSouthWidget(foot, new BorderLayoutData(15));
        
        blc.setCenterWidget(blc2);;
        

        TextButton removeBtn = new TextButton("Remove");
        Menu removeMenu = new Menu();
        removeMenu.add(new MyMenuItemWithTip("Remove", "Remove the selected question from the custom quiz.",
                new SelectionHandler<MenuItem>() {
					
					@Override
					public void onSelection(SelectionEvent<MenuItem> event) {
                        removeSelectedQuestionFromCustomQuiz();
                    }
                }));
        
        
        removeMenu.add(new MyMenuItemWithTip("Remove All", "Remove all questions from the custom quiz",
                new SelectionHandler<MenuItem>() {
        			public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<MenuItem> event) {
                        removeAllSelectedQuestionFromCustomQuiz();
                    }
                }));
        
        removeBtn.setMenu(removeMenu);
        _questionToolbar.getHeader().addTool(removeBtn);

        _questionToolbar.getHeader().addTool(
                new MyButtonWithTip("Move Down", "Move the selected question down in the custom quiz.",
                        new SelectHandler() {
							@Override
							public void onSelect(SelectEvent event) {
                                moveSelectedQuestionInProgramDown();
                            }
                        }));

        _questionToolbar.getHeader().addTool(
                new MyButtonWithTip("Move Up", "Move the selected question up in the custom quiz.",
                        new SelectHandler() {
							@Override
							public void onSelect(SelectEvent event) {
                                moveSelectedQuestionInProgramUp();
                            }
                        }));
        
        return _questionToolbar;
    }

    private void moveSelectedQuestionInProgramDown() {
        QuizQuestionModel question = _listCustomQuiz.getSelectedQuestion();
        if (question != null) {
            int num = getQuestionNum(_listCustomQuiz.getQuestions(), question);
            if (num + 1 < _listCustomQuiz.getQuestions().size()) {
                _listCustomQuiz.getQuestions().remove(question);
                 _listCustomQuiz.getQuestions().add(num + 1,question);
                 _listCustomQuiz.refreshList();
                 
                 markSelectedItem(_listCustomQuiz, question);
            }
            else {
                CmMessageBox.showAlert("This is already the last question in the custom quiz.");
            }
        }
        else {
            showMustSelectQuestionMessage();
        }
    }

    private void showMustSelectQuestionMessage() {
        CmMessageBox.showAlert("Select a question first.");
    }
    
    private void moveSelectedQuestionInProgramUp() {
        QuizQuestionModel question = _listCustomQuiz.getSelectedQuestion();
        if (question != null) {
            int num = getQuestionNum(_listCustomQuiz.getQuestions(), question);
            if (num > 0) {
                _listCustomQuiz.getQuestions().remove(question);
                _listCustomQuiz.getQuestions().add(num - 1,question);
                _listCustomQuiz.refreshList();
                
                markSelectedItem(_listCustomQuiz, question);
            }
            else {
                CmMessageBox.showAlert("This is already the first question in the custom quiz.");
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
    private void markSelectedItem(QuizQuestionFlow list, QuizQuestionModel question) {
        list.setSelectedQuestion(question);
        //int num = getQuestionNum(list.getQuestions(),question);
        // list.getElement(num).scrollIntoView();
    }

    private ContentPanel createQuestionListPanel(Widget widget) {
        ContentPanel cp = new ContentPanel();
        cp.setHeadingText("Questions");
        cp.setWidget(widget);
        _addButton =
        	new MyButtonWithTip("Add", "Add the selected question to custom quiz.",
                new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
                        addSelectedQuestionToCustomQuiz();
                    }
                });
        _addButton.disable();
        cp.getHeader().addTool(_addButton);
        return cp;
    }

    private Widget createTopPanel() {
        
        // form.setLabelWidth(70);
        
        _textQuizName = new TextField();
        _textQuizName.setAllowBlank(false);
        // _textQuizName.setFieldLabel("Quiz Name");

        BorderLayoutContainer blc = new BorderLayoutContainer();
        _readOnlyLabel = new HTML();
        _readOnlyLabel.setStyleName("custom-quiz-no-modify-label");

        
        blc.setWestWidget(new FieldLabel(_textQuizName, "Quiz Name"), new BorderLayoutData(.45));
        _readOnlyLabel.setVisible(true);
        blc.setEastWidget(_readOnlyLabel, new BorderLayoutData(.50));
        
        FramedPanel frame = new FramedPanel();
        frame.setBodyBorder(false);
        frame.setHeaderVisible(false);
        frame.setWidget(blc);
        //_readOnlyLabel.setText("TEST TEST");
        return frame;
    }

    static public interface Callback {
        void quizCreated();
    }

    public static void startTest() {
        new GwtTester(new TestWidget() {
            
            @Override
            public void runTest() {
                CustomQuizDef quiz;
                CustomProgramAddQuizDialog.Callback callback = new CustomProgramAddQuizDialog.Callback() {
                    
                    @Override
                    public void quizCreated() {
                        // TODO Auto-generated method stub
                        
                    }
                };
                quiz = new CustomQuizDef();
                quiz.setQuizName("TEST QUIZ");
                new CustomProgramAddQuizDialog(2, callback, quiz, false);                
            }
        });
    }
}


class MyMenuItemWithTip extends MenuItem {
    MyMenuItemWithTip(String name, String tip, SelectionHandler<MenuItem> me) {
        super(name, me);
        setToolTip(tip);
    }
}

class MyButtonWithTip extends TextButton {
    MyButtonWithTip(String name, String tip, SelectHandler be) {
        super(name, be);
        setToolTip(tip);
    }
}