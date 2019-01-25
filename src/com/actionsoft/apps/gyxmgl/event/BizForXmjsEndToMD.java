package com.actionsoft.apps.gyxmgl.event;


import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.util.ObjectCopy;
import com.actionsoft.apps.gyxmgl.util.StringUtil;
import com.actionsoft.apps.gyxmgl.util.WorkFlowUtil;
import com.actionsoft.apps.gyxmgl.webServices.ProjectInfoSynchroClient;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 投委会审批流程完成后调用事件
 * com.actionsoft.apps.gyxmgl.event.BizForXmjsEndToMD
 * @author CHENDX
 *
 */
public class BizForXmjsEndToMD extends ExecuteListener {
	
	private String XMJL_ROLE = "项目经理";
	private String XMCY_ROLE = "项目成员";
	
	public BizForXmjsEndToMD(){
		super.setDescription("固有项目归档：办公室负责人审批通过后，更新项目主数据【固有项目信息表】项目状态为“已归档”");
		super.setVersion("1.0");
	}
	
	/**
	 * 将投委会审批信息中的项目经理、项目组成员抽取至主数据[项目干系人表]
	 */
	public void execute(ProcessExecutionContext ctx) throws Exception {
		// 获取流程实例ID
		System.out.println("222222");
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
				String JDTZCD = DBSql.getString("select JDTZCD from " + GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE + " where lcuuid='" + processDefId + "' and rownum=1 order by id asc", "JDTZCD");
				//下阶段ID
				String XJDID = DBSql.getString("select XJDID from " + GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE + " where lcuuid='" + processDefId + "' and rownum=1 order by id asc", "XJDID");
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
								xmxxData.set("XMZT", GlobleParams.gyxmxmxmjsSuccessStatus);
								String MD_XMXX_BOID = DBSql.getString("select ID from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'", "ID");
								xmxxData.set("ID", MD_XMXX_BOID); 
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
		}
	}
}
