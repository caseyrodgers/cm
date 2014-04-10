package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_tools.client.ui.MyValidatorDef.Verifier;

public class IntegerVerifier implements Verifier {

	@Override
	public boolean verify(String value) {
		try {
			Integer.parseInt(value);
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
