package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkSubToolBar;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkSubToolBar.Callback;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuizViewImpl extends AbstractPagePanel implements QuizView {
    @UiField
    DivElement quizHtml;
    
    @UiField
    SpanElement questionCount;
    
    @UiField
    HTMLPanel quizView;
    
    @UiField
    Button checkQuizTop, checkQuizBottom;

    Presenter presenter;
    
    ShowWorkPanel2 _showWork;
    FlowPanel _contentPanel;
    ShowWorkSubToolBar _subBar;
    private FlowPanel _whiteboardWrapper;
    private String _lastPid;
    
    
    public QuizViewImpl() {
        _contentPanel = new FlowPanel();
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
            public void showLesson() {
            }
            
            @Override
            public void hideWhiteboard() {
                QuizViewImpl.this.hideWhiteboard();
            }
        });
        _contentPanel.add(uiBinder.createAndBindUi(this));
        initWidget(_contentPanel);
    }

    interface MyUiBinder extends UiBinder<Widget, QuizViewImpl> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        presenter.prepareQuizView(this);
    }
    
    
    private void showWhiteboardPanel(final String pid) {
        
        CmGwtUtils.jsni_positionQuestionToTopOfViewable(pid);
        
        _lastPid = pid;
        
        if(_showWork != null) {
            hideWhiteboard();
        }
        _subBar.setupWhiteboardTools(true);
        
         _whiteboardWrapper = new FlowPanel();
         _whiteboardWrapper.add(_subBar);
        _showWork = new ShowWorkPanel2(new ShowWorkPanel2Callback() {
            @Override
            public void windowResized() {
            }

            @Override
            public void showWorkIsReady() {
                presenter.loadWhiteboard(_showWork, _lastPid);                
            }

            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return presenter.getWhiteboardSaveAction(_lastPid, commandType, data);
            }
        });
        _whiteboardWrapper.addStyleName("static_whiteboard");
        _whiteboardWrapper.add(_showWork);
        _contentPanel.add(_whiteboardWrapper);
        
        _showWork.setBackground(_subBar.getShowProblem());
        
        CmGwtUtils.addDoubleTapRemoveEvent(_showWork.getElement());
        
        // position to top of document so toolbar is visible on open.
        //Window.scrollTo(0, 0);

        CmGwtUtils.resizeElement(quizView.getElement());
        CmGwtUtils.moveToTopOfViewableScreen(_whiteboardWrapper.getElement(), _whiteboardWrapper.getElement());
    }     
    
    private void hideWhiteboard() {
        _subBar.setupWhiteboardTools(false);
        if(_showWork != null) {
            _contentPanel.remove(_whiteboardWrapper);
            _showWork = null;
            _whiteboardWrapper=null;
        }
    }

    @Override
    public String getViewTitle() {
        int segNum = SharedData.getUserInfo().getTestSegment();
        int segCount = SharedData.getUserInfo().getProgramSegmentCount();
        String programName = SharedData.getUserInfo().getTestName();
        String user = SharedData.getUserInfo().getUserName();
        String section = "";
        if(!SharedData.getUserInfo().isCustomProgram()) {
            section = " for Section " + segNum + " of " + segCount;
        }
        String title = programName + " Quiz " + section;
        return title;
    }
    @Override
    public String getBackButtonText() {
        return null;
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
    public void setQuizHtml(String html, int count) {
        quizHtml.setInnerHTML(html);
        questionCount.setInnerHTML("" + count);
        quizView.setVisible(true);
    }

    @Override
    public void clearQuizHtml() {
        quizView.setVisible(false);
        quizHtml.setInnerHTML("");
    }
    
    @UiHandler("checkQuizTop")
    protected void doCheckQuizTop(ClickEvent ce) {
        presenter.checkQuiz();
    }

    
    @UiHandler("checkQuizBottom")
    protected void doCheckQuizBottom(ClickEvent ce) {
        presenter.checkQuiz();
    }


    @Override
    public void showWhiteboard(String pid) {
        showWhiteboardPanel(pid);
    }
    
}