package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.History;

public class PrescriptionResourcePage implements IPage {

	InmhItemData item;
	String lesson;
	
	public PrescriptionResourcePage(String lesson, InmhItemData item) {
	    this.lesson = lesson;	    
		this.item = item;
	}
	
	@Override
    public void isNowActive() {
        /** empty */
    }

	
	@Override
	public String getViewTitle() {
		return "Learning Resource";
	}

	@Override
	public String getBackButtonText() {
		return "Lesson";
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
                //History.back();
                History.newItem(new TokenParser("lesson", lesson, 0).getHistoryTag());
            }
        });
        return actions;
    }
    
    
    @Override
    public TokenParser getBackButtonLocation() {
        return new TokenParser("lesson", lesson, 0);
    }    
    
    
    @Override
    public BackAction getBackAction() {
    	return null;
    }
    

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.PROGRAM;        
    }

    @Override
    public String getHeaderBackground() {
        return null;
    }

}
