package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.ui.SearchComboBoxPanel.Topic;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.XTemplates;

public interface SearchListViewTemplate extends XTemplates {
    // @XTemplate("<div class='{style.searchItem}'><h3><span>{post.date:date(\"M/d/yyyy\")}<br />by {post.author}</span>{post.title}</h3>{post.excerpt}</div>")
    @XTemplate("<div class='{style.searchItem}'><h3>{post.name}</span></h3>{post.excerpt}</div>")
    SafeHtml render(Topic post, SearchStyle style);
    
    public interface SearchBundle extends ClientBundle {
        @Source("SearchListViewTemplate.css")
        SearchStyle css();
    }

    public interface SearchStyle extends CssResource {
        String searchItem();
    }

}