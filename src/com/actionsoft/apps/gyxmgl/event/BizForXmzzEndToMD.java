package com.actionsoft.apps.gyxmgl.event;


import java.util.List;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.util.ObjectCopy;
import com.actionsoft.apps.gyxmgl.util.StringUtil;
import com.actionsoft.apps.gyxmgl.webServices.ProjectInfoSynchroClient;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 项目中止流程完成后调用事件
 * com.actionsoft.apps.gyxmgl.event.BizForXmzzEndToMD
 * @author Administrator
 *
 */
public class BizForXmzzEndToMD extends ExecuteListener {
	
	public BizForXmzzEndToMD(){
		super.setDescription("固有项目中止,办理后，项目状态更新至主数据[固有项目信息表]，并且项目中止，创建只读的任务实例，给项目干系人发送通知（审核动作与项目阶段流程信息维护相关）");
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
				String XJDID = DBSql.getString("select XJDID from " + GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE + " where lcid='" + processDefId + "' and rownum=1 order by id asc", "XJDID");
				if(!"".equals(JDTZCD)){
					String JDTZCDS[] = StringUtil.ConvertString(JDTZCD, ",");
					for(int i = 0;i <JDTZCDS.length; i++){
						String wfAuditMenu =JDTZCDS[i];
						//流程是否被激活(如果是,之前做过的业务逻辑不做了)
						String isEnd = twhspxxData.getString("ISEND");
						if("0".equals(isEnd)){
							if(SDK.getTaskAPI().isChoiceActionMenu(taskId, wfAuditMenu)){
								BO xmxxData = ObjectCopy.ObjectCopyMtd(twhspxxData, GlobleParams.BO_ACT_PM_GYXMXXB_TABLE);
								String xmid = twhspxxData.getString("XMID");//项目ID
								xmxxData.set("XMJDID", XJDID);
								xmxxData.set("XMZT", GlobleParams.gyxmzzSuccessStatus);
								int MD_XMXX_BOID = DBSql.getInt("select ID from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'", "ID");
								xmxxData.set("ID", MD_XMXX_BOID);
								//向固有项目信息表同步项目状态
								SDK.getBOAPI().update(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, xmxxData);
								//更新到5平台
								String bindId=DBSql.getString("select bindid from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'");
								ProjectInfoSynchroClient.xmxxToPmsFive(bindId);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//项目中止后，向项目干系人发送通知
			String gxrid = twhspxxData.getString("GXRID");
			BO gxr = (BO) SDK.getBOAPI().query(GlobleParams.BO_ACT_PM_GYXMGXRB_TABLE).addQuery("SSXMID=", twhspxxData.getString("XMID"));
			if (gxr!=null) {
				List<RowMap> list=DBSql.getMaps("select * from '"+GlobleParams.ORGUSER_TABLE+"' where ID=?", gxrid);
			    for(RowMap m : list){
			    	String userid=m.getString("USERID");
			    	//发送通知
			    	SDK.getNotificationAPI().sendMessage("admin", userid, "项目["+twhspxxData.getString("XMMC")+"]已经中止，特此通知！");
				}
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
