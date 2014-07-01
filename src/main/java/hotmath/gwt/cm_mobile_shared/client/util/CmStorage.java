package hotmath.gwt.cm_mobile_shared.client.util;

import com.allen_sauer.gwt.log.client.Log;
import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.core.client.JavaScriptException;

/** Simpley wrapper around Storage to catch security exception
 * 
 * @author casey
 *
 */
public class CmStorage {
    static public Storage getLocalStorage() {
        Storage storage=null;
        try {
            storage = Storage.getLocalStorage();
        }
        catch(JavaScriptException jse) {
            Log.warn("Could not get LocalStorage: " + jse);
        }
        return storage;
    }
}
