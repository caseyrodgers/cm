package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.model.StudentModelI;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;

public class CmTemplateFormaters {
    public static NullChecker getFormat() {
        return new NullChecker();
    }
    
    @FormatterFactories(@FormatterFactory(factory = CmTemplateFormaters.class, name = "nullChecker"))
    interface StudentDetailsTemplate extends XTemplates {
        @XTemplate(source = "StudentDetailsPanel_DetailInfo.html")
        SafeHtml render(StudentModelI adminInfo);
    }

}