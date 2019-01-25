package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.webServices.ProjectInfoSynchroClient;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;

/**
 * 机构管理保存后同步到五平台
 * @author Administrator
 *
 */
public class BizForJgxxSaveEvent extends ExecuteListener{

	@Override
	public void execute(ProcessExecutionContext arg0) throws Exception {
		String bindid=arg0.getProcessInstance().getId();
		ProjectInfoSynchroClient.JgToPmsFive(bindid);
	}

}
