package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.JSOBaseModel;
import hotmath.gwt.cm_core.client.JSOModel;

/** Defines a Widget Definition as defined in JSON 
 * 
 * @author casey
 *
 */
public class WidgetDefModel extends JSOBaseModel {
    
    String id;
    String type;
    String value;
    Integer width;
    Integer height;
    String format;
    
    public WidgetDefModel() {
        this(JSOModel.fromJson("{}"));
    }
    
    public WidgetDefModel(JSOModel data) {
        super(data);
        id = get("id");
        type = get("type");
        value = get("value");
        width = getInt(get("width"));
        height = getInt(get("height"));
        format = get("format");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
    
    
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /** return the complete definition of this widget
     * 
     * @return
     */
    public String getJson() {
        String json = "{type:'" + type + "',value:'" + value + "', format:'" + format + "', width:" + width + ", height:" + height + "}";
        return json;
    }
    
    public Integer getInt(String o) {
        try {
            return Integer.parseInt(o);
        }
        catch(Exception e) {
            return 0;
        }
    }
    
}