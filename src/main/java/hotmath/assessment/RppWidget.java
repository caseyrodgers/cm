package hotmath.assessment;

import hotmath.gwt.shared.client.util.CmException;

import java.io.StringReader;

import org.apache.log4j.Logger;

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
    public RppWidget(String jsonRecord) throws Exception {
        setJsonRecord(jsonRecord);
    }
    
    public RppWidget(String pid, String json) throws Exception {
        this.widetKey = pid;
        setJsonRecord(json);
    }
    
    public void setJsonRecord(String jsonRecord) throws Exception {
        if(jsonRecord==null)
            return;
        
        JSONParser parser = new JSONParser(new StringReader(jsonRecord));
        final JSONValue value = parser.nextValue();
        if(value.isComplex()) {
            JSONObject complex = (JSONObject)value;
            widetKey = ((JSONString)complex.get("widget")).getValue();
            widgetJsonArgs = jsonRecord;
        }
        if(widetKey == null)
            throw new CmException("JSON string did not contain a 'widget' element");
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
    
    @Override
    public String toString() {
        return String.format("widgetKey=%s,widgetJsonArgs=%s",widetKey,widgetJsonArgs);
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
