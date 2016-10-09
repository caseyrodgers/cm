package hotmath.cm.server.model;

public class CmPurchase {

	private String purchase;
	boolean freeProgram;

	public CmPurchase(String purchase, boolean freeProgram) {
		this.purchase = purchase;
		this.freeProgram = freeProgram;
	}
	
	public String getPurchase() {
		return purchase;
	}
	
	public boolean isFreeProgram() {
		return freeProgram;
	}
	
	public void setFreeProgram(boolean freeProgram) {
		this.freeProgram = freeProgram;
	}
}
