package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplQuiz extends CmResourcePanelImplDefault {

    public Widget getResourcePanel() {
        
        return new QuizPage(true,UserInfo.getInstance().getTestSegment(),new CmAsyncRequest() {
            public void requestComplete(String requestData) {
            }

            public void requestFailed(int code, String text) {
            }
        });
    }
}
