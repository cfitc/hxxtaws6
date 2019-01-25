package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;


/**
 * 
 * @author Administrator
 *
 */
public class BizForXzdzywSaveData extends ExecuteListener{

	public BizForXzdzywSaveData(){
		super.setDescription("新增抵质押流程，填写子表信息的时候，初始化合同ID与合同编号等信息。");
		super.setVersion("1.0");
	}
	
	/**
	 * 条件:
	 * 表单、子表保存后被触发
	 */
	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception{
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		// 获取流程定义ID
		String processDefId = ctx.getProcessDef().id;
		//BO名称
		String subBOTable = SDK.getRepositoryAPI().getProcessDefinition(processDefId).getProcessVars().get("boName").getDefaultValue();
		if("BO_ACT_PM_GYYYHTZB".equals(subBOTable)){
			//运用合同信息
			DBSql.update("update BO_ACT_PM_GYYYHTZB set HTID='" + processInstId + "' where BINDID='" + processInstId +"'");
		} else if("BO_ACT_PM_TRZXMFFCB".equals(subBOTable)){
			//投融资项目信息（非房产类）
			DBSql.update("update BO_ACT_PM_TRZXMFFCB set TRZXMID='"+processInstId+ "' where BINDID='" + processInstId +"'");
		}  else if("BO_ACT_TRZXMFCB".equals(subBOTable)){
			//投融资项目信息（房产类）
			DBSql.update("update BO_ACT_TRZXMFCB set TRZXMID='"+processInstId+ "' where BINDID='" + processInstId +"'");
		} else if("BO_ACT_PM_GYDYWXXB".equals(subBOTable)){
			//抵押物信息
			DBSql.update("update BO_ACT_PM_GYDYWXXB set HTID='" + processInstId + "' where BINDID='" + processInstId +"'");
		} else if("BO_ACT_PM_GYZYWXXB".equals(subBOTable)){
			//质押物信息
			DBSql.update("update BO_ACT_PM_GYZYWXXB set HTID='" + processInstId + "' where BINDID='" + processInstId +"'");
		} else if("BO_ACT_PM_GYDBHTXXB".equals(subBOTable)){
			//担保合同信息
			DBSql.update("update BO_ACT_PM_GYDBHTXXB set HTID='" + processInstId + "' where BINDID='" + processInstId +"'");
		} 
	}

}
