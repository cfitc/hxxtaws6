package com.actionsoft.apps.gyxmgl.event;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.util.ObjectCopy;
import com.actionsoft.apps.gyxmgl.util.StringUtil;
import com.actionsoft.apps.gyxmgl.webServices.ProjectInfoSynchroClient;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 固有项目方案变更（无风险）流程完成后调用事件
 * com.actionsoft.apps.gyxmgl.event.BizForGyxmbgEndToMD
 * @author chendx
 *
 */
public class BizForGyxmbgEndToMD extends ExecuteListener {
	
	public BizForGyxmbgEndToMD(){
		super.setDescription("固有项目变更审批结束后,项目信息更新至主数据的项目信息表。（审核动作:同意）");
		super.setVersion("1.0");
	}
	
	/**
	 * 更新主数据[项目干系人表]
	 * 创建只读的任务实例，给项目干系人发送通知
	 */
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		// 获取流程定义ID
		String processDefId = ctx.getProcessDef().id;
		//任务ID
		String taskId = ctx.getTaskInstance().getId();
		//用户ID
		String uid = ctx.getUserContext().getUID();
		//BO名称
		String boName = SDK.getRepositoryAPI().getProcessDefinition(processDefId).getProcessVars().get("boName").getDefaultValue();
		//查询当前BO数据
		BO twhspxxData = SDK.getBOAPI().query(boName).detailByBindId(processInstId);
		if(twhspxxData != null){
			try {
				//阶段跳转菜单名称
				String JDTZCD = DBSql.getString("select JDTZCD from " + GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE + " where lcid='" + processDefId + "' and rownum=1 order by id asc", "JDTZCD");
				//下阶段ID
//				String XJDID = DBSql.getString("select XJDID from " + GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE + " where lcid='" + processDefId + "' and rownum=1 order by id asc", "XJDID");
				if(!"".equals(JDTZCD)){
					String JDTZCDS[] = StringUtil.ConvertString(JDTZCD, ",");
					for(int i = 0;i <JDTZCDS.length; i++){
						String wfAuditMenu =JDTZCDS[i];
						//流程是否被激活(如果是,之前做过的业务逻辑不做了)
						String isEnd = twhspxxData.getString("ISEND");
						if("0".equals(isEnd)){
							if(SDK.getTaskAPI().isChoiceActionMenu(taskId, wfAuditMenu)){
								BO xmxxData = ObjectCopy.ObjectCopyMtd(twhspxxData, GlobleParams.BO_ACT_PM_GYXMXXB_TABLE);
								//向固有项目信息表同步项目状态
								SDK.getBOAPI().update(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, xmxxData);
								//更新到5平台
								String bindId=DBSql.getString("select bindid from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmxxData.getString("XMID") + "'");
								ProjectInfoSynchroClient.xmxxToPmsFive(bindId);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 判断是否为null
	 * @param obj
	 * @return
	 */
	public static String isNull(Object obj){
		return obj==null || obj.equals(null)?"":obj.toString();
	}
}
