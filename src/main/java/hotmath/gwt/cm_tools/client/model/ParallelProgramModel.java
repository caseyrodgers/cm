package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ParallelProgramModel extends BaseModel implements Response {

	public ParallelProgramModel(){
        /** empty */
    }

    public ParallelProgramModel(String name, Integer id, Integer adminId, String password, Integer cmProgId) {
        setName(name);
        setId(id);
        setAdminId(adminId);
        setPassword(password);
        setCmProgId(cmProgId);
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getName() {
        return get("name");
    }

    public void setId(Integer id) {
        set("id", id);
    }

    public Integer getId() {
        return get("id");
    }

    public void setAdminId(int adminId) {
        set("adminId", adminId);
    }

    public Integer getAdminId() {
        return get("adminid");
    }

    public void setCmProgId(Integer id) {
        set("cmProgId", id);
    }

    public Integer getCmProgId() {
        return get("cmProgId");
    }

    public void setPassword(String password) {
        set("password", password);
    }

    public Integer getPassword() {
        return get("password");
    }
}