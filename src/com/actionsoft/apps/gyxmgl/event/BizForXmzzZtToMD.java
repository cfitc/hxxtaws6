package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.webServices.ProjectInfoSynchroClient;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 表单办理前校验
 * @author chendx
 * com.actionsoft.apps.gyxmgl.event.BizForXmzzZtToMD
 */
public class BizForXmzzZtToMD extends InterruptListener  {
	
	public BizForXmzzZtToMD(){
		super.setDescription("固有项目中止审批，起草者办理后更新至主数据[固有项目信息表]中的项目状态");
		super.setVersion("1.0");
	}
	@Override
	public boolean execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		BO boData = SDK.getBOAPI().query(GlobleParams.BO_ACT_PM_GYXMXMZZB_TABLE).detailByBindId(processInstId);
		if(boData != null){
			String xmid = boData.getString("XMID");
			if(xmid != "" && !"".equals(xmid)){
				DBSql.update("update " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " set xmzt = '"+GlobleParams.gyxmzzStatusIng + "' where xmid = '"+xmid+"'");
				//更新到5平台
				String bindId=DBSql.getString("select bindid from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'");
				ProjectInfoSynchroClient.xmxxToPmsFive(bindId);
			}
		}
		return true;
	}

}
