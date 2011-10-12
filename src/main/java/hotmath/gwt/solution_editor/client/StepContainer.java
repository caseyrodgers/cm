package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;
import hotmath.gwt.solution_editor.client.rpc.SolutionMetaStep;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Event;

/**
 * Provides wrapper around a step (step-unit and hint-unit)
 * 
 * @author casey
 * 
 */
public class StepContainer extends LayoutContainer {

    String figure;
    StepUnitWrapper hintWrapper;
    StepUnitWrapper textWrapper;
    SolutionMetaStep step;
    String pid;

    public StepContainer(String pid, int stepNumber, SolutionMeta meta, SolutionMetaStep step, String figure) {
        this.pid = pid;
        this.step = step;
        setStyleName("step-container");
        hintWrapper = new StepUnitWrapper("Hint",meta, new StepUnitHint(step));
        textWrapper = new StepUnitWrapper("Step Text",meta, new StepUnitText(step));
        this.figure = figure;
        add(new Html("<h2>Step Number: " + stepNumber));

        add(new FigureBox(pid,figure,new FigureBox.Callback() {
            @Override
            public void figureChanged(String figure) {
                StepContainer.this.figure = figure;
            }
        }));
        
        add(hintWrapper);
        add(textWrapper);
        addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STEP_CONTAINER_SELECTED, StepContainer.this));
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

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

}
