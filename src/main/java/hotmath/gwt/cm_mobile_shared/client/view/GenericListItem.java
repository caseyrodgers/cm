package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;

public class GenericListItem extends GenericTextTag<String> {
    public GenericListItem(String htmlToDisplay) {
        super("li");
        setHtml(htmlToDisplay);
    }
}