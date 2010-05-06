package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
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
    Thermometer _thermometer = new Thermometer();
    
    /** bind to the main panel */
    
    @UiField VerticalPanel mainPanel;
    @UiField Button prevButton, nextButton;
    @UiField SimplePanel thermometerPanel;
    
    public PrescriptionPanel() {
        this.prescription = CatchupMathMobile.getUser().getPrescripion();
        
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        UnOrderedList ol = new UnOrderedList();
        for (PrescriptionSessionDataResource r : prescription.getCurrSession().getInmhResources()) {
            
            String type = r.getType();
            
            /** only show mobile ready resource
             * TODO: only return mobile types
             */
            if(type.equals("video") || type.startsWith("flashcards") || type.equals("activity") || type.equals("results"))
                continue;
            
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
        _thermometer.setPerecent(getPrescriptionCompletionPercent());
        thermometerPanel.add(_thermometer);
        mainPanel.add(ol);
    }
    
    private int getPrescriptionCompletionPercent() {
        if (prescription == null)
            return 0;
        double cs = prescription.getCurrSession().getSessionNumber();
        double ts = prescription.getSessionTopics().size();
        double d = ((cs * 100) / ts);
        int i = (int) Math.round(d);
        return i;
    }

    @UiHandler("nextButton")
    void handleNextButtonClick(ClickEvent e) {
        CmMobileUser user = CatchupMathMobile.getUser();
        int sessionNumber = user.getPrescripion().getCurrSession().getSessionNumber();
        if(sessionNumber > user.getPrescripion().getSessionTopics().size()) {
            Window.alert("No more lessons");
            return;
        }
        
        loadLesson(++sessionNumber);
    }
    
    private void loadLesson(int sessionNumber) {
        History.newItem("lesson:" + sessionNumber);                
    }
    
    @UiHandler("prevButton")
    void handlePrevButtonClick(ClickEvent e) {
        CmMobileUser user = CatchupMathMobile.getUser();
        int sessionNumber = user.getPrescripion().getCurrSession().getSessionNumber();
        if(sessionNumber < 1) {
            Window.alert("No previous lessons");
            return;
        }
        loadLesson(--sessionNumber);
    }
    
    static class ResourceButton extends Button {
        InmhItemData item;
        int ordinal;
        public ResourceButton(final int ordinal, final InmhItemData item) {
            super(item.getTitle());
            this.ordinal = ordinal;
            this.item = item;
            setWidth("200px");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent arg0) {
                    History.newItem("resource:" + item.getType() + ":" + ordinal); 
                }
            });
        }
    }
}

