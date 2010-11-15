package hotmath.gwt.cm_mobile2.client;

import hotmath.gwt.cm_mobile2.client.page.PrescriptionPage;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.ListItem;
import hotmath.gwt.cm_mobile_shared.client.Thermometer;
import hotmath.gwt.cm_mobile_shared.client.UnOrderedList;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionPanel extends AbstractPagePanel {

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
    
    PrescriptionPage pPage;
    UnOrderedList listItems = new UnOrderedList();
    
    public PrescriptionPanel(PrescriptionPage pPage) {
    	this.pPage = pPage;
    	
    	/** to get started... probably, should pass data in
    	 * 
    	 */
        this.prescription = CatchupMathMobileShared.getUser().getPrescripion();
        
        
        /** do the binding */
        initWidget(uiBinder.createAndBindUi(this));
        
        loadPrescriptionSession(prescription.getCurrSession());
        _thermometer.setPercent(getPrescriptionCompletionPercent());
        mainPanel.add(listItems);  
        thermometerPanel.add(_thermometer);
    }
    
    
    private void loadPrescriptionSession(PrescriptionSessionData session) {
    	
    	/** start fresh */
        listItems.clear();
        
        for (PrescriptionSessionDataResource r : session.getInmhResources()) {
            
            String type = r.getType();
            
            /** only show mobile ready resource
             * TODO: only return mobile types
             */
            if(type.equals("video") || type.startsWith("flashcards") || type.equals("activity") || type.equals("results"))
                continue;
            
            ListItem li = new ListItem();
            listItems.add(li);
            li.setText(r.getLabel());
            if(r.getItems().size() > 0) {
                UnOrderedList ol2 = new UnOrderedList();
                li.add(ol2);
                for(int ordinal=0;ordinal<r.getItems().size();ordinal++) {
                    InmhItemData i = r.getItems().get(ordinal);
                    ListItem li2 = new ListItem();
                    li2.add(new ResourceButton(pPage,ordinal, i));
                    ol2.add(li2);
                }
            }
        }
        _thermometer.setPercent(getPrescriptionCompletionPercent());
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
        CmMobileUser user = CatchupMathMobileShared.getUser();
        int sessionNumber = this.prescription.getCurrSession().getSessionNumber();
        if((sessionNumber+1) >= this.prescription.getSessionTopics().size()) {
            Window.alert("No more lessons");
            return;
        }
        
        loadLesson(++sessionNumber);
    }
    
    private void loadLesson(int sessionNumber) {
        
        History.newItem("lesson:" + sessionNumber);   
        if(true)
            return;
        

        
		GetPrescriptionAction action = new GetPrescriptionAction(CatchupMathMobileShared.getUser().getRunId(), sessionNumber, true);
		CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<PrescriptionSessionResponse>() {
			public void onSuccess(PrescriptionSessionResponse prescriptionSession) {
				PrescriptionPanel.this.prescription = prescriptionSession.getPrescriptionData();
				
				loadPrescriptionSession(prescriptionSession.getPrescriptionData().getCurrSession());
			}

			@Override
			public void onFailure(Throwable arg0) {
				Window.alert(arg0.getMessage());
			}
		});
    }
    
    @UiHandler("prevButton")
    void handlePrevButtonClick(ClickEvent e) {
        CmMobileUser user = CatchupMathMobileShared.getUser();
        int sessionNumber = this.prescription.getCurrSession().getSessionNumber();
        if(sessionNumber < 1) {
            Window.alert("No previous lessons");
            return;
        }
        loadLesson(--sessionNumber);
    }
    
    static class ResourceButton extends Button {
        InmhItemData item;
        int ordinal;
        public ResourceButton(final PrescriptionPage pPage, final int ordinal, final InmhItemData item) {
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

