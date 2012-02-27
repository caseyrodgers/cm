package hotmath.gwt.cm_mobile_shared.client.data;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
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
        if(flowAction == null) {
            flowAction = new CmProgramFlowAction();
        }
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

    static public int getCountLessonsRemaining() {
        int cnt=SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics().size();
        for(SessionTopic topic: SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics()) {
            if(topic.isComplete()) {
                cnt--;
            }
        }
        return cnt;
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
    
    
    public static String calculateCurrentLessonStatus(String topic) {
        for(PrescriptionSessionDataResource dr: flowAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().getInmhResources()) {
            if("practice".equals(dr.getType())) {
                int viewed=0;
                int total=0;
                for(int i=0,t=dr.getItems().size();i<t;i++) {
                    InmhItemData item = dr.getItems().get(i);
                    if(item.isViewed()) {
                        viewed++;
                    }
                    total++;
                }
                
                return viewed + " of " + total;
            }
        }
        return "unknown status '" + topic + "'";
    }
    
    
    /** Return the InmhItemData for the named resource file or null
     * 
     * @param type
     * @param file
     * @return
     * @throws Exception
     */
    public static InmhItemData findInmhDataInPrescriptionByFile(String type, String file) throws Exception {
        for(PrescriptionSessionDataResource dr: flowAction.getPrescriptionResponse().getPrescriptionData().getCurrSession().getInmhResources()) {
            if(type.equals(dr.getType())) {
                for(int i=0,t=dr.getItems().size();i<t;i++) {
                    if(dr.getItems().get(i).getFile().equals(file)) {
                        return dr.getItems().get(i);
                    }
                }
            }
        }
        return null;
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
