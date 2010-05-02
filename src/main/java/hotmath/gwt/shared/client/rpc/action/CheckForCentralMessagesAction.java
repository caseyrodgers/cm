package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CentralMessage;

public class CheckForCentralMessagesAction implements Action<CmList<CentralMessage>> {

    Integer uid;
    CmList<CentralMessage> messages;
    MessageActionType actionType;
    
    public CheckForCentralMessagesAction() {}
    
    public CheckForCentralMessagesAction(Integer uid) {
        this.actionType = MessageActionType.GET_UNREAD_MESSAGE;
        this.uid = uid;
    }
    
    public CheckForCentralMessagesAction(Integer uid,CmList<CentralMessage> messages) {
        this.actionType = MessageActionType.MARK_AS_VIEWED;
        this.uid = uid;
        this.messages = messages;
    }

    public CmList<CentralMessage> getMessages() {
        return messages;
    }

    public void setMessages(CmList<CentralMessage> messages) {
        this.messages = messages;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public MessageActionType getActionType() {
        return actionType;
    }

    public void setActionType(MessageActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "CheckForCentralMessagesAction [actionType=" + actionType + ", messages=" + messages + ", uid=" + uid
                + "]";
    }
    
    public enum MessageActionType{GET_UNREAD_MESSAGE,MARK_AS_VIEWED};
}
