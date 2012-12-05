package hotmath.gwt.cm_rpc.client.rpc;

public class GetSolutionPidsAction implements Action<CmList<String>> {
    
    private String book;
    public GetSolutionPidsAction(){}
    public GetSolutionPidsAction(String book) {
        this.book = book;
    }
    public String getBook() {
        return book;
    }
    public void setBook(String book) {
        this.book = book;
    }
    @Override
    public String toString() {
        return "GetSolutionPids [book=" + book + "]";
    }
}
