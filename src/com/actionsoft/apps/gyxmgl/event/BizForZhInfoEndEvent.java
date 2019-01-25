package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.webServices.ProjectInfoSynchroClient;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
/**
 * 账户信息保存后向五平台同步
 * @author Administrator
 *
 */
public class BizForZhInfoEndEvent extends ExecuteListener{

	@Override
	public void execute(ProcessExecutionContext arg0) throws Exception {
		String bindid= arg0.getProcessInstance().getId();
		ProjectInfoSynchroClient.ZhToPmsFive(bindid);
	}

}
