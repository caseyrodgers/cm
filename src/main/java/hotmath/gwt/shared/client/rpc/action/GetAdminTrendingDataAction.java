package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;

public class GetAdminTrendingDataAction implements Action<CmAdminTrendingDataI>{


    Integer adminId;
    GetStudentGridPageAction dataAction;
    DataType dataType;
    
    public GetAdminTrendingDataAction() {}
    
    public GetAdminTrendingDataAction(DataType dataType, Integer adminId, GetStudentGridPageAction action) {
        this.dataType = dataType;    
        this.adminId = adminId;
        this.dataAction = action;
    }

    public GetStudentGridPageAction getDataAction() {
        return dataAction;
    }

    public void setDataAction(GetStudentGridPageAction dataAction) {
        this.dataAction = dataAction;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
    
    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
    
    
    @Override
    public String toString() {
        return "GetAdminTrendingDataAction [adminId=" + adminId + ", dataAction=" + dataAction + ", dataType="
                + dataType + "]";
    }    

    public enum DataType{
        /** Use the full history for each user
         * 
         */
        FULL_HISTORY,
        
        
        /** use only the active program assigned
         * to each user.
         * 
         */
        ONLY_ACTIVE};
        
        
}
