package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.History;

public class PrescriptionResourcePage implements IPage {

	InmhItemData item;
	
	public PrescriptionResourcePage(InmhItemData item) {
		this.item = item;
	}
	
	@Override
	public String getTitle() {
		return "Prescription Resource";
	}

	@Override
	public String getBackButtonText() {
		return "<< Lesson";
	}

	public InmhItemData getItem() {
    	return item;
    }

	public void setItem(InmhItemData item) {
    	this.item = item;
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        List<ControlAction> actions = new ArrayList<ControlAction>();
        actions.add(new ControlAction("Back to Lesson") {
            @Override
            public void doAction() {
                History.back();
                // Controller.navigateBack();
            }
        });
        return actions;
    }
}
