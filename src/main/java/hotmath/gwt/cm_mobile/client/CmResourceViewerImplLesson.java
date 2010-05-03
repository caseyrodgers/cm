package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class CmResourceViewerImplLesson extends Composite implements CmMobileResourceViewer {
    
    interface LessonPanelBinder extends UiBinder<Widget, CmResourceViewerImplLesson> {}
    private static LessonPanelBinder uiBinder = GWT.create(LessonPanelBinder.class);
    
    
    @UiField VerticalPanel mainPanel;
   
    public CmResourceViewerImplLesson() {
        initWidget(uiBinder.createAndBindUi(this));
        
    }
    
    @Override
    public Widget getViewer(InmhItemData item) {
        GetReviewHtmlAction action = new GetReviewHtmlAction("/hotmath_help/" + item.getFile(), "/hotmath_help/topics");
        
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                HTML html = new HTML(result.getDataAsString("html"));
                mainPanel.add(html);
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();               
            }
        });
        
        return this;
    }
}
