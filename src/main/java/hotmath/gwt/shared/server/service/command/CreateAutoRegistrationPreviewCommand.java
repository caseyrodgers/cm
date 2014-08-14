package hotmath.gwt.shared.server.service.command;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.cm.util.service.BulkRegLoader;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationPreviewAction;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.List;

/** Create a preview set of Student Entries that could be 
 * saved 'right now'.  No guarantee if after delay a validation
 * error might occur.
 * 
 * 
 * NOTE: Reads cached BulkRegLoader that should be been populated by MULTIPART file upload
 * store in CmCacheManger.BULK_UPLOAD_FILE under the 'key'.
 * 
 * @author casey
 *
 */
public class CreateAutoRegistrationPreviewCommand implements ActionHandler<CreateAutoRegistrationPreviewAction, AutoRegistrationSetup> {

	private static final Logger LOGGER = Logger.getLogger(CreateAutoRegistrationPreviewCommand.class);

    @Override
    public AutoRegistrationSetup execute(Connection conn, CreateAutoRegistrationPreviewAction action) throws Exception {

        StudentModelI studentTemplate = action.getStudentTemplate();
        AutoRegistrationSetup preview = new AutoRegistrationSetup();

        CmStudentDao dao = CmStudentDao.getInstance();

        BulkRegLoader bulkLoader = (BulkRegLoader)CmCacheManager.getInstance().retrieveFromCache(CacheName.BULK_UPLOAD_FILE, action.getUploadKey());
        
        LOGGER.debug("+++ execute(): bulkLoader: " + ((bulkLoader != null)?"not null":"NULL"));

        if(bulkLoader == null)
            throw new CmRpcException("Upload file was not found, perhaps timed out");
        
        List<AutoRegistrationEntry> entries = bulkLoader.getEntries();
        
        /** Create a series of Student records using student model as source for template values
         * 
         */
        boolean haveErrors = false;
        int dummyUid = -1;
        for(AutoRegistrationEntry entry: entries) {
            
            if( dao.checkForDuplicatePasscode(conn, studentTemplate.getAdminUid(), dummyUid, entry.getPassword())) {
                entry.setIsError(true);
                entry.setMessage("Password is already in use");
                haveErrors = true;
            }
            preview.getEntries().add(entry);
        }

        LOGGER.debug("+++ execute(): haveErrors: " + haveErrors + ", preview.getEntries().size(): " + preview.getEntries().size(), new Exception());

        return preview;
    }
    

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return CreateAutoRegistrationPreviewAction.class;
    }
}
