package hotmath.gwt.solution_editor.server.solution;

import java.util.List;

import org.simpleframework.xml.Root;



@Root (name="stepunit")
public class TutorStepUnitImplProof extends TutorStepUnit {
    
    String postulateText;
    String postNumber;
    String justification;
    
    
    public TutorStepUnitImplProof() {
    }
    
    public TutorStepUnitImplProof(String postulateText, String postNumber, String justification) {
        this.postulateText = postulateText;
        this.postNumber = postNumber;
        this.justification = justification;
    }
    
    public String getPostulateText() {
        return postulateText;
    }

    public void setPostulateText(String postulateText) {
        this.postulateText = postulateText;
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public List<String> getFigures() {
        return figures;
    }

    public void setFigures(List<String> figures) {
        this.figures = figures;
    }
    
    @Override
    public String getContentAsString() {
        return postulateText + ", " + postNumber + ", " + justification;
    }
}

