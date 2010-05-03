package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_rpc.client.rpc.GetMobileSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
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
        UnOrderedList ol = new UnOrderedList();
        for (PrescriptionSessionDataResource r : prescription.getCurrSession().getInmhResources()) {
            ListItem li = new ListItem();
            ol.add(li);
            li.setText(r.getLabel());
            if(r.getItems().size() > 0) {
                UnOrderedList ol2 = new UnOrderedList();
                li.add(ol2);
                for(int ordinal=0;ordinal<r.getItems().size();ordinal++) {
                    InmhItemData i = r.getItems().get(ordinal);
                    ListItem li2 = new ListItem();
                    li2.add(new ResourceButton(ordinal, i));
                    ol2.add(li2);
                }
            }
        }
        mainPanel.add(ol);
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
    
    
    static class ResourceButton extends Button {
        InmhItemData item;
        int ordinal;
        public ResourceButton(final int ordinal, final InmhItemData item) {
            super(item.getTitle());
            this.ordinal = ordinal;
            this.item = item;
            setWidth("250px");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent arg0) {
                    History.newItem("resource:" + item.getType() + ":" + ordinal); 
                }
            });
        }
    }
}

