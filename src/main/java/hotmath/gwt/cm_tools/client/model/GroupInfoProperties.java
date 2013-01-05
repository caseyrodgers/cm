package hotmath.gwt.cm_tools.client.model;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface GroupInfoProperties extends PropertyAccess<String> {

    ModelKeyProvider<GroupInfoModel> id();
    LabelProvider<GroupInfoModel> groupName();
}

