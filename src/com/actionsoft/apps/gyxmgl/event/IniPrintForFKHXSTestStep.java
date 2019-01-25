package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;

public class IniPrintForFKHXSTestStep extends ExecuteListener {
	
	public IniPrintForFKHXSTestStep(){
		super.setDescription("不显示节点名称为：'风控会委员表决'和' 投委会委员表决' 的审批意见。");
		super.setVersion("V1.0");
	}
	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// TODO Auto-generated method stub
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		//获取流程定义ID
		String processDefID = ctx.getProcessDef().id;
		//获取任务ID
		String taskID = ctx.getTaskInstance().getId();
		//用户ID
		String userID = ctx.getUserContext().getUID();
		//当前的BO表名
		String boName = ctx.getParameterOfString(ListenerConst.FORM_EVENT_PARAM_BONAME);
		//当前步骤ID
		//String stepID =  ctx.getTaskInstance().
	}

}
