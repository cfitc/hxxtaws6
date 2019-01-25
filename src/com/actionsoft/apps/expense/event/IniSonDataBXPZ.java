package com.actionsoft.apps.expense.event;
import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;

/**
 * 初始化报销凭证子记录
 * @author CHENDX
 */
public class IniSonDataBXPZ extends ExecuteListener implements ExecuteListenerInterface {
	
	public IniSonDataBXPZ(){
		super.setDescription("表单加载前，初始化子表记录行数。目前最多只允许填写4个摘要");
		super.setVersion("1.0");
	}
	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		String uid=ctx.getUserContext().getUID();
		int num=DBSql.getInt("select count(id) as num  from "+GlobleParams.BO_ACT_BXPD_APPLY_S_TABLE+" where bindid='"+processInstId+"'", "num");
		if(num<4)
		{
			for(int j=num;j<4;j++)
			{
				BO SonData=new BO();
				SonData.set("ZY","");
				SonData.set("JE",0);
				SonData.set("JK",0);
				SonData.set("SE",0);
				SonData.set("SM","");
				try {
					SDK.getBOAPI().create(GlobleParams.BO_ACT_BXPD_APPLY_S_TABLE, SonData, processInstId,uid);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}

