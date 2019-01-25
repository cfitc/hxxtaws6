package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.util.GyPublicUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;

/**
 * 节点办理完后事件
 * @author Administrator
 * com.actionsoft.apps.gyxmgl.event.WfBizPMStartUtil
 */
public class WfBizPMStartUtil extends ExecuteListener {
	
	public WfBizPMStartUtil(){
		super.setDescription("某流程(固有项目储备、项目立项、方案预审等)正式流转后，向项目流程统计数据表插入此流程数据信息");
		super.setVersion("1.0");
	}
	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		//获取流程定义ID
		String processDefId = ctx.getProcessDef().id;
		//用户ID
		UserContext uc = ctx.getUserContext();
		//当前的BO表名
		String boName = SDK.getRepositoryAPI().getProcessDefinition(processDefId).getProcessVars().get("boName").getDefaultValue();
		String boname_cs=ctx.getParameterOfString(ListenerConst.FORM_EVENT_PARAM_BONAME);
		
		
		GyPublicUtil.getInstance().getPMStartUtil(processDefId, boName, processInstId, uc, 1);
	}

}
