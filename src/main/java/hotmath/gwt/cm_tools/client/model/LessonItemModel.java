package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class LessonItemModel implements Response{

    String name, file, prescribed;
    
	CmList<String> stateStandards = new CmArrayList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPrescribed() {
        return prescribed;
    }

    public void setPrescribed(String prescribed) {
        this.prescribed = prescribed;
    }

    public CmList<String> getStateStandards() {
        return stateStandards;
    }

    public void setStateStandards(CmList<String> stateStandards) {
        this.stateStandards = stateStandards;
    }
}
