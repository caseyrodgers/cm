package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.rpc.GetMobileSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class CmResourceViewerImplSolution extends Composite implements CmMobileResourceViewer {
    
    interface SolutionPanelBinder extends UiBinder<Widget, CmResourceViewerImplSolution> {}
    private static SolutionPanelBinder uiBinder = GWT.create(SolutionPanelBinder.class);
    
    
    @UiField VerticalPanel mainPanel;
   
    public CmResourceViewerImplSolution() {
        initWidget(uiBinder.createAndBindUi(this));
        
    }
    
    @Override
    public Widget getViewer(final InmhItemData item) {
        CmMobileUser user = CatchupMathMobileShared.getUser();
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_START));
        GetMobileSolutionAction action = new GetMobileSolutionAction(user.getUserId(),item.getFile());
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<SolutionResponse>() {
            @Override
            public void onSuccess(SolutionResponse result) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                HTML html = new HTML(result.getTutorHtml());
                mainPanel.add(html);
                initializeTutor(item.getFile(),result.getSolutionData(),"Solution", false,false);
            }

            @Override
            public void onFailure(Throwable caught) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                caught.printStackTrace();
                mainPanel.add(new HTML("Could not load solution: " + caught.getMessage()));               
            }
        });
        
        return this;
    }
    
    private native void initializeTutor(String pid, String solutionDataJs, String title, boolean hasShowWork,boolean shouldExpandSolution) /*-{
                                          $wnd.TutorManager.initializeTutor(pid, solutionDataJs,title,hasShowWork,shouldExpandSolution);
                                          }-*/;
}
