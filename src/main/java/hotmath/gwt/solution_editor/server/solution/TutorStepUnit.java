package hotmath.gwt.solution_editor.server.solution;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Root;



@Root (name="stepunit")
abstract public class TutorStepUnit {
    List<String> figures = new ArrayList<String>();
    String figure;
    
    public TutorStepUnit() {
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public List<String> getFigures() {
        return figures;
    }

    public void setFigures(List<String> figures) {
        this.figures = figures;
    }
    public abstract String getContentAsString();
    
    
    public abstract Role getRole();
    
    public enum Role{HINT,STEP};
}

