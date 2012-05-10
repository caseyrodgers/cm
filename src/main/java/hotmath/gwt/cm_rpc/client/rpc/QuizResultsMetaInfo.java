package hotmath.gwt.cm_rpc.client.rpc;


import com.google.gwt.user.client.rpc.IsSerializable;


/** Represents the returned values from a request for a 
 * quiz results.  Either old school HTML, or PDF
 * 
 * @author casey
 *
 */
public class QuizResultsMetaInfo implements Response, IsSerializable {
   
    public static enum QuizResultsType  {HTML,PDF};
    
    public QuizResultsMetaInfo(){}
    
    public QuizResultsMetaInfo(QuizResultsType type) {
        this.type = type;
    }
    
    QuizResultsType type;
    String pdfFileName;
    
    RpcData rpcData;
    

    public QuizResultsType getType() {
        return type;
    }

    public void setType(QuizResultsType type) {
        this.type = type;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public RpcData getRpcData() {
        return rpcData;
    }

    public void setRpcData(RpcData rpcData) {
        this.rpcData = rpcData;
    }

    @Override
    public String toString() {
        return "QuizResultsMetaInfo [type=" + type + ", pdfFileName=" + pdfFileName + ", rpcData=" + (rpcData!=null?true:false) + "]";
    }
    
    
}
