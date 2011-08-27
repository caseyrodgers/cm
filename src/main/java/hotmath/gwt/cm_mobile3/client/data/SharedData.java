package hotmath.gwt.cm_mobile3.client.data;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

/** centralized wrapper for shared global data structures
 * 
 * Contains composite objects for both Quiz and Prescription data.
 * 
 * @TODO: refactor these info a better data structure.  This is really 
 * a local datasource. Would be nice to be able to serialize this data
 * at some point into local storage as well.
 *
 *  
 * @author casey
 *
 */
public class SharedData {
    
    private static UserInfo userInfo;
    private static CmProgramFlowAction flowAction;
    
    static public CmProgramFlowAction getFlowAction() {
        return flowAction;
    }
    
    static public void setFlowAction(CmProgramFlowAction flowActionIn) {
        flowAction = flowActionIn;
    }
    
    static public UserInfo getUserInfo() {
        return userInfo;
    }
    
    static public void setUserInfo(UserInfo userInfoIn) {
        userInfo = userInfoIn;
    }

    
    /** Search through prescription data and return the InmhItemData that is 
     * in the ordinal position of the named resource type.
     * 
     * @param type
     * @param ordinal
     */
    public static InmhItemData findInmhDataInPrescriptionByOrdinal(String type, int ordinal) throws Exception  {
        for(PrescriptionSessionDataResource dr: flowAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().getInmhResources()) {
            if(type.equals(dr.getType())) {
                for(int i=0,t=dr.getItems().size();i<t;i++) {
                    if(i == ordinal) {
                        return dr.getItems().get(i);
                    }
                }
            }
        }
        throw new Exception("No " + type + " resource at ordinal position '" + ordinal + "'");
    }
    
    public static int findOrdinalPositionOfResource(InmhItemData itemIn) {
        String type = itemIn.getType();
        for(PrescriptionSessionDataResource dr: flowAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().getInmhResources()) {
            if(type.equals(dr.getType())) {
                for(int i=0,t=dr.getItems().size();i<t;i++) {
                    InmhItemData item = dr.getItems().get(i);
                    if(item.getTitle().equals(itemIn.getTitle())) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
}
