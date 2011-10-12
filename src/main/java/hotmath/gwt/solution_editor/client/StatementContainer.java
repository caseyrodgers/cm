package hotmath.gwt.solution_editor.client;

import hotmath.gwt.solution_editor.client.FigureBox.Callback;

import com.extjs.gxt.ui.client.widget.LayoutContainer;


/**
 * Provides wrapper around a step (step-unit and hint-unit)
 * 
 * @author casey
 * 
 */
public class StatementContainer extends LayoutContainer {
    
    StepUnitWrapper stepUnitWrapper;
    
    public StatementContainer(String title, final ProblemStatement statement) {

        setStyleName("statement-container");
        
        add(new FigureBox(statement.getMeta().getPid(),statement.getMeta().getFigure(),new Callback() {
            @Override
            public void figureChanged(String figure) {
                statement.getMeta().setFigure(figure);
            }
        }));        
        add(statement);
        
        stepUnitWrapper = new StepUnitWrapper(title,statement.getMeta(),statement);
        add(stepUnitWrapper);        
    }
    
    public StepUnitWrapper getStepUnitWrapper() {
        return stepUnitWrapper;
    }
}
