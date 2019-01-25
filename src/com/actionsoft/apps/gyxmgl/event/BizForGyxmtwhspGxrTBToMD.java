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
 * com.actionsoft.apps.gyxmgl.event.BizForGyxmtwhspGxrToMD
 * @author Administrator
 *
 */
public class BizForGyxmtwhspGxrTBToMD extends ExecuteListener {
	
	private String XMJL_ROLE = "项目经理";
	private String XMCY_ROLE = "项目成员";
//	
//	public BizForGyxmtwhspGxrToMD(){
//		super.setDescription("投委会审批最后环节办理后,将项目信息中项目经理、项目组成员数据备份至主数据[项目干系人表]及更改主数据的项目状态（审核动作与项目阶段流程信息维护相关）");
//		super.setVersion("1.0");
//	}
	
	/**
	 * 将投委会审批信息中的项目经理、项目组成员抽取至主数据[项目干系人表]
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
								String xmjl = twhspxxData.getString("XMJL");//项目经理MMM<MMM>
								String xmzcy = twhspxxData.getString("XMZCY");//项目组成员 XXX<XXX> YYY<YYY> ZZZ<ZZZ> QQQ<QQQ>
								xmxxData.set("XMJDID", XJDID);
								xmxxData.set("XMZT", GlobleParams.gyxmlxspSuccessStatus);
								String MD_XMXX_BOID = DBSql.getString("select ID from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'", "ID");
								xmxxData.set("ID", MD_XMXX_BOID);
								SDK.getBOAPI().update(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, xmxxData);
								GxrToMD(xmid,xmjl,xmzcy,uid);
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
	/**
	 * 将项目经理、项目组成员抽取至主数据[项目干系人表]
	 * @param xmid项目ID
	 * @param xmjl项目经理
	 * @param xmzcy项目组成员
	 * @return
	 */
	public void GxrToMD(String xmid,String xmjl,String xmzcy,String createUser){
		//先将项目干系人表中对应项目组人员全删除
		//DBSql.executeUpdate("delete from " + GlobleNames.BO_BCF_TRU_MD_XMGXRXXB_TABLE + " where SSXMID='"+xmid+"'");
		//默认先将项目经理添加到干系人,依次将项目组成员添加
		String team = StringUtil.getUserStr(xmjl);
		String xmzcyStr[] = StringUtil.ConvertString(xmzcy," ");
		for (int i = 0; i < xmzcyStr.length + 1; i++) {
			//干系人信息都存在orguser表下
			//干系人ID
			String gxrId = DBSql.getString("select ID from " + GlobleParams.ORGUSER_TABLE + " where userid='" + team + "'", "ID");
			//先查此人是否在此项目组干系人中,在的话,进入下个循环,不在就加入
			 int xmgxrCount = DBSql.getInt("select count(*) as c from " + GlobleParams.BO_ACT_PM_GYXMGXRB_TABLE + " where SSXMID='"+xmid+"' and GXRID='"+gxrId+"'", "c");//修改之后的
			 if(xmgxrCount==0){
				//干系人角色
				String gxrRole = i==0?XMJL_ROLE:XMCY_ROLE;
				gxrRole = gxrRole==null?"":gxrRole;
				UserModel gxrModel = SDK.getORGAPI().getUser(team);
				//干系人姓名
				String gxrXm = gxrModel.getUserName();
				//干系人所属单位
				String gxrSsdw = SDK.getORGAPI().getDepartmentByUser(team).getName();
				//干系人电话
				String gxrDh = gxrModel.getOfficeTel();
				//干系人手机
				String gxrSj = gxrModel.getMobile();
				//干系人邮箱
				String gxrYx = gxrModel.getEmail();
				//项目名称
				String xmmc = DBSql.getString("select XMMC from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'", "XMMC");
				//项目编号
				String xmbh = DBSql.getString("select XMBH from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'", "XMBH");//修改之后的
				//项目发起部门
				String xmfqbm = DBSql.getString("select XMFQBM from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'", "XMFQBM");//修改之后的
				BO gxxData = new BO();
				gxxData.set("SSXMID", xmid);//所属项目ID
				gxxData.set("GXRJS", gxrRole);//干系人角色
				gxxData.set("GXRID", gxrId);//干系人ID
				gxxData.set("GXRMZ", gxrXm);//干系人姓名
				gxxData.set("GXRDW", gxrSsdw);//干系人所属单位
				gxxData.set("GXRDH", gxrDh);//干系人电话
				gxxData.set("GXRSJ", gxrSj);//干系人手机
				gxxData.set("GXRYX", gxrYx);//干系人邮箱
				gxxData.set("XMMC", xmmc);//项目名称
				gxxData.set("GXRUSERID", team);//干系人USERID  修改之后的
				gxxData.set("XMBH", xmbh);     //项目编号  修改之后的
				gxxData.set("XMFQBM", xmfqbm); //项目发起部门  修改之后的
				WorkFlowUtil.CreateJCCData(GlobleParams.GYXMGXR_UUID, createUser,gxrXm, GlobleParams.BO_ACT_PM_GYXMGXRB_TABLE, gxxData);
			 }
			 if(xmzcyStr.length==i)
					break;
				team = StringUtil.getUserStr(xmzcyStr[i]);
		}
	}
	
	/**  
	 * (1).流程结束点有多处时
	 * 比如:1.总经理审批         同意
	 *      2.董事长审批       审批通过
	 * 
	 * (2).激活流程重新办理情况
	 * 业务逻辑代码又重新走了遍。
	 * 激活的流程的业务数据中isend=1,等于1说明流程已结束
	 * 
	 * (3).流程最后环节参与者多人
	 * 业务逻辑代码会执行多次
	 * WorkflowInstanceAPI.getInstance().isClose(instanceId);//判断流程实例是否已结束   此种方式判断不了
	 * 只能放到流程事件触发器。
	 * 
	 * 特殊情况(审核菜单):不同审核动作,而且业务逻辑不同,都是结束流程,这样的情况需要对应写特殊处理，尽量不要出现(导致代码)。
	 * 辅助流程与触发流程不考虑最后节点的审核菜单配置，由于个别辅助流程与触发触发流程的最后节点有业务逻辑处理，
     * 就加了人工审核动作判断，如果大曲对这些流程的人工审核菜单有改动需改成之前的。
     * 
     * //是否为最后参与者(如果是,之前做过的业务逻辑不做了)
	 * //boolean isEndUser = WorkflowInstanceAPI.getInstance().isClose(instanceId);
	 * //System.out.println("isEndUser:" + isEndUser);
	 * //此业务流程未有总经理与董事长审批暂不考虑
	 */
}
