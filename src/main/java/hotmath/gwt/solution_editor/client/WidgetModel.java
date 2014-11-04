package hotmath.gwt.solution_editor.client;


public class WidgetModel  {

    String config;
    String type;
    String name;
    String value;

    public WidgetModel(String type) {
        this.type = type;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getKey() {
        return type + name + value;
    }
}
