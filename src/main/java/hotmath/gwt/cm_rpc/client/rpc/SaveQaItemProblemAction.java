package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;


public class SaveQaItemProblemAction implements Action<RpcData>{
    
    String userName;
    String item;
    String problem;
    
    public SaveQaItemProblemAction(){}
    
    public SaveQaItemProblemAction(String userName, String item, String problem) {
        this.userName = userName;
        this.item = item;
        this.problem = problem;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    @Override
    public String toString() {
        return "SaveQaItemProblemAction [userName=" + userName + ", item=" + item + ", problem=" + problem + "]";
    }
}
