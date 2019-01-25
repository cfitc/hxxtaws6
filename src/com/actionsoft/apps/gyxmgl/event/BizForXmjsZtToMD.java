package com.actionsoft.apps.gyxmgl.event;

import java.util.List;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.webServices.ProjectInfoSynchroClient;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;

public class BizForXmjsZtToMD extends ExecuteListener{
	
	public BizForXmjsZtToMD() {
		super.setDescription("固有项目归档：起草者办理后，更新项目主数据【固有项目信息表】项目状态为“归档中”");
		super.setVersion("V1.0");
	}
	
	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		String bindId = ctx.getProcessInstance().getId();
		List<RowMap> list=DBSql.getMaps("select * from BO_ACT_PM_GYXMJSB where BINDID=?", bindId);
		for(RowMap m : list){
			String XMID=m.getString("XMID");
			DBSql.update("update BO_ACT_PM_GYXMXXB set XMZT='项目归档中' where XMID='" + XMID +"'");
			//更新到5平台
			String bindId_xmxx=DBSql.getString("select bindid from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + XMID + "'");
			ProjectInfoSynchroClient.xmxxToPmsFive(bindId_xmxx);
		}
	}
}
