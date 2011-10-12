package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class StepConverter implements Converter<String> {

    public String read(InputNode node) {
       return new String();
    }

    public void write(OutputNode node, String external) {
        String figure=null;
        try {
            String sfigure = node.getAttributes().get("figure").getValue();
            if(sfigure != null) {
                figure = sfigure;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        if(figure != null) {
            node.setAttribute("figure",figure);
        }
        node.setValue(external);
    }
 }