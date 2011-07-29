<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher"%><%@ page import="hotmath.util.*" %><%String typeData = request.getParameter("data");
    if(typeData == null)
        throw new Exception("'data' must be specified");

    long dataValue = -1;
    if(typeData.equals("actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.ActionsExecuted);
    }
    else if(typeData.equals("actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.ActionsCompleted);
    }
    else if(typeData.equals("actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.ProcessingTime);
    }

    else if(typeData.equals("student.actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.StudentProcessingTime);
    }
    if(typeData.equals("student.actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.StudentActionsExecuted);
    }
    else if(typeData.equals("student.actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.StudentActionsCompleted);
    }


    if(typeData.equals("admin.actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.AdminActionsExcecuted);
    }
    else if(typeData.equals("admin.actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.AdminActionsCompleted);
    }
    else if(typeData.equals("admin.actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.AdminProcessingTime);
    }


    else if(typeData.equals("any.actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.AnyActionsExecuted);
    }
    else if(typeData.equals("any.actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.AnyActionsCompleted);
    }
    else if(typeData.equals("any.actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.AnyProcessingTime);
    }

    else if(typeData.equals("admin.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.AdminActionsException);
    }
    else if(typeData.equals("student.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.StudentActionsException);
    }
    else if(typeData.equals("any.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.AnyActionsException);
    }
    else if(typeData.equals("other.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.OtherActionsException);
    }
    else if(typeData.equals("actions.errors")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.ExceptionCount);
    }

    else if(typeData.equals("db.connections")) {
        dataValue = HMConnectionPool.getInstance().getConnectionCount();
    }
    else if(typeData.equals("db.errors")) {
        dataValue = HMConnectionPool.getInstance().getErrorCount();
    }
    else if(typeData.equals("server.memory.free")) {
        dataValue = Runtime.getRuntime().freeMemory();
    }
    else if(typeData.equals("server.memory.total")) {
        dataValue = Runtime.getRuntime().totalMemory();
    }
    else if(typeData.equals("server.memory.used")) {
        dataValue = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
    else if(typeData.equals("cpool.connection.create.time")) {
        dataValue = HMConnectionPool.getInstance().getConnectionCreateTime();
    }
    
    
    else if(typeData.equals("hm_mobile.actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.HmMobileActionsExecuted);
    }
    else if(typeData.equals("hm_mobile.actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.HmMobileActionsCompleted);
    }
    else if(typeData.equals("hm_mobile.actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.HmMobileProcessingTime);
    }
    else if(typeData.equals("hm_mobile.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorDataPoints.HmMobileActionsException);
    }%><%= dataValue %>
