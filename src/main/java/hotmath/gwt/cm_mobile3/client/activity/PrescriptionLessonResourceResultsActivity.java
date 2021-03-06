package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizResultsHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.QuizResultsMetaInfo;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PrescriptionLessonResourceResultsActivity  implements PrescriptionLessonResourceResultsView.Presenter {
	InmhItemData resourceItem;
	com.google.gwt.event.shared.EventBus  eventBus;
    private PrescriptionLessonResourceResultsView _lastView;

	public PrescriptionLessonResourceResultsActivity(com.google.gwt.event.shared.EventBus  eventBus, InmhItemData resourceItem) {
	    this.eventBus = eventBus;
		this.resourceItem = resourceItem;
		
		setupTriggers(this);
	}


    @Override
    public void setupView(final PrescriptionLessonResourceResultsView view) {
        _lastView = view;
        GetQuizResultsHtmlAction action = new GetQuizResultsHtmlAction(SharedData.getMobileUser().getBaseLoginResponse().getUserInfo().getRunId());
        
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        CatchupMathMobileShared.getCmService().execute(action,  new AsyncCallback<QuizResultsMetaInfo>() {
            @Override
            public void onSuccess(QuizResultsMetaInfo results) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                
                RpcData rdata = results.getRpcData();
                String resultHtml = rdata.getDataAsString("quiz_html");
                String resultJson = rdata.getDataAsString("quiz_result_json");
                int questionCount = rdata.getDataAsInt("quiz_question_count");
                int correctCount = rdata.getDataAsInt("quiz_correct_count");
                String title = rdata.getDataAsString("title");
                
                view.setQuizResults(title, resultHtml, resultJson, questionCount, correctCount);
            }
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
            }
        });
    }


    @Override
    public void loadWhiteboard(final ShowWorkPanel2 showWorkPanel, String pid) {
        // always use zero for run_id
        GetWhiteboardDataAction action = new GetWhiteboardDataAction(SharedData.getMobileUser().getUserId(), pid, 0);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmList<WhiteboardCommand>>() {
            final String flashId="";
            public void onSuccess(CmList<WhiteboardCommand> commands) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                showWorkPanel.loadWhiteboard(commands);
            }
            
            public void onFailure(Throwable caught) {
                Log.error("Error getting whiteboard data", caught);
                eventBus.fireEvent(new SystemIsBusyEvent(false));
            };
        });                
    }

    private void showWhiteboard_Gwt(String pid) {
        _lastView.showWhiteboard(pid);
    }

    private native void setupTriggers(PrescriptionLessonResourceResultsActivity x) /*-{
        $wnd.showWhiteboard_Gwt = function (pid) {
            x.@hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceResultsActivity::showWhiteboard_Gwt(Ljava/lang/String;)(pid);
        };
        
    }-*/;
}