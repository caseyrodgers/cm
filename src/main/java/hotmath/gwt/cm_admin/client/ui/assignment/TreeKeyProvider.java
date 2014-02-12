package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.SubjectDto;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class TreeKeyProvider implements ModelKeyProvider<BaseDto> {
    @Override
    public String getKey(BaseDto item) {
        if (item instanceof SubjectDto) {
            return ((SubjectDto) item).getSubject();
        } else {
            return (item instanceof FolderDto ? "f-" : "m-") + item.getId().toString();
        }
    }
}