package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.LessonResult;

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
        loadItem(item);
        return this;
    }
    
    public void loadItem(InmhItemData item) {
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_START));
        GetReviewHtmlAction action = new GetReviewHtmlAction(item.getFile());
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<LessonResult>() {
            @Override
            public void onSuccess(LessonResult result) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                mainPanel.clear();
                mainPanel.add(new HTML(result.getLesson()));
            }

            @Override
            public void onFailure(Throwable caught) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                caught.printStackTrace();               
            }
        });
    }
}
