package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.ui.CmTemplateFormaters;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;


@FormatterFactories(@FormatterFactory(factory = CmTemplateFormaters.class, name = "nullChecker"))
public interface InfoLoaderTemplate extends XTemplates {
    @XTemplate(source="AccountInfoPanel_InfoLoader.html")
    SafeHtml renderAdminInfo(AccountInfoModel adminInfo);
}

