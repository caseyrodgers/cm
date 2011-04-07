package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.CategoryModel;

public class GetQaCategoriesAction implements Action<CmList<CategoryModel>> {
    
    String category;
    
    public GetQaCategoriesAction() {}
    
    public GetQaCategoriesAction(String category) {
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
        return "GetQaCategoriesAction [category=" + category + "]";
    }
}
