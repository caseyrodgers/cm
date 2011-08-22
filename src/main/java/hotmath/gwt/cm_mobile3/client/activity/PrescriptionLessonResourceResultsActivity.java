package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_rpc.client.rpc.GetQuizResultsHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class PrescriptionLessonResourceResultsActivity  implements PrescriptionLessonResourceResultsView.Presenter {
	InmhItemData resourceItem;
	com.google.gwt.event.shared.EventBus eventBus;

	public PrescriptionLessonResourceResultsActivity(com.google.gwt.event.shared.EventBus eventBus, InmhItemData resourceItem) {
	    this.eventBus = eventBus;
		this.resourceItem = resourceItem;
	}


    @Override
    public void setupView(final PrescriptionLessonResourceResultsView view) {
        GetQuizResultsHtmlAction action = new GetQuizResultsHtmlAction(CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo().getRunId());
        
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        CatchupMathMobileShared.getCmService().execute(action,  new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData rdata) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                
                String resultHtml = rdata.getDataAsString("quiz_html");
                String resultJson = rdata.getDataAsString("quiz_result_json");
                int questionCount = rdata.getDataAsInt("quiz_question_count");
                int correctCount = rdata.getDataAsInt("quiz_correct_count");
                String title = rdata.getDataAsString("title");
                
                
                view.setQuizResults(title, resultHtml, resultJson, questionCount, correctCount);
                
                //view.setQuizResultsHtml(
            }
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
            }
        });
    }
}