package com.actionsoft.apps.gyxmgl.event;

import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class BizForZjzyxyToMD extends ExecuteListener {
	
	private String XMJL_ROLE = "中介资源负责人";
	
	public BizForZjzyxyToMD(){
		super.setDescription("中介资源选用流程业务表中的数据信息更新至主数据[项目中介资源选用表]和[项目干系人表]");
		super.setVersion("1.0");
	}
	
	@Override
	public void execute(ProcessExecutionContext ctx) throws Exception {
		
		UserContext uc=ctx.getUserContext();
		// 获取流程实例ID
		String processInstId = ctx.getProcessInstance().getId();
		// 获取流程定义ID
		String processDefId = ctx.getProcessDef().id;
		//BO名称
		String boName = SDK.getRepositoryAPI().getProcessDefinition(processDefId).getProcessVars().get("boName").getDefaultValue();
		//查询当前BO数据
		BO twhspxxData = SDK.getBOAPI().query(boName).detailByBindId(processInstId);
		if(twhspxxData != null){
			BO boData = new BO();
			boData.set("XMMC", isNull(twhspxxData.get("XMMC")));
			boData.set("XMBH", isNull(twhspxxData.get("XMBH")));
			boData.set("XMJC", isNull(twhspxxData.get("XMMC")));
			boData.set("XMZT", isNull(twhspxxData.get("XMZT")));
			boData.set("ZJMC", isNull(twhspxxData.get("GYZJMC")));
			boData.set("ZJBH", isNull(twhspxxData.get("GYZJBH")));
			boData.set("ZJID", "");
			boData.set("XMID", isNull(twhspxxData.get("XMID")));
			boData.set("FZR", isNull(twhspxxData.get("FZR")));
			boData.set("CONTENT", isNull(twhspxxData.get("GZNR")));
			boData.set("ZHPJ", isNull(twhspxxData.get("ZHPJ")));
			boData.set("MEMO", isNull(twhspxxData.get("MEMO")));
			boData.set("HXJG", "");
			ProcessInstance inst= SDK.getProcessAPI().createBOProcessInstance("obj_a5aa603aae3b42058aeffb87d8b2a143", "admin","项目中介评估机构");
			SDK.getBOAPI().create("BO_ACT_PM_XMZJGXB", boData, inst,uc);
		
		    GxrToMD(isNull(twhspxxData.get("XMID")), isNull(twhspxxData.get("FZR")), uc);
		}
	}

	/**
	 * 将项目经理、项目组成员抽取至主数据[项目干系人表]
	 * @return
	 */
	public void GxrToMD(String xmid,String xmfzr, UserContext uc){
		//先将项目干系人表中对应项目组人员全删除
		//默认先将项目经理添加到干系人,依次将项目组成员添加
		String team = getUserStr(xmfzr);
		//干系人信息都存在orguser表下
		//干系人角色
		String gxrRole =XMJL_ROLE;
		gxrRole = gxrRole==null?"":gxrRole;
		List<RowMap> list=DBSql.getMaps("select * from BO_ACT_PM_WPZJSPB_S where XM=?", team);
		for(RowMap m : list){
			//项目名称
			String xmmc = DBSql.getString("select XMMC from BO_ACT_PM_GYXMXXB where xmid='" + xmid + "'", "XMMC");
			//项目编号
			String xmbh = DBSql.getString("select XMBH from BO_ACT_PM_GYXMXXB where xmid='" + xmid + "'", "XMBH");
			BO boData = new BO();
			boData.set("SSXMID", xmid);//所属项目ID
			boData.set("GXRJS", gxrRole);//干系人角色
			boData.set("GXRMZ", isNull(m.get("XM")));//干系人姓名
			boData.set("GXRDH", isNull(m.get("TEL")));//干系人电话
			boData.set("GXRSJ", isNull(m.get("PHONE")));//干系人手机
			boData.set("GXRYX", isNull(m.get("MAIL")));//干系人邮箱
			boData.set("XMMC", xmmc);//项目名称   
			boData.set("GXRUSERID", team);//干系人USERID
			boData.set("XMBH", xmbh);     //项目编号 
			SDK.getBOAPI().createDataBO("BO_ACT_PM_XMGXRXXB", boData, uc);
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
	
	/**
	 * 将xxx<XXX>转成xxx
	 * @param str
	 * @return
	 */
	public static String getUserStr(String str) {
		if (str.indexOf("<") > -1)
			str = str.substring(0, str.indexOf("<"));
		if (str.indexOf("&lg;") > -1)
			str = str.substring(0, str.indexOf("&lt"));
		return str;
	}
}
