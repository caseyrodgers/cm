package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.FileUtil;
import hotmath.cm.util.export.ExportStudentsInExcelFormat;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.testset.ha.HaAdmin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExportStudentsCommand implements ActionHandler<ExportStudentsAction,CmWebResource>{

	private static Log LOG = LogFactory.getLog(ExportStudentsCommand.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public CmWebResource execute(Connection conn, ExportStudentsAction action) throws Exception {

    	List<StudentModelExt> studentList = new GetStudentGridPageCommand().getStudentPool(conn, action.getPageAction());

    	HaAdmin haAdmin = CmAdminDao.getInstance().getAdmin(action.getPageAction().getAdminId());
    	ByteArrayOutputStream baos = null;

    	try {
    		ExportStudentsInExcelFormat exporter =
    			new ExportStudentsInExcelFormat(studentList);
    		baos = exporter.export();

            // write to temporary file to be cleaned up later
            String outputBase = CmWebResourceManager.getInstance().getFileBase();

            // if outputBase/adminId directory doesn't exist, create it
            String unique = Long.toString(System.currentTimeMillis());
            outputBase = outputBase + "/" + action.getAdminId();
            String outputDir = FileUtil.ensureOutputDir(outputBase, unique);

            String todaysDate = sdf.format(new Date());
			File filePath = new File(outputDir, "CM-" + haAdmin.getUserName().toUpperCase() + "-" + todaysDate + ".xls");
			LOG.info("Writing XLS output: " + filePath);
			FileOutputStream fos = null;
			try {
			    fos = new FileOutputStream(filePath);
				baos.writeTo(fos);

				return new CmWebResource(filePath.getPath(), CmWebResourceManager.getInstance().getFileBase(), CmWebResourceManager.getInstance().getWebBase());
			}
			finally {
			    if (fos != null) fos.close();
			}
    	}
    	catch (Exception e) {
    		LOG.error(String.format("*** Error exporting Excel file: adminId: %d, studentList.size(): %d",
    				action.getAdminId(), studentList.size()), e);
    	}
    	return null;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return ExportStudentsAction.class;
    }
}
