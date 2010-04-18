package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** Provide information about current build version numbers
 *  for CM
 *  
 * @author casey
 *
 */
public class CatchupMathVersion implements Response{
    int version;
    
    public CatchupMathVersion() {}
    
    public CatchupMathVersion(Integer version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "CatchupMathVersion [version=" + version + "]";
    }
}
