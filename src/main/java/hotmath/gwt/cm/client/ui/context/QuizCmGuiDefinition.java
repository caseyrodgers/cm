package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.UserInfoBase.Mode;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.QuizHtmlResult;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmGuiDefinition;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard;
import hotmath.gwt.cm_tools.client.ui.viewer.CmResourcePanelImplWithWhiteboard.WhiteboardResourceCallback;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * add the quiz resource as the default
 * 
 * @TODO: which quiz?
 */
public class QuizCmGuiDefinition implements CmGuiDefinition  {
    int testSegment;
    
    public QuizCmGuiDefinition(int segmentNumber) {
        this.testSegment = segmentNumber;
    }

    QuizHtmlResult _htmlResult;

    public QuizCmGuiDefinition(QuizHtmlResult htmlResult) {
        this._htmlResult = htmlResult;

    }

    public String getTitle() {
        return "Quiz Page";
    }

    public Widget getWestWidget() {

        FlowLayoutContainer cp = new FlowLayoutContainer();
        cp.setStyleName("quiz-page-resource");
        String html = "";
        if (UserInfo.getInstance().getTestName().indexOf("Auto-Enrollment") > -1) {
            html = "<p>Take the quiz to the right.</p>" + "<p>Afterwards, you may get one or two more"
                    + "   quizzes before starting your review and practice. " + "</p>"
                    + "<p>Work out your answers carefully on our whiteboard or on paper.</p>"
                    + "<p>If you log out, your answers will be saved." + "</p> ";
        } else {

            if (UserInfo.getInstance().isCustomProgram()) {
                html = "<h1>Catchup Math Custom Quiz</h1>"
                        + "<p>Work out the Quiz answers carefully.</p>"
                        + "<p>After completion, your teacher will be able to review the score.</p>";
            } else {
                html = "<h1>HOW TO USE CATCHUP MATH</h1>" + "<p>Take the 10-question quiz to the right.</p>"
                        + "<p>Then, we will provide you with personalized review and practice.</p>"
                        + "<p>Work out your answers carefully on our whiteboard or on paper.</p>";
            }
        }
        cp.add(new HTML(html));
        cp.add(new HTML(CatchupMathTools.FEEDBACK_MESSAGE));

        int currentQuiz = UserInfo.getInstance().getTestSegment();

        /**
         * this seems like a bug .. maybe switching between 1 and 0 base.
         */
        if (currentQuiz == 0)
            currentQuiz = 1;

        return cp;
    }

    QuizPage qp;
    public Widget getCenterWidget() {
        qp = new QuizPage(false, _htmlResult);

        /**
         * Create resource container to display the tutor/white-board
         * combination
         * 
         */

        
        CmResourcePanelImplWithWhiteboard resourcePanel = new CmResourcePanelImplWithWhiteboard() {
        	
        	@Override
        	public boolean isQuiz() {
        		return true;
        	}
        	
        	
            @Override
            protected DisplayMode getInitialWhiteboardDisplay() {
                return DisplayMode.TUTOR;
            }

            public Widget getResourcePanel() {
                return this;
            }

            public Integer getOptimalHeight() {
                return -1;
            }

            public Boolean allowClose() {
                return false;
            }

            @Override
            public List<Widget> getContainerTools() {
                ArrayList<Widget> list2 = new ArrayList<Widget>();
                if (CmCore.isDebug() == true || UserInfoBase.getInstance().getMode() == Mode.TEACHER_MODE) {
                    list2.add(new TextButton("Mark Correct", new SelectHandler() {
                        @Override
                        public void onSelect(SelectEvent event) {
                            qp.markAllAnswersCorrect();
                        }
                    }));
                }
                list2.add(new TextButton("Check Quiz",new SelectHandler() {
                    
                    @Override
                    public void onSelect(SelectEvent event) {
                        ContextController.getInstance().doNext();
                    }
                        
                }));
                List<Widget> list = super.getContainerTools();
                list2.addAll(list);
                return list2;
            }

            @Override
            public Widget getTutorDisplay() {
                return qp;
            }

            @Override
            public void setupShowWorkPanel(ShowWorkPanel2 whiteboardPanel) {
                whiteboardPanel.setupForPid(ShowWorkPanel2.QUIZ_PREFIX + CmMainPanel.getLastQuestionPid());
            }
            
            @Override
            public void loadWhiteboardData(final ShowWorkPanel2 showWorkPanel) {
                new RetryAction<CmList<WhiteboardCommand>>() {
                    
                    @Override
                    public void attempt() {
                        CmBusyManager.setBusy(true);
                        GetWhiteboardDataAction action = new GetWhiteboardDataAction(UserInfo.getInstance().getUid(), "quiz:quiz",  UserInfo.getInstance().getRunId());
                        setAction(action);
                        CmRpcCore.getCmService().execute(action, this);
                    }

                    public void oncapture(CmList<WhiteboardCommand> whiteboardCommands) {
                        try {
                            showWorkPanel.loadWhiteboard(whiteboardCommands);
                        } finally {
                            CmBusyManager.setBusy(false);
                        }
                    }
                }.register();                
            }
        };
        
        WhiteboardResourceCallback whiteboardCallback = new WhiteboardResourceCallback() {
            @Override
            public ResizeContainer getResizeContainer() {
                return CmMainPanel.getActiveInstance();
            }
            
            @Override
            public void ensureOptimizedResource() {
                CmMainPanel.getActiveInstance().ensureOptimizedResource();
            }
            
            @Override
            public void ensureMaximizeResource() {
                CmMainPanel.getActiveInstance().ensureMaximizeResource();
            }
        };
        resourcePanel.setWhiteboardCallback(whiteboardCallback);;

        resourcePanel.addResource(qp, "Quiz");
        //resourcePanel.setScrollMode(ScrollMode.AUTOY);
        
        QuizContext qc = (QuizContext) getContext();
        qc.setTitle(_htmlResult.getTitle());

        CmMainPanel.getActiveInstance().showResource(resourcePanel, _htmlResult.getTitle());

        ContextController.getInstance().setCurrentContext(qc);

        if (UserInfo.getInstance().isAutoTestMode()) {
            qc.doCheckTest();
        }

        return null;
    }

    public CmContext getContext() {
        // TODO Auto-generated method stub
        return new QuizContext(this);
    }
}