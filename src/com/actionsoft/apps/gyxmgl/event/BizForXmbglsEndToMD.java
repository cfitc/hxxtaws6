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
 * 项目信息管理流程完成后调用事件
 * com.actionsoft.apps.gyxmgl.event.BizForXmbglsEndToMD
 * @author Administrator
 *
 */
public class BizForXmbglsEndToMD extends ExecuteListener {
	
	private String XMJL_ROLE = "项目经理";
	private String XMCY_ROLE = "项目成员";
	
	public BizForXmbglsEndToMD(){
		super.setDescription("项目方案变更落实，流程结束后更新固有项目主数据表。（审核动作:同意）");
		super.setVersion("1.0");
	}
	
	/**
	 * 项目变更落实审批结束后的项目状态更新至主数据[项目信息表]
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
				if(!"".equals(JDTZCD)){
					String JDTZCDS[] = StringUtil.ConvertString(JDTZCD, ",");
					for(int i = 0;i <JDTZCDS.length; i++){
						String wfAuditMenu =JDTZCDS[i];
							if(SDK.getTaskAPI().isChoiceActionMenu(taskId, wfAuditMenu)){
								BO xmxxData = ObjectCopy.ObjectCopyMtd(twhspxxData, GlobleParams.BO_ACT_PM_GYXMXXB_TABLE);
								String xmid = twhspxxData.getString("XMID");//项目ID
								String xmjl = twhspxxData.getString("XMJL");//项目经理MMM<MMM>
								String xmzcy = twhspxxData.getString("XMZCY");//项目组成员 XXX<XXX> YYY<YYY> ZZZ<ZZZ> QQQ<QQQ>
								xmxxData.set("XMZT", GlobleParams.gyxmfabgSuccessStatus);
								int MD_XMXX_BOID = DBSql.getInt("select ID from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'", "ID");
								xmxxData.set("ID", MD_XMXX_BOID);
								SDK.getBOAPI().update(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE, xmxxData);
								//更新干系人
								GxrToMD(xmid, xmjl, xmzcy, uid);
								//更新到5平台
								String bindId=DBSql.getString("select bindid from " + GlobleParams.BO_ACT_PM_GYXMXXB_TABLE + " where xmid='" + xmid + "'");
								ProjectInfoSynchroClient.xmxxToPmsFive(bindId);
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
}
