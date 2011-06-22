package hotmath.gwt.hm_mobile.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class CategoryModel implements Response {
	
	String category;
	
	public CategoryModel() {}
	public CategoryModel(String cat) {
		this.category = cat;
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
