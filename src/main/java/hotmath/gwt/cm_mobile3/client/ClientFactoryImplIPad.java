package hotmath.gwt.cm_mobile3.client;

import com.allen_sauer.gwt.log.client.Log;

public class ClientFactoryImplIPad extends ClientFactoryImplBase  {
	public ClientFactoryImplIPad() {
		Log.info("ClientFactoryImplPad loading");
		this.pagesContainer = new PagesContainerPanelImplIPad();
	}
}
