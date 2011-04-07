package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/** Represents a single QA category
 * 
 * @author casey
 *
 */
public class CategoryModel implements Response {
    
    String category;
    
    public CategoryModel() {}
    
    public CategoryModel(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "CategoryModel [category=" + category + "]";
    }
}
