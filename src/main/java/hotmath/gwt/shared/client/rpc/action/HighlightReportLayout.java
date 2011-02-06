package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class HighlightReportLayout implements Response {
    
    public HighlightReportLayout() {}
    
    String columnLabels[];
    String[][] columnValues = null;

    public HighlightReportLayout(String columnLabels[]) {
        this.columnLabels = columnLabels;
    }

    public HighlightReportLayout(String columnLabels[], String[][] columnValues) {
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
}
