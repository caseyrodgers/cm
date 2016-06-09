package hotmath.cm.server.model;

public class CmPurchase {

	private String purchase;
	boolean first;  // first one/free

	public CmPurchase(String purchase) {
		this.purchase = purchase;
	}
	
	public String getPurchase() {
		return purchase;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}
	
	public boolean isFirst() {
		return first;
	}
}
