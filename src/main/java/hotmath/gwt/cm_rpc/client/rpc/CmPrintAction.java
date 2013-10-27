package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class CmPrintAction implements Action<RpcData>{
    
    public enum PrintType {ASSIGNMENT}

    private PrintType printType;
    private int uid;
    private int other;;
    public CmPrintAction() {}
    
    public CmPrintAction(PrintType printType, int uid, int other) {
        this.printType = printType;
        this.uid = uid;
        this.other = other;
    }

    public PrintType getPrintType() {
        return printType;
    }

    public void setPrintType(PrintType printType) {
        this.printType = printType;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getOther() {
        return other;
    }

    public void setOther(int other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "CmPrintAction [printType=" + printType + ", uid=" + uid + ", other=" + other + "]";
    }

}
