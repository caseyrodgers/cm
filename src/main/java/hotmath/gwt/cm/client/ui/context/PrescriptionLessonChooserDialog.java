package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm.client.ui.CmProgramFlowClientManager;
import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserInfo.UserProgramCompletionAction;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.ui.EndOfProgramWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.info.Info;

public class PrescriptionLessonChooserDialog extends GWindow {

    private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

    interface AboutDialogUiBinder extends UiBinder<Widget, PrescriptionLessonChooserDialog> {
    }

    PrescriptionData pData;
    Grid<LessonChoice> _grid;
    TextButton _nextSegment;

    
    
    private PrescriptionLessonChooserDialog() {
        super(false);

        setPixelSize(450, 380);
        setHeadingText("Choose Lesson");
        setResizable(false);
        setModal(true);

        CenterLayoutContainer loading = new CenterLayoutContainer();
        loading.setWidget(new HTML("Loading ..."));
        setWidget(loading);
        addStyleName(PrescriptionLessonChooserDialog.class.getName());

        _nextSegment = new TextButton("Next Quiz", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                CmLogger.debug("NextQuiz: pData.areAllLessonsCompleted() = " + pData.areAllLessonsCompleted() + ", "
                        + pData.getCountCompletedTopics() + ", " + pData.getSessionTopics().size());
                if (CmCore.isDebug() == false && !pData.areAllLessonsCompleted()) {
                    Info.display("Not Finished", "Please complete all lessons first");
                    return;
                }
                
                final ConfirmMessageBox mb = new ConfirmMessageBox("Continue?", "Ready to move to next quiz?");
                mb.addDialogHideHandler(new DialogHideHandler() {
                    @Override
                    public void onDialogHide(DialogHideEvent event) {
                        if (event.getHideButton() == PredefinedButton.YES) {
                            doMoveNextAux();
                        }
                    }
                });
                mb.setVisible(true);
            }
        });
        addButton(_nextSegment);

        addButton(new TextButton("Select Lesson", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                loadSelectedLesson();
            }
        }));

        addCloseButton();

        addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
            }
        });

    }

    public interface LessonProperties extends PropertyAccess<String> {
        ModelKeyProvider<LessonChoice> id();
        ValueProvider<LessonChoice, String> topic();
        ValueProvider<LessonChoice, String> status();
        ValueProvider<LessonChoice, Boolean> complete();
    }

    private Grid<LessonChoice> createGrid(LessonProperties props) {
        
        List<ColumnConfig<LessonChoice, ?>> cols = defineColumns(props);
        ColumnModel<LessonChoice> cm = new ColumnModel<LessonChoice>(cols);
        ListStore<LessonChoice> store = new ListStore<LessonChoice>(props.id());
        Grid<LessonChoice> grid = new Grid<LessonChoice>(store, cm);
        
        grid.getColumnModel().getColumn(0).setSortable(false);

        grid.getView().setAutoExpandColumn(cols.get(0));
        grid.getView().setStripeRows(true);
        grid.getView().setColumnLines(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        grid.getView().setAutoExpandColumn(cols.get(0));

        grid.setWidth("440px");
        grid.setHeight("300px");

        grid.getView().setViewConfig(new GVC());

        grid.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                loadSelectedLesson();
            }
        }, DoubleClickEvent.getType());
        return grid;
    }

    int scrollIntoView = 0;
    int _id;
    public void showDialog(PrescriptionData pData) {

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));

        this.pData = null;

        if (this.pData != pData) {
            if (_grid != null) {
                remove(_grid);
            }

            this.pData = pData;

            LessonProperties props = GWT.create(LessonProperties.class);
            _grid = createGrid(props);
            final ListStore<LessonChoice> store = _grid.getStore();
            store.clear();

            int recCnt = 0;
            int nextLesson = -1;
            int currentIndex = -1;
            for (SessionTopic st : pData.getSessionTopics()) {
                String status = st.getTopicStatus(); //

                String currentLessonTopic = pData.getCurrSession().getTopic();
                if (st.getTopic().equals(currentLessonTopic)) {
                    status = getLessonStatus(st);
                    if (!st.isComplete()) {
                        currentIndex = recCnt;
                    }
                }
                LessonChoice lc = new LessonChoice(_id++, st.getTopic(), st.isComplete(), status);
                store.add(lc);

                if (nextLesson < 0 && !st.isComplete()) {
                    nextLesson = recCnt;
                }

                recCnt++;
            }
            scrollIntoView = nextLesson;
            setWidget(_grid);

            /**
             * 
             */
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    List<LessonChoice> selectedList = new ArrayList<LessonChoice>();
                    selectedList.add(store.get(scrollIntoView));
                    _grid.getSelectionModel().setSelection(selectedList);
                    _grid.getView().ensureVisible(scrollIntoView, 0, true);
                }
            });

        }

        if (UserInfo.getInstance().isCustomProgram()) {
            _nextSegment.setVisible(false);
        } else {
            if (CmCore.isDebug() == true || pData.areAllLessonsCompleted()) {
                _nextSegment.setEnabled(true);
                _nextSegment.setVisible(true);
            } else {
                _nextSegment.setEnabled(false);
                _nextSegment.setVisible(true);
            }
        }

        setVisible(true);
    }

    /**
     * handles real time analysis is current lesson topic
     * 
     * NOTE: side-effect of setting isComplete
     * 
     * */
    private String getLessonStatus(SessionTopic lesson) {

        for (PrescriptionSessionDataResource pr : pData.getCurrSession().getInmhResources()) {
            if (pr.getType().equals(CmResourceType.PRACTICE)) {
                int totalRp = 0;
                int completeRp = 0;
                for (InmhItemData item : pr.getItems()) {
                    totalRp++;
                    if (item.isViewed()) {
                        completeRp++;
                    }
                }

                if (totalRp == completeRp) {
                    lesson.setComplete(true);
                }

                return completeRp + " of " + totalRp;
            }
        }
        return "";
    }

    private void doMoveNextAux() {

        int correctPercent = UserInfo.getInstance().getCorrectPercent();

        /**
         * hard exit after completion of prescription for any demo
         * 
         */
        if (UserInfo.getInstance().isDemoUser()) {
            new SampleDemoMessageWindow();
            return;
        }

        // there are no more sessions, so need to move to the 'next'.
        // Next might be the same Quiz, the next Quiz or AutoAdvance.

        int passPercentRequired = UserInfo.getInstance().getPassPercentRequired();
        if (!UserInfo.getInstance().isActiveUser()) {
            CatchupMathTools.showAlert("You are a visitor and cannot jump to the next quiz.");
            return;
        }

        CmLogger.debug("Correct percent: " + correctPercent + ", " + passPercentRequired);

        if (UserInfo.getInstance().isCustomProgram() || correctPercent >= passPercentRequired) {
            
            
            /** DEBUGGING ... save state on each each move to next segment
             * 
             */
//            String comments = "Correct percent: " + correctPercent + ", " + passPercentRequired;
//            comments += ", isCustom: " + UserInfo.getInstance().isCustomProgram();
//            
//            SaveFeedbackAction action = new SaveFeedbackAction(comments, "move_next_quiz_segment", "uid: " + UserInfo.getInstance().getUid() );
//            CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
//                @Override
//                public void onSuccess(RpcData result) {
//                    Log.info("Debug message saved");
//                }
//
//                @Override
//                public void onFailure(Throwable caught) {
//                    Log.error("Error saving debug message", caught);
//                }
//            });
            ////
            // END DEBUGGING /////
            ////////////////////// 
           
            
            
            /**
             * user passed the quiz
             * 
             */
            handlePassedQuiz();
        } else {
            /**
             * user did not pass quiz
             * 
             */
            handleNotPassedQuiz();
        }
        return;
    }

    private void handlePassedQuiz() {
        hide();

        /**
         * User has passed this section, and is ready to move to next
         * quiz/autoAdvance
         */
        if (UserInfo.getInstance().isDemoUser()) {
            new SampleDemoMessageWindow();
            return;
        }

        /**
         * are there more Quizzes in this program?
         */
        boolean areMoreSegments = (!UserInfo.getInstance().isCustomProgram() && (UserInfo.getInstance()
                .getTestSegment() < UserInfo.getInstance().getProgramSegmentCount()));
        if (areMoreSegments) {
            new PassedSectionWindow();
        } else {
            if (UserInfo.getInstance().getOnCompletion() == UserProgramCompletionAction.STOP) {
                new EndOfProgramWindow();
            } else {
                QuizCheckResultsWindow.autoAdvanceUser();
            }
        }
    }

    private void handleNotPassedQuiz() {
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));
        
        final ConfirmMessageBox mb = new ConfirmMessageBox("Ready for next Quiz?","Are you ready to be quizzed again on this section?");
        mb.addDialogHideHandler(new DialogHideHandler() {
            
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton() == PredefinedButton.YES) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLEAR, null));
                    hide();
                    CmProgramFlowClientManager.retakeProgramSegment();
                }
            }
        });
        mb.setVisible(true);
    }

    public void showCentered() {
        center();
    }

    private void loadSelectedLesson() {
        if (_grid.getSelectionModel().getSelectedItems().size() > 0) {
            LessonChoice lesson = _grid.getSelectionModel().getSelectedItem();
            boolean found = false;
            int sessionNumber = 0;
            for (LessonChoice item : _grid.getStore().getAll()) {
                if (item == lesson) {
                    found = true;
                    break;
                }
                sessionNumber++;
            }

            if (!found) {
                CatchupMathTools.showAlert("Lesson not found: " + lesson);
            } else {
                CmHistoryManager.getInstance().addHistoryLocation(
                        new CmLocation(LocationType.PRESCRIPTION, sessionNumber));

                hide();
            }
        }
    }

    private List<ColumnConfig<LessonChoice, ?>> defineColumns(LessonProperties props) {
        List<ColumnConfig<LessonChoice, ?>> cols = new ArrayList<ColumnConfig<LessonChoice,?>>();
        cols.add(new ColumnConfig<LessonChoice, String>(props.topic(), 160,  "Lesson"));
        cols.add(new ColumnConfig<LessonChoice, String>(props.status(), 50,  "Status"));
        
        ColumnConfig<LessonChoice, Boolean> compCol = new ColumnConfig<LessonChoice, Boolean>(props.complete(), 80,  "Completed");
        compCol.setColumnTextClassName("is_complete");
        compCol.setCell(new AbstractCell<Boolean>() {
            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, Boolean value, SafeHtmlBuilder sb) {
                if (value) {
                    sb.appendHtmlConstant("<img src='/gwt-resources/images/check_black.png'/>");
                } else {
                    sb.appendHtmlConstant("");
                }
            }
        });
        cols.add(compCol);
        
        return cols;
    }

    static PrescriptionLessonChooserDialog __instance;

    static public PrescriptionLessonChooserDialog getSharedInstance() {
        if (__instance == null) {
            __instance = new PrescriptionLessonChooserDialog();
        }
        return __instance;
    }

}

class GVC implements GridViewConfig<LessonChoice> {
    @Override
    public String getColStyle(LessonChoice model, ValueProvider<? super LessonChoice, ?> valueProvider, int rowIndex,
            int colIndex) {
        return null;
    }
    @Override
    public String getRowStyle(LessonChoice model, int rowIndex) {
        if (model != null) {
            return model.getStyle();
        }
        return "";
    }
}
