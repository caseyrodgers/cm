package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.FileUtil;
import hotmath.cm.util.export.ExportStudentsInExcelFormat;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

public class ExportStudentsCommand implements ActionHandler<ExportStudentsAction,CmWebResource>{

	private static Log LOG = LogFactory.getLog(ExportStudentsCommand.class);

    @Override
    public CmWebResource execute(Connection conn, ExportStudentsAction action) throws Exception {

    	List<StudentModelExt> studentList = new GetStudentGridPageCommand().getStudentPool(conn, action.getPageAction());

    	ByteArrayOutputStream baos = null;

    	try {
    		ExportStudentsInExcelFormat exporter =
    			new ExportStudentsInExcelFormat(studentList, action.getPageAction().getAdminId());
    		baos = exporter.export();

            // write to temporary file to be cleaned up later
            String outputBase = CmWebResourceManager.getInstance().getFileBase();

            // if outputBase/adminId directory doesn't exist, create it
            String unique = Long.toString(System.currentTimeMillis());
            outputBase = outputBase + "/" + action.getAdminId();
            String outputDir = FileUtil.ensureOutputDir(outputBase, unique);

			 File filePath = new File(outputDir, "studentExportFor-" + action.getAdminId() + ".xls");
			 LOG.info("Writing XLS output: " + filePath);
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
    	catch (Exception e) {
    		LOG.error(String.format("*** Error exporting Excel file: adminId: %d, studentList.size(): %d",
    				action.getAdminId(), studentList.size()));
    	}
    	return null;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ExportStudentsAction.class;
    }
}
