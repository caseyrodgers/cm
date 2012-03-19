package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.CmProgramType;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ProgramTypeModel extends BaseModelData {

	protected ProgramTypeModel() {
    }

    public ProgramTypeModel(String label, CmProgramType type) {
        set("label", label);
        set("type", type);
    }

    public void setLabel(String label) {
        set("label", label);
    }

    public String getLabel() {
        return get("label");
    }

    public void setType(CmProgramType type) {
        set("type", type);
    }

    public CmProgramType getType() {
        return get("type");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ProgramTypeModel) {
            ProgramTypeModel mobj = (ProgramTypeModel) obj;
            boolean b = mobj.getLabel().equals(this.getLabel()) &&
            		    mobj.getType().equals(this.getType());
            return b;
        }
        return super.equals(obj);
    }
}
