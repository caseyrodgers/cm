package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.CmProgramType;

public class ProgramTypeModel  {

	private String label;
    private CmProgramType type;

    protected ProgramTypeModel() {
    }

    public ProgramTypeModel(String label, CmProgramType type) {
        this.label = label;
        this.type = type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setType(CmProgramType type) {
        this.type = type;
    }

    public CmProgramType getType() {
        return this.type;
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
