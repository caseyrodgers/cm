package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.data.BaseModel;


public class WidgetModel  extends BaseModel{
    
    private String type;
    private String config;
    private String value;

    public WidgetModel(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
