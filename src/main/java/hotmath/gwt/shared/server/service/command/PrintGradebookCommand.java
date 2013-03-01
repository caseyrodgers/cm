package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.FileUtil;
import hotmath.cm.util.report.GroupGradebookReport;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.PrintGradebookAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.CmWebResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;

public class PrintGradebookCommand implements ActionHandler<PrintGradebookAction,CmWebResource>{

    @Override
    public CmWebResource execute(Connection conn, PrintGradebookAction action) throws Exception {
        ByteArrayOutputStream baos = new GroupGradebookReport("Grade Book Report").makePdf(action.getAid(), action.getGroupId());
        
        // write to temporary file to be cleaned up later
        String outputBase = CmWebResourceManager.getInstance().getFileBase();

        // if outputBase/adminId directory doesn't exist, create it

        String unique = Long.toString(System.currentTimeMillis());

        outputBase = outputBase + "/" + action.getAid();
        String outputDir = FileUtil.ensureOutputDir(outputBase, unique);
        File filePath = new File(outputDir, "gradebook_" + action.getGroupId() + ".pdf");
        FileOutputStream fw = null;
        try {
            fw = new FileOutputStream(filePath);
            baos.writeTo(fw);
            return new CmWebResource(filePath.getPath(), CmWebResourceManager.getInstance().getFileBase(), CmWebResourceManager.getInstance().getWebBase());
        }
        finally {
            if (fw != null) fw.close();
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return PrintGradebookAction.class;
    }
}
