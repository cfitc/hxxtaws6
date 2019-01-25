package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.util.GyPublicUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.sdk.local.SDK;

public class WfBizPMDeleteUtil extends InterruptListener {

	public WfBizPMDeleteUtil() {
		super.setDescription("流程删除前时，同时删除项目流程统计信息中对应数据");
		super.setVersion("V1.0");
	}

	@Override
	public boolean execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		// 获取流程定义ID
		String processDefID = ctx.getProcessDef().id;
		// 当前的BO表名
		String boName = SDK.getRepositoryAPI()
				.getProcessDefinition(processDefID).getProcessVars()
				.get("boName").getDefaultValue();

		return GyPublicUtil.getInstance().deleteTjxxdata(processDefID,
				processInstId, boName);
	}

}
