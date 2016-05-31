package hotmath.cm.server.model;

import java.util.ArrayList;
import java.util.List;

public class CmPurchases {
	private List<CmPurchase> purchases = new ArrayList<CmPurchase>();
	public CmPurchases(List<CmPurchase> res) {
		purchases.addAll(res);
	}
	public List<CmPurchase> getPurchases() {
		return purchases;
	}
}
