package com.actionsoft.apps.aws52.worklist;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.EAITaskInstance;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;

public class CreateTaskASLP implements ASLP {

	public CreateTaskASLP() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ResponseObject call(Map<String, Object> params) {
		if (params == null) {
			return ResponseObject.newErrResponse().err("不接受参数为空的调用!");
		}
		if (!params.containsKey("taskId")) {
			return ResponseObject.newErrResponse().err("taskId参数不允许为空!");
		}
		if (!params.containsKey("owner")) {
			return ResponseObject.newErrResponse().err("owner参数不允许为空!");
		}
		if (!params.containsKey("target")) {
			return ResponseObject.newErrResponse().err("target参数不允许为空!");
		}
		if (!params.containsKey("title")) {
			return ResponseObject.newErrResponse().err("title参数不允许为空!");
		}
		if (!params.containsKey("priority")) {
			return ResponseObject.newErrResponse().err("priority参数不允许为空!");
		}
		if (!params.containsKey("processInstId")) {
			return ResponseObject.newErrResponse().err("processInstId参数不允许为空!");
		}
		if (!params.containsKey("processInstName")) {
			return ResponseObject.newErrResponse().err("processInstName参数不允许为空!");
		}
		if (!params.containsKey("state")) {
			return ResponseObject.newErrResponse().err("state参数不允许为空!");
		}
		if (!params.containsKey("processGroupName")) {
			return ResponseObject.newErrResponse().err("processGroupName参数不允许为空!");
		}
		if (!params.containsKey("ownerDepartmentId")) {
			return ResponseObject.newErrResponse().err("ownerDepartmentId参数不允许为空!");
		}
		String outerId = (String) params.get("taskId");
		String createUid = (String) params.get("owner");
		String targetUid = (String) params.get("target");
		String title = (String) params.get("title");
		String priority = (String) params.get("priority");
		// complex data
		String processInstId = (String) params.get("processInstId");
		String processInstName = (String) params.get("processInstName");
		String processGroupName = (String) params.get("processGroupName");
		String ownerDepartmentId = (String) params.get("ownerDepartmentId");
		/** 转换5平台的流程分类 到6平台 用于6平台首页的业务事项和日常事项  --start **/
		String groupName = (String)params.get("groupName");
		String ywsxGroup = "固有项目管理,其他业务管理,信托项目管理,EAST信息报送";
	    String IOBD_ID = "";
	    if(ywsxGroup.contains(groupName)){
	    	IOBD_ID = "22cd4a24-47f1-46f0-b002-765ee1e34d0c";
	    }else{
	    	IOBD_ID = "bd0d6ae7-cd49-4801-9be3-996c04df7ccb";
	    }
	    /** 转换5平台的流程分类 到6平台 用于6平台首页的业务事项和日常事项 --end **/
		String state = (String) params.get("state");
		String processDefId = (String) params.get("processDefId");
		JSONObject json = new JSONObject();
		json.put("processInstId", processInstId);
		json.put("processInstName", processInstName);
		json.put("processGroupName", processGroupName);
		json.put("ownerDepartmentId", ownerDepartmentId);
		// modify by dub 传递的ext2中的json字符串中添加taskId
		json.put("taskId", outerId);
		json.put("state", state);
		json.put("processDefId", processDefId);//
		System.err.println(">>>>>processDefId:"+processDefId);
	//	EAITaskInstance eaiTaskInstance = WorklistCollect.getInstance().createTask(outerId, createUid, targetUid, title, json.toString(), Integer.parseInt(priority));
		EAITaskInstance eaiTaskInstance = SDK.getTaskAPI().createEAITaskInstance("com.actionsoft.apps.aws52", outerId, createUid, targetUid, title, json.toString(), Integer.parseInt(priority));
		DBSql.update("update wfc_task set iobd = '"+IOBD_ID+"' where id = '"+eaiTaskInstance.getId()+"'");
		if (eaiTaskInstance == null) {
			return ResponseObject.newErrResponse().err("创建失败!");
		} else {
			// 修改6平台的数据库中的EAI任务状态
			Connection conn = null;
			conn = DBSql.open();
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("state", state);
			paraMap.put("ownerDepartmentId", ownerDepartmentId);
			paraMap.put("id", eaiTaskInstance.getId());
			try {
				String sql = "update WFC_TASK set TASKSTATE=:state,OWNERDEPTID=:ownerDepartmentId where ID=:id";
				DBSql.update(conn, sql, paraMap);
			} catch (Exception e) {
			} finally {
				DBSql.close(conn);
			}

			return ResponseObject.newOkResponse().put("id", eaiTaskInstance.getId());
		}
	}

}
