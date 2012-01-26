package hotmath.assessment;

import hotmath.gwt.shared.client.util.CmException;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;


/** Can either be a Tutor solution or a Flash Widget
 * 
 * @author casey
 *
 */
public class RppWidget {
    
    static Logger __logger = Logger.getLogger(RppWidget.class);
    

    String widetKey;
    String widgetJsonArgs;
    String title;
    int numProblems;
    List<Integer> gradeLevels = new ArrayList<Integer>();

    public RppWidget() {
        /* empty */
    }
    
    public boolean isFlashRequired() {
        return widetKey.indexOf("swf") > -1?true:false;
    }
    
    
    /**
     * form of [PATH_TO_WIDGET|TITLE_OF_WIDGET]
     * 
     * Is a JSON block that describes this RPP.
     * 
     * The jsonRecord will be passed to the widget
     * to allow it be configured by the author.
     * 
     *  
     * Must have a file
     * @param def
     */
    public RppWidget(String pid) throws Exception {
        if(pid.startsWith("{")) {
            setJsonRecord(pid);
        }
        else {
            this.widetKey = pid;
        }
    }

    public RppWidget(String jsonRecord, List<Integer> gradeLevels) throws Exception {
        this(jsonRecord);
        this.gradeLevels.addAll(gradeLevels);
    }
    
    
    public void setJsonRecord(String jsonRecord) throws Exception {
        if(jsonRecord==null)
            return;
        
        JSONParser parser = new JSONParser(new StringReader(jsonRecord));
        final JSONValue value = parser.nextValue();
        if(value.isComplex()) {
            JSONObject complex = (JSONObject)value;
            widetKey = ((JSONString)complex.get("widget")).getValue();
            numProblems = ((JSONInteger)complex.get("limit")).getValue().intValue();
            widgetJsonArgs = jsonRecord;
            
            if(complex.get("level") != null) {
                gradeLevels.add(((JSONInteger)complex.get("level")).getValue().intValue());
                
            }
        }
        if(widetKey == null)
            throw new CmException("JSON string did not contain a 'widget' element");
    }
    
    /** Does this RPP widget support the given grade level
     * 
     * @param gradeLevel
     * @return
     */
    public boolean isGradeLevel(int gradeLevel) {
       for(Integer gl: gradeLevels)  {
           if(gl == gradeLevel) {
               return true;
           }
       }
       return false;
    }
    
    public String getInfoLabel() {
        if(numProblems > 0) {
            return "set of " + numProblems;
        }
        else {
            return "1 problem";
        }
    }
    
    public String getWidgetJsonArgs() {
        return widgetJsonArgs;
    }

    public void setWidgetJsonArgs(String widgetJsonArgs) {
        this.widgetJsonArgs = widgetJsonArgs;
    }

    public String getFile() {
        return widetKey;
    }

    public void setFile(String file) {
        this.widetKey = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public boolean isSolution() {
        return !this.getFile().contains(".swf");
    }
    
    /** return true if this RPP is a 
     *  dynamic solution
     * @return
     */
    public boolean isDynamicSolution() {
        return this.getWidgetJsonArgs() != null && this.getWidgetJsonArgs().trim().startsWith("{") && !isFlashRequired();
    }
    
    
    public List<Integer> getGradeLevels() {
        return gradeLevels;
    }

    public void setGradeLevels(List<Integer> gradeLevels) {
        this.gradeLevels = gradeLevels;
    }

    @Override
    public String toString() {
        return "RppWidget [widetKey=" + widetKey + ", widgetJsonArgs=" + widgetJsonArgs + ", title=" + title
                + ", numProblems=" + numProblems + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RppWidget) {
           RppWidget w = (RppWidget)obj;
           return this.getFile().equals(w.getFile());
        }
        else {
            return super.equals(obj);
        }
    }
}
