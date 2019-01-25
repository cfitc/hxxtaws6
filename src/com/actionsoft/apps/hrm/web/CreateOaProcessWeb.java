package com.actionsoft.apps.hrm.web;


import com.actionsoft.bpms.bpmn.constant.UserTaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ProcessAPI;

public class CreateOaProcessWeb extends ActionWeb{
	public CreateOaProcessWeb(UserContext me) {
		super(me);
	}
	@SuppressWarnings("deprecation")
	/**
	 * 启动流程
	 * @param xmid
	 * @param xmmc
	 * @param processDefId
	 * @param porcessName
	 * @return
	 */
	public String createProcess(String processDefId,String porcessName){
		UserContext uc = this.getContext();
		String uid = uc.getUID();
		ProcessAPI processApi = SDK.getProcessAPI();
		//创建流程实例
		String title = uc.getUserName()+"发起的"+porcessName;
		ProcessInstance processInst = processApi.createProcessInstance(processDefId,uid, title);
		//流程实例ID
		String bindid = processInst.getId();
		//启动流程实例
		processApi.start(processInst);
		//根据流程实例ID获取对应的任务实例ID
		String taskInstId = DBSql.getString("select id from wfc_task where PROCESSINSTID='"+bindid+"'","id");
		return SDK.getFormAPI().getFormPage(uc, bindid, taskInstId, UserTaskRuntimeConst.STATE_TYPE_TRANSACT, 1, "", "");
	}
}
