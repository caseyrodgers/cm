package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** An EXT model for representing a single item 
 * in a custom program.
 * 
 * a 'item' can be either a 'lesson' or a 'custom quiz'
 * 
 * @author casey
 *
 *
 * NOTE USED ... DELETE
 * 
 */
public class CustomProgramItemModel extends BaseModel implements Response {
    
    public CustomProgramItemModel(){
        /** empty */
    }
    
    public CustomProgramItemModel(String customProgramItem) {
        setCustomProgramItem(customProgramItem);
    }

    public String getCustomProgramItem() {
        return get("customProgramItem");
    }

    public void setCustomProgramItem(String item) {
        set("customProgramItem", item);
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CustomProgramItemModel) {
            CustomProgramItemModel clm = (CustomProgramItemModel)obj;
            if(clm.getCustomProgramItem().equals(getCustomProgramItem()))
                return true;
            else
                return false;
        }
        else
            return super.equals(obj);
    }    
}
