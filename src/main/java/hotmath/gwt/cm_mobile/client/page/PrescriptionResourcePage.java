package hotmath.gwt.cm_mobile.client.page;

import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_mobile.client.CatchupMathMobile;
import hotmath.gwt.cm_mobile.client.ControlAction;
import hotmath.gwt.cm_mobile.client.ControlPanel;
import hotmath.gwt.cm_mobile.client.Controller;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

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
    public void setupControlFloater() {
        ControlPanel cp = CatchupMathMobile.__instance.getControlPanel();
        List<ControlAction> actions = new ArrayList<ControlAction>();
        actions.add(new ControlAction("Back to Lesson") {
            @Override
            public void doAction() {
                Controller.navigateBack();
            }
        });
        cp.setControlActions(actions);
        cp.showControlPanelFloater();        
    }
}
