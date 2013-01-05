package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.model.StudentModelI;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;



public interface StudentGridProperties extends PropertyAccess<String> {
    @Path("uid")
    ModelKeyProvider<StudentModelI> id();

    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, String> name();

    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, String> passcode();

    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, String> group();

    @Path("program.programDescription")
    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, String> programDescription();

    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, String> status();

    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, String> quizzes();

    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, Integer> passingCount();

    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, String> lastQuiz();

    ValueProvider<hotmath.gwt.cm_tools.client.model.StudentModelI, String> lastLogin();
}
