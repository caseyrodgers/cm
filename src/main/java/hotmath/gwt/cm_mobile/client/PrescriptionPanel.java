package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.GetMobileSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
    
    
    PrescriptionData prescription;
    
    /** bind to the main panel */
    
    @UiField VerticalPanel mainPanel;
    
    public PrescriptionPanel() {
        this.prescription = CatchupMathMobile.getUser().getPrescripion();
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        String html = "<ul style='cmResources'>\n";
        for (PrescriptionSessionDataResource r : prescription.getCurrSession().getInmhResources()) {
            
            html += "<li>" +
                    r.getLabel() + "\n";
            if(r.getItems().size() > 0) {
                html += "<ul>\n";
                for(InmhItemData i: r.getItems()) {
                    html += "<li>" + i.getTitle() + "</li>\n";
                }
                html += "</ul>";
            }
            html += "</li>\n";
        }
        html += "</ul>";
        
        mainPanel.add(new HTML(html));
    }

    private void getSolution() {
        GetMobileSolutionAction action = new GetMobileSolutionAction(CatchupMathMobile.__instance.user.getUserId(),"cmextrasgeo_1_5_1_1_5");
        CatchupMathMobile.getCmService().execute(action, new AsyncCallback<SolutionResponse>() {
            @Override
            public void onSuccess(SolutionResponse result) {
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();               
            }
        });
    }    
}