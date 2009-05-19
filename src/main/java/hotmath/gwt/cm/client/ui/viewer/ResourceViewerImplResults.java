package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.util.RpcData;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplResults extends ResourceViewerContainer implements ResourceViewer {
    String _title;
    
    public ResourceViewerImplResults() {
        addStyleName("resource-viewer-impl-results");
    }

    private native void setSolutionQuestionAnswerIndex(String pid, String which)/*-{
        $wnd.setSolutionQuestionAnswerIndex(pid,which);
    }-*/;

    public Widget getResourcePanel(final InmhItemData resource) {

            CatchupMath.setBusy(true);
            
            PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
            s.getQuizResultsHtml(UserInfo.getInstance().getRunId(),new AsyncCallback() {
                public void onSuccess(Object result) {
                    try {
                        RpcData rdata = (RpcData)result;
                        String html = rdata.getDataAsString("quiz_html");
                        String resultJson = rdata.getDataAsString("quiz_result_json");
                        int total = rdata.getDataAsInt("quiz_question_count");
                        int correct = rdata.getDataAsInt("quiz_correct_count");
                        _title = rdata.getDataAsString("title");
    
                        addResource(new Html(html),resource.getTitle() + "  (" + correct + " out of " + total + ")");
                        layout();
                        
                        markAnswers(resultJson);
                    }
                    finally {
                        CatchupMath.setBusy(false);
                    }
                }
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                }
            });
            
            return this;
    }    
    
    
    /** Parse JSONized array of HaTestRunResult objects
     * 
     * @param resultJson
     */
    private void markAnswers(String resultJson) {
        try {
            JSONValue jsonValue = JSONParser.parse(resultJson);
            JSONArray a = jsonValue.isArray();
            
            for(int i=0;i<a.size();i++) {
                JSONObject o = a.get(i).isObject();
                String pid = o.get("pid").isString().stringValue();
                String correct = o.get("result").isString().stringValue();
                int answerIndex = (int)o.get("responseIndex").isNumber().doubleValue();
                
                setSolutionQuestionAnswerIndex(pid, Integer.toString(answerIndex));
                setQuizQuestionResult(i, correct);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private native void setQuizQuestionResult(int questionIndex, String result) /*-{
        $wnd.setQuizQuestionResult(questionIndex, result);
    }-*/;
}
