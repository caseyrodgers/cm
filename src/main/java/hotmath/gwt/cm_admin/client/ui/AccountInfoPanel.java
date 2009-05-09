package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;

import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class AccountInfoPanel extends LayoutContainer {
	
	private XTemplate template;
	private HTML html;
	private AccountInfoModel model;
	
	public AccountInfoPanel() {
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(10);
				
		StringBuilder sb = new StringBuilder();  
		sb.append("<div class=text style='line-height: 13px; font-size: 11px'>");  
		sb.append("<b>School:</b> {school-name}");
		sb.append("</div>");
        template = XTemplate.create(sb.toString());  
		html = new HTML();
		html.setHTML(sb.toString());
		html.setWidth("200px");
		hp.add(html);
		
		//TODO: add "Details" button or tool tip

		add(hp);
	}
	
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		//TODO: obtain account info from DB
		AccountInfoModel model = new AccountInfoModel();
		model.setSchoolName("Hotmath High");

		setAccountInfoModel(model);
	}

	public void setAccountInfoModel(AccountInfoModel model) {
		this.model = model;
		template.overwrite(html.getElement(), Util.getJsObject(this.model));  
	}

}
