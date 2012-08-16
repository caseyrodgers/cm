package hotmath.gwt.cm_admin.client.ui.assignment;


import hotmath.gwt.cm_rpc.client.model.GroupDto;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface GroupNameProperties extends PropertyAccess<String> {
    ModelKeyProvider<GroupDto> groupId();
    LabelProvider<GroupDto> name();
  }
