package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class HighlightReportLayout implements Response {

	private static final long serialVersionUID = 6468133897574809056L;

	public HighlightReportLayout() {}

    String title;
    String countLabel;
    String columnLabels[];
    String[][] columnValues = null;

    public HighlightReportLayout(String columnLabels[]) {
        this.columnLabels = columnLabels;
    }

    public HighlightReportLayout(String columnLabels[], String[][] columnValues) {
        this.columnLabels = columnLabels;
        this.columnValues = columnValues;
    }

    public HighlightReportLayout(String title, String countLabel, String columnLabels[], String[][] columnValues) {
    	this.title = title;
    	this.countLabel = countLabel;
        this.columnLabels = columnLabels;
        this.columnValues = columnValues;
    }

    public String[][] getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(String[][] columnValues) {
        this.columnValues = columnValues;
    }

    public String[] getColumnLabels() {
        return columnLabels;
    }

    public void setColumnLabels(String[] columnLabels) {
        this.columnLabels = columnLabels;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCountLabel() {
		return countLabel;
	}

	public void setCountLabel(String countLabel) {
		this.countLabel = countLabel;
	}
}
