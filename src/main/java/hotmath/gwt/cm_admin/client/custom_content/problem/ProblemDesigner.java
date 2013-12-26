package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.CmGwtTestUi;
import hotmath.gwt.cm_core.client.model.WhiteboardModel;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveStaticWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;
import hotmath.gwt.cm_tutor.client.view.TutorCallback.WidgetStatusIndication;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class ProblemDesigner extends Composite {
    
    BorderLayoutContainer _main;
    SolutionInfo _solutionInfo;
    public ProblemDesigner() {
        
        _main = new BorderLayoutContainer();
        
        _main.setCenterWidget(new DefaultGxtLoadingPanel());
        initWidget(_main);
    }

    public void loadProblem(final String pid) {
        new RetryAction<SolutionInfo>() {
            @Override
            public void attempt() {
                GetSolutionAction action = new GetSolutionAction(0,0, pid);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(SolutionInfo solution) {
                loadProblem(solution);
            }
        }.register();
    }
    
    
    protected void loadProblem(final SolutionInfo solution) {
        
        if(true) {
            TutorWrapperPanel tutor = new TutorWrapperPanel(true, false, false, false, new TutorCallbackDefault());
            tutor.externallyLoadedTutor(solution, getWidget(),  "", "Solution Title", false, true, null);
            _main.setCenterWidget(tutor);
            _main.forceLayout();
            
            return;
        }
        if(solution.getWhiteboards().size() > 0) {

            FlowLayoutContainer flow = new FlowLayoutContainer();
            flow.add(new HTML("<div id='wb_ps'></div>"));
            
            BorderLayoutData bData = new BorderLayoutData(100);
            _main.setSouthWidget(new Label("A step"), bData);
            _main.setCenterWidget(flow);
            _main.forceLayout();

            
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                
                @Override
                public void execute() {
    
                
                final ShowWorkPanel2Callback callBack = new ShowWorkPanel2Callback() {
                    @Override
                    public void windowResized() {
                    }
                    
                    @Override
                    public void showWorkIsReady(ShowWorkPanel2 showWork) {
                        WhiteboardModel whiteBoard = solution.getWhiteboards().size() > 0?solution.getWhiteboards().get(0):new WhiteboardModel();
                        showWork.loadWhiteboard(whiteBoard.getCommands());
                    }
                    
                    @Override
                    public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                        return new SaveStaticWhiteboardDataAction(UserInfoBase.getInstance().getUid(),solution.getPid(), commandType, data);
                    }
                };
                new ShowWorkPanel2(callBack,true,true,"wb_ps",300, getWidget());
                // TODO Auto-generated method stub
            }
        });

        }
    }

    static public class GwtTestUi implements CmGwtTestUi {
        @Override
        public void startTest() {
            String testPid="test_casey_1_1_1_1";
            testPid="cmextras_dynamic_oops_basic_1_1";
            new ProblemDesigner().loadProblem(testPid);
        }
    }

}
