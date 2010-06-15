package hotmath.assessment;

import hotmath.ProblemID;
import hotmath.gwt.shared.client.util.CmException;

import java.io.StringReader;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;


/** Can either be a Tutor or a Flash Widget
 * 
 * TODO: move to separate implementions.
 * 
 * @author casey
 *
 */
public class RppWidget {

    public String getWidgetJsonArgs() {
        return widgetJsonArgs;
    }

    public void setWidgetJsonArgs(String widgetJsonArgs) {
        this.widgetJsonArgs = widgetJsonArgs;
    }

    String file;
    String widgetJsonArgs;
    String title;
    ProblemID pid;

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
    public RppWidget(String jsonRecord) throws Exception {
        try {
            JSONParser parser = new JSONParser(new StringReader(jsonRecord));
            final JSONValue value = parser.nextValue();
            
            if(value.isComplex()) {
                JSONObject complex = (JSONObject)value;
                file = ((JSONString)complex.get("widget")).getValue();
                widgetJsonArgs = jsonRecord;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        if(file == null)
            throw new CmException("JSON string did not contain a 'widget' element");
    }

    public RppWidget(ProblemID pid) {
        this.pid = pid;
    }

    public ProblemID getPid() {
        return pid;
    }

    public void setPid(ProblemID pid) {
        this.pid = pid;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public boolean isSolution() {
        return this.pid != null;
    }
    
    @Override
    public String toString() {
        return String.format("pid=%s,file=%s",pid,file);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RppWidget) {
            RppWidget w = (RppWidget)obj;
            if(this.isSolution() && w.isSolution()) {
                return this.getPid().equals(w.getPid());
            }
            else {
                return this.getFile().equals(w.getFile());
            }
        }
        else {
            return super.equals(obj);
        }
    }
}
