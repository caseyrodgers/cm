package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.model.StudentModelI;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;



public interface StudentGridProperties extends PropertyAccess<String> {
    @Path("uid")
    ModelKeyProvider<StudentModelI> id();

    ValueProvider<StudentModelI, String> name();

    ValueProvider<StudentModelI, String> passcode();

    ValueProvider<StudentModelI, String> group();

    @Path("program.programDescription")
    ValueProvider<StudentModelI, String> programDescription();

    ValueProvider<StudentModelI, String> status();

    @Path("status")
    ValueProvider<StudentModelI, String> quizzes();

    ValueProvider<StudentModelI, Integer> passingCount();

    ValueProvider<StudentModelI, String> lastQuiz();

    ValueProvider<StudentModelI, String> lastLogin();
}
