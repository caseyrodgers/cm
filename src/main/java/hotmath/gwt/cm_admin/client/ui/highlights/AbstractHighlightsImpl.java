package hotmath.gwt.cm_admin.client.ui.highlights;

import com.google.gwt.user.client.ui.Widget;

abstract class AbstractHighlightsImpl  {
    String name;
    Widget _widget;
    protected HIGHLIGHT_TYPE type;
    
    public static enum HIGHLIGHT_TYPE {
    	GREATEST_EFFORT, LEAST_EFFORT, MOST_QUIZZES_PASSED, MOST_QUIZZES_FAILED, MOST_GAMES, HIGHEST_QUIZ_AVERAGE,
    	SCHOOL_COMPARE, NATIONWIDE_COMPARE
    }
    AbstractHighlightsImpl(String name) {
        this.name = name;
    }
    
    public String getText() {
        return this.name;
    }
    
    public Widget getWidget() {
        if(_widget == null)
            _widget = prepareWidget();
        return _widget;
    }
    
    public HIGHLIGHT_TYPE getType() {
    	return type;
    }

    abstract Widget prepareWidget();
}
