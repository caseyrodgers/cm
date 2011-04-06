package hotmath.gwt.cm_rpc.client.rpc;


public class SaveQaItemAction implements Action<RpcData>{
    
    String userName;
    String item;
    boolean verified;
    
    public SaveQaItemAction(){}
    
    public SaveQaItemAction(String userName, String item, boolean verified) {
        this.userName = userName;
        this.item = item;
        this.verified = verified;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "SaveQaItemAction [userName=" + userName + ", item=" + item + ", verified=" + verified + "]";
    }

}
