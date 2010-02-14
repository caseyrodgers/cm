package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.GetQuizResultsHtmlAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplResults extends
		CmResourcePanelImplWithWhiteboard {
	String _title;
	Widget _quizPanel;

	static final String STYLE_NAME = "resource-viewer-impl-results";

	public ResourceViewerImplResults() {
		addStyleName(STYLE_NAME);
		setScrollMode(Scroll.AUTOY);
	}

	@Override
	public String getContainerStyleName() {
		return STYLE_NAME;
	}

	@Override
	public Integer getOptimalWidth() {
		// TODO Auto-generated method stub
		return 520;
	}

	@Override
	public void whiteboardIsReady() {
		ShowWorkPanel.setWhiteboardIsReadonly();
	}

	/**
	 * Select the correct question response for question for pid
	 * 
	 * @param pid
	 *            The solution which question response to set
	 * @param which
	 *            The index of the response set to select
	 * @param disabled
	 *            Should the selection control be made disabled.
	 */
	private native void setSolutionQuestionAnswerIndex(String pid,
			String which, boolean disabled)/*-{
		$wnd.setSolutionQuestionAnswerIndex(pid,which,disabled);
	}-*/;

	public Widget getResourcePanel() {


		CmBusyManager.setBusy(true, false);

		CmServiceAsync s = CmShared.getCmService();
		GetQuizResultsHtmlAction action = new GetQuizResultsHtmlAction(UserInfo
				.getInstance().getRunId());
		s.execute(action, new CmAsyncCallback<RpcData>() {
			public void onSuccess(RpcData result) {
				try {
					RpcData rdata = result;
					String html = rdata.getDataAsString("quiz_html");
					String resultJson = rdata
							.getDataAsString("quiz_result_json");
					int total = rdata.getDataAsInt("quiz_question_count");
					int correct = rdata.getDataAsInt("quiz_correct_count");
					_title = rdata.getDataAsString("title");

					_quizPanel = new Html(html);

					addResource(_quizPanel, getResourceItem().getTitle() + ": "
							+ correct + " out of " + total);

					markAnswers(resultJson);
				} finally {
					CmBusyManager.setBusy(false);
				}
			}

			public void onFailure(Throwable caught) {
				CmBusyManager.setBusy(false);
				super.onFailure(caught);
			}
		});

		return this;
	}

	/**
	 * Parse JSONized array of HaTestRunResult objects
	 * 
	 * and mark each question with result state (correct,incorrect,unanswered)
	 * and disable each radio button to disable each.
	 * 
	 * @param resultJson
	 */
	private void markAnswers(String resultJson) {
		try {
			JSONValue jsonValue = JSONParser.parse(resultJson);
			JSONArray a = jsonValue.isArray();

			for (int i = 0; i < a.size(); i++) {
				JSONObject o = a.get(i).isObject();
				String pid = o.get("pid").isString().stringValue();
				String correct = o.get("result").isString().stringValue();
				int answerIndex = (int) o.get("responseIndex").isNumber()
						.doubleValue();

				setQuizQuestionResult(pid, correct);
				if (!correct.equals("Unanswered"))
					setSolutionQuestionAnswerIndex(pid, Integer
							.toString(answerIndex), true);
				else
					setSolutionQuestionAnswerIndex(pid, "-1", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private native void setQuizQuestionResult(String pid, String result) /*-{
		$wnd.setQuizQuestionResult(pid, result);
	}-*/;

	@Override
	public Widget getTutorDisplay() {
		return _quizPanel;
	}

	@Override
	public void setupShowWorkPanel(ShowWorkPanel whiteboardPanel) {
		whiteboardPanel.setPid("quiz:" + UserInfo.getInstance().getTestId());
	}
}
