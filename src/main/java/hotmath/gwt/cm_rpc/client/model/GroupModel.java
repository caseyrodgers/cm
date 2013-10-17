package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class GroupModel implements Response {
    
    private int id;
    private String name;
    private String description;

    public GroupModel() {}
    
    public GroupModel(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
