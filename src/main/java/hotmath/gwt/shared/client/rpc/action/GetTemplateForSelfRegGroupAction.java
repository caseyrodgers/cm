package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.Action;

public class GetTemplateForSelfRegGroupAction implements Action<StudentModelI>{

	private static final long serialVersionUID = 5052941219402392180L;
	private Integer groupId;
    
    public GetTemplateForSelfRegGroupAction(){}
    
    public GetTemplateForSelfRegGroupAction(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
