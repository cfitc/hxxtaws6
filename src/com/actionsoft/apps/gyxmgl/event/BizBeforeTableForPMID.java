package com.actionsoft.apps.gyxmgl.event;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.util.GyPublicUtil;
import com.actionsoft.apps.gyxmgl.util.ObjectCopy;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.bpmn.engine.model.def.ProcessVarModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * FORM_BEFORE_LOAD 表单构建前触发
 * @preserve 项目事务看板的公共加载类
 * com.actionsoft.apps.gyxmgl.event.BizBeforeTableForPMID
 */
public class BizBeforeTableForPMID extends ExecuteListener {
	
	public BizBeforeTableForPMID(){
		super.setDescription("(公共)项目ID表单加载前事件");
		super.setVersion("V1.0");
	}

	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		//获取流程定义ID
		String processDefID = ctx.getProcessDef().id;
		//获取任务ID
		String taskID = ctx.getTaskInstance().getId();
		//用户ID
		String userID = ctx.getUserContext().getUID();
		//当前的BO表名，从流程变量中获取
		String boName = SDK.getRepositoryAPI().getProcessDefinition(processDefID).getProcessVars().get("boName").getDefaultValue();
		//单据类型，从流程变量中获取
		String djlx = SDK.getRepositoryAPI().getProcessDefinition(processDefID).getProcessVars().get("djlx").getDefaultValue();
		try {
			String xmid = SDK.getProcessAPI().getVariable(processInstId, "xmid").toString();
			if(xmid != "" && !"".equals(xmid) && xmid != null){
				//根据项目ID获取项目数据
				BO xmxxData = SDK.getBOAPI().getByKeyField(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, "xmid", xmid);
				//例外字段数组，项目部门不进行COPY
				String exception = "FQBM";
				//把项目信息表中的数据COPY至业务表中
				BO newData = ObjectCopy.ObjectCopyMtd(xmxxData, boName, exception);
				//---单据信息
				//单据编号
				UserContext  uc = ctx.getUserContext();
				String djbh = djlx+SDK.getRuleAPI().executeAtScript("@year@month")+"-"+SDK.getRuleAPI().executeAtScript("@sequence("+djlx+")");
				String fqrq = SDK.getRuleAPI().executeAtScript("@date");
				String fqbm = SDK.getRuleAPI().executeAtScript("@departmentName",uc);
				String fqr = SDK.getRuleAPI().executeAtScript("@userName",uc);
				String bgdh = SDK.getRuleAPI().executeAtScript("@userTel",uc);
				String jm = "内部公开";
				String jjcd = "普通";
				String bgqx = "永久";
				String bmqx = "永久";
				String sfyy = "否";
				newData.set("DJBH", djbh);	
				newData.set("FQRQ", fqrq);
				newData.set("FQBM", fqbm);
				newData.set("FQR", fqr);
				newData.set("BGDH", bgdh);
				newData.set("JM", jm);
				newData.set("JJCD", jjcd);
				newData.set("BGGX", bgqx);
				newData.set("BMGX", bmqx);
				newData.set("SFYY", sfyy);
				int count = DBSql.getInt("select count(*) as c from "+boName+" where bindid='" + processInstId + "'", "c");
				if(count==0){
					//业务表存储数据
					SDK.getBOAPI().create(boName, newData, ctx.getProcessInstance(), ctx.getUserContext());
					
					//向项目流程统计信息录入数据
					GyPublicUtil.getInstance().getPMStartUtil(processDefID, boName, processInstId, ctx.getUserContext(), 1);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
