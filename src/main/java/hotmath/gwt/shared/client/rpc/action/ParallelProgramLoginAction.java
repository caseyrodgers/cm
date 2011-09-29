package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;


/** Log user into Parallel Program
 * 
 * @author bob
 *
 */
public class ParallelProgramLoginAction implements Action<RpcData> {

    Integer parallelProgId;
    Integer progInstId;
    String  user;
    String  password;

    public ParallelProgramLoginAction() {
    }

    public ParallelProgramLoginAction(Integer parallelProgId, String password) {
        this.parallelProgId = parallelProgId;
        this.password = password;
    }

    public Integer getParallelProgId() {
        return parallelProgId;
    }

    public void setParallProgId(Integer parallelProgId) {
        this.parallelProgId = parallelProgId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
