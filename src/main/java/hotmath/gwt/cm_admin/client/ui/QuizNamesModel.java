package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.model.CustomQuizDef;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class QuizNamesModel extends BaseModelData {
    
    CustomQuizDef customDef;
    
    public QuizNamesModel(){}
    
    public QuizNamesModel(CustomQuizDef def) {
        this.customDef = def;
        set("quizName", def.getQuizName());
    }
    
    public String getQuizName() {
        return get("quizName");
    }
}
