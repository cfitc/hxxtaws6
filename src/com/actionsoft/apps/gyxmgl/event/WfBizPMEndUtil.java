package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.util.GyPublicUtil;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.sdk.local.SDK;

/**
 * 流程结束后，修改项目流程统计信息
 * @author Administrator
 * com.actionsoft.apps.gyxmgl.event.WfBizPMEndUtil
 */
public class WfBizPMEndUtil extends ExecuteListener {

	public WfBizPMEndUtil() {
		super.setDescription("流程结束后，修改项目流程统计信息");
		super.setVersion("1.0");
	}

	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		// 获取流程定义ID
		String processDefID = ctx.getProcessDef().id;
		// 当前的BO表名
		String boName = SDK.getRepositoryAPI()
				.getProcessDefinition(processDefID).getProcessVars()
				.get("boName").getDefaultValue();
		GyPublicUtil.getInstance().getPMEndUtil(processDefID, processInstId, boName);
	}

}
