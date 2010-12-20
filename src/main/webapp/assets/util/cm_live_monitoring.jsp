<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher.MonitorData"%><%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher"%><%@ page import="hotmath.util.*" %><%

    String typeData = request.getParameter("data");
    if(typeData == null)
        throw new Exception("'data' must be specified");

    long dataValue = -1;
    if(typeData.equals("actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.ActionsExecuted);
    }
    else if(typeData.equals("actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.ActionsCompleted);
    }
    else if(typeData.equals("actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.ProcessingTime);
    }

    else if(typeData.equals("student.actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.StudentProcessingTime);
    }
    if(typeData.equals("student.actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.StudentActionsExecuted);
    }
    else if(typeData.equals("student.actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.StudentActionsCompleted);
    }


    if(typeData.equals("admin.actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.AdminActionsExcecuted);
    }
    else if(typeData.equals("admin.actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.AdminActionsCompleted);
    }
    else if(typeData.equals("admin.actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.AdminProcessingTime);
    }


    else if(typeData.equals("any.actions.executed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.AnyActionsExecuted);
    }
    else if(typeData.equals("any.actions.completed")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.AnyActionsCompleted);
    }
    else if(typeData.equals("any.actions.time")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.AnyProcessingTime);
    }

    else if(typeData.equals("admin.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.AdminActionsException);
    }
    else if(typeData.equals("student.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.StudentActionsException);
    }
    else if(typeData.equals("any.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.AnyActionsException);
    }
    else if(typeData.equals("other.actions.exception")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.OtherActionsException);
    }
    else if(typeData.equals("actions.errors")) {
        dataValue = ActionDispatcher.getInstance().getMonitoredData(ActionDispatcher.MonitorData.ExceptionCount);
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
%><%= dataValue %>
