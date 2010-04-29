package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.GetMobileSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionPanel extends Composite {

    /** define interface to the widget
     */
    interface PrescriptionPanelBinder extends UiBinder<Widget, PrescriptionPanel> {}

    /** Have GWT create an instance of the Binder interface/
     * 
     */
    private static PrescriptionPanelBinder uiBinder = GWT.create(PrescriptionPanelBinder.class);
    
    
    /** bind to the main panel */
    @UiField VerticalPanel mainPanel;
    @UiField Button checkTest;
    
    public PrescriptionPanel() {
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        mainPanel.add(new HTML("<h2>Prescription loading ...</h2>"));
        
        getPrescription();
    }

    private void getPrescription() {
        GetMobileSolutionAction action = new GetMobileSolutionAction(CatchupMathMobile.__instance.user.getUserId(),"cmextrasgeo_1_5_1_1_5");
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<SolutionResponse>() {
            @Override
            public void onSuccess(SolutionResponse result) {
                mainPanel.remove(0);
                mainPanel.add(new HTML(result.getTutorHtml()));
            }

            @Override
            public void onFailure(Throwable caught) {
                mainPanel.remove(0);
                caught.printStackTrace();               
                mainPanel.add(new HTML("<div style='color: red'><h1>Error Occurred</h2>" + caught.getMessage() + "</div>"));
            }
        });
    }    
}