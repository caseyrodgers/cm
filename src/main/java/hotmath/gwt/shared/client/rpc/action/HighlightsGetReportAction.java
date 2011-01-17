package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

import java.util.Date;

public class HighlightsGetReportAction implements Action<CmList<HighlightReportData>> {
    
    ReportType type;
    Integer adminId;
    Date from;
    Date to;


    public HighlightsGetReportAction() {
    }

    public HighlightsGetReportAction(ReportType type, Integer adminId, Date from, Date to) {
        this.type = type;
        this.adminId = adminId;
        this.from = from;
        this.to = to;
    }

    
    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }


    @Override
    public String toString() {
        return "HighlightsGetReportAction [type=" + type + ", adminId=" + adminId + ", from=" + from + ", to=" + to
                + "]";
    }


    public enum ReportType{GREATEST_EFFORT};
}
