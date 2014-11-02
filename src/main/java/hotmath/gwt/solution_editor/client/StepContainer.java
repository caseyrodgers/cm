package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.model.SolutionMetaStep;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

/**
 * Provides wrapper around a step (step-unit and hint-unit)
 * 
 * @author casey
 * 
 */
public class StepContainer extends FocusPanel {

    String figure;
    StepUnitWrapper hintWrapper;
    StepUnitWrapper textWrapper;
    SolutionMetaStep step;
    String pid;

    public StepContainer(String pid, int stepNumber, SolutionMeta meta, SolutionMetaStep step, String figure) {
        this.pid = pid;
        this.step = step;
        setStyleName("step-container");
        
        
        FlowLayoutContainer _main = new FlowLayoutContainer();
        
        hintWrapper = new StepUnitWrapper("Hint",meta, new StepUnitHint(step));
        textWrapper = new StepUnitWrapper("Step Text",meta, new StepUnitText(step));
        this.figure = figure;
        _main.add(new HTML("<h2>Step Number: " + stepNumber));

        _main.add(new FigureBox(pid,figure,new FigureBox.Callback() {
            @Override
            public void figureChanged(String figure) {
                StepContainer.this.figure = figure;
            }
        }));
        
        _main.add(hintWrapper);
        _main.add(textWrapper);
        
        addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventBus.getInstance().fireEvent(new CmEvent(EventTypes.STEP_CONTAINER_SELECTED, StepContainer.this));				
			}
		});
        
        setWidget(_main);
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
