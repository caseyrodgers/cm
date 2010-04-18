package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;


/** Encapsulates an Admin Auto Registration configuration
 * 
 * @author casey
 *
 */
public class AutoRegistrationSetup implements Response {
    String setupName;
    String nameTemplate;
    Integer programId;
    Integer errorCount;
    
    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }

    public void setNameTemplate(String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }

    public String getSetupName() {
        return setupName;
    }

    public void setSetupName(String setupName) {
        this.setupName = setupName;
    }

    public CmList<AutoRegistrationEntry> getEntries() {
        return entries;
    }

    public void setEntries(CmList<AutoRegistrationEntry> entries) {
        this.entries = entries;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public AutoRegistrationSetup() {}
    
    private CmList<AutoRegistrationEntry> entries = new CmArrayList<AutoRegistrationEntry>();
}
