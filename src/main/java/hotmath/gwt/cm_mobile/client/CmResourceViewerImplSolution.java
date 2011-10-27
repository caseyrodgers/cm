package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
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
        CmMobileUser user = CatchupMathMobile.getUser();
        GetMobileSolutionAction action = new GetMobileSolutionAction(user.getUserId(),item.getFile());
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<SolutionResponse>() {
            @Override
            public void onSuccess(SolutionResponse result) {
                HTML html = new HTML(result.getTutorHtml());
                mainPanel.add(html);
                initializeTutor(item.getFile(),result.getSolutionData(),result.getTutorHtml(),"Solution", false,false);
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();               
            }
        });
        
        return this;
    }
    
    private native void initializeTutor(String pid, String solutionDataJs, String solutionHtml, String title, boolean hasShowWork,boolean shouldExpandSolution) /*-{
                                          $wnd.TutorManager.initializeTutor(pid, solutionDataJs,title,hasShowWork,shouldExpandSolution);
                                          }-*/;
}
