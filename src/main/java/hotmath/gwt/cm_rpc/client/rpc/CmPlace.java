package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;


public enum CmPlace implements Response {
    QUIZ,
    PRESCRIPTION,
    END_OF_PROGRAM,
    AUTO_ADVANCED_PROGRAM,
    AUTO_PLACEMENT,
    WELCOME,
    ERROR_FLASH_REQUIRED, 
    ASSIGNMENTS_ONLY,
    NO_PROGRAM_ASSIGNED, AUTO_CREATE
}
