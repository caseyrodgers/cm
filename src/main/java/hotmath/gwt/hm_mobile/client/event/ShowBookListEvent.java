package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.hm_mobile.client.model.CategoryModel;

import com.google.gwt.event.shared.GwtEvent;


public class ShowBookListEvent extends GwtEvent<ShowBookListEventHandler> {
    CategoryModel category;
    
	public static Type<ShowBookListEventHandler> TYPE = new Type<ShowBookListEventHandler>();

	public ShowBookListEvent(CategoryModel category) {
	    this.category = category;
	}

	@Override
	public Type<ShowBookListEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowBookListEventHandler handler) {
		handler.showBookList(category);
	}

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }
	
}
