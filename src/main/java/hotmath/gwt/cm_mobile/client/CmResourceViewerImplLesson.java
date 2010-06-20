package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;
import hotmath.gwt.cm_tools.client.model.StringHolder;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
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
        GetReviewHtmlAction action = new GetReviewHtmlAction(item.getFile());
        
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<LessonResult>() {
            @Override
            public void onSuccess(LessonResult result) {
                mainPanel.add(new Html(result.getLesson()));
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();               
            }
        });
        
        return this;
    }
}
