package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.solution_editor.client.rpc.SolutionMetaStep;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Event;

public class StepContainer extends LayoutContainer {
    
    StepUnitWrapper hintWrapper;
    StepUnitWrapper textWrapper;
    SolutionMetaStep step;

    public StepContainer(int stepNumber, SolutionMetaStep step) {
        this.step = step;
        setStyleName("step-container");
        hintWrapper = new StepUnitWrapper("Hint", new StepUnitHint(step.getHint()) );
        textWrapper = new StepUnitWrapper("Step Text",new StepUnitText(step.getText()));
        add(new Html("<h2>Step Number: " + stepNumber));        
        add( hintWrapper );
        add( textWrapper );
        
        
        addListener(Events.OnClick, new Listener<BaseEvent>(){
            @Override
            public void handleEvent(BaseEvent be) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STEP_CONTAINER_SELECTED,StepContainer.this));
            } 
         });
        
        sinkEvents(Event.ONCLICK);             
    }

    public StepUnitWrapper getHintWrapper() {
        return hintWrapper;
    }
    
    public String getHintText() {
        return hintWrapper.getItem().getEditorText();        
    }

    public void setHintWrapper(StepUnitWrapper hintWrapper) {
        this.hintWrapper = hintWrapper;
    }

    public StepUnitWrapper getTextWrapper() {
        return textWrapper;
    }

    public String getStepText() {
        return textWrapper.getItem().getEditorText();
    }
    
    public void setTextWrapper(StepUnitWrapper textWrapper) {
        this.textWrapper = textWrapper;
    }

    public SolutionMetaStep getStep() {
        return step;
    }

    public void setStep(SolutionMetaStep step) {
        this.step = step;
    }
    
}
