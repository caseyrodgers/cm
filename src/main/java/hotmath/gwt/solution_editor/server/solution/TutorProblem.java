package hotmath.gwt.solution_editor.server.solution;

import java.util.ArrayList;
import java.util.List;

public class TutorProblem {
    
    String statementFigure;
    
    public TutorProblem(){}
    
    public TutorProblem(String createdBy, Identification id, String statement, String statementFigure) {
        this.identification = id;
        this.statement = statement;
        this.createdBy = createdBy;
        this.statementFigure = statementFigure;
    }
    
    String createdBy;

    Identification identification;
    
    String statement;
    
    List<TutorStepUnit> stepUnits = new ArrayList<TutorStepUnit>();

    /** TODO: integrate into SimpleXML as annotation
     *  move String to Statement object with figure attirubte
     */

    
    
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Identification getIdentification() {
        return identification;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public List<TutorStepUnit> getStepUnits() {
        return stepUnits;
    }

    public void setStepUnits(List<TutorStepUnit> stepUnits) {
        this.stepUnits = stepUnits;
    }

    public String getStatementFigure() {
        return statementFigure;
    }

    public void setStatementFigure(String statementFigure) {
        this.statementFigure = statementFigure;
    }
}
