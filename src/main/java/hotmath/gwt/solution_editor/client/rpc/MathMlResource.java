package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** Encapsulates a MathML Resource
 * Gives it a name and holds the 
 * mathml associated with resource.
 * 
 * @author casey
 *
 */
public class MathMlResource implements Response{
    
    String name;
    String mathMl;
    public MathMlResource() {
        name = "" + System.currentTimeMillis();
    }
    
    public MathMlResource(String name, String mathMl) {
        this.name = name;
        this.mathMl = mathMl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMathMl() {
        return mathMl;
    }

    public void setMathMl(String mathMl) {
        this.mathMl = mathMl;
    }
}
