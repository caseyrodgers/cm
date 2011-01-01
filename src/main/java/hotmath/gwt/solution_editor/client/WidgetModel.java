package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class WidgetModel extends BaseModelData  {
    
    public WidgetModel(String type) {
        set("type", type);
    }
    
    public String getConfig() {
        return get("config");
    }
    
    public void setConfig(String config) {
        set("config", config);
    }
    
    public String getType() {
        return get("type");
    }
    
    public String getName() {
        return get("name");
    }
    
    public void setName(String name) {
        set("name", name);
    }
    
    public String getValue() {
        return get("value");
    }
    
    public void setValue(String value) {
        set("value",value);
    }
}
