package com.actionsoft.apps.gyxmgl.web;

import java.util.List;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.bpmn.engine.model.def.ActivityModel;
import com.actionsoft.bpms.bpmn.engine.model.def.ProcessDefinition;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class GyPMProcessListWeb {
	
	public String createGyPMProcessList(){
		//从流程配置清单查询流程列表
		//String sql = "select g.gllx,g.lcfl,g.lcmc,g.lcuuid from "+GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE+" g where g.xmlb =? order by case when g.gllx = '项目立项' then 1 when g.gllx = '项目运行中' then 2 else 3 end, g.pxh;";
		String sql = "select g.gllx,g.lcfl,g.lcmc,g.lcuuid from "+GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE+" g where g.xmlb =? order by g.gllx,g.pxh";
		List<RowMap> rstList = DBSql.getMaps(sql, "自营项目");
		StringBuffer sb = new StringBuffer();
		sb.append("<table class=\"processListTable\">");
		sb.append("<tr>");
		sb.append("<th width=\"40px\">序号</th>");
		sb.append("<th width=\"80px\">项目阶段</th>");
		sb.append("<th width=\"80px\">流程类型</th>");
		sb.append("<th width=\"200px\">流程名称</th>");
		sb.append("<th>详细流程</th>");
		sb.append("</tr>");
		for(int i=0;i<rstList.size();i++){
			sb.append("<tr>");
			sb.append("<td>"+(i+1)+"</td>");
			sb.append("<td>"+rstList.get(i).getString("GLLX")+"</td>");
			sb.append("<td>"+rstList.get(i).getString("LCFL")+"</td>");
			sb.append("<td>"+rstList.get(i).getString("LCMC")+"</td>");
			sb.append("<td>"+getProcessTaskByDefID(rstList.get(i).getString("LCUUID"))+"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		System.out.print(sb.toString());
		return sb.toString();
	}
	
	/**
	 * 根据流程实例ID获取该流程的所有节点
	 *<p>Title: GyPMProcesslistWeb.java<／p>
	 *<p>Description: <／p>
	 * @author 陈昱
	 * 2017年10月11日
	 * @return
	 */
	public String getProcessTaskByDefID(String processDefId){
		ProcessDefinition processDef = SDK.getRepositoryAPI().getProcessDefinition(processDefId);
		List<ActivityModel> taskList = processDef.getTaskList();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<taskList.size();i++){
			sb.append("["+taskList.get(i).getNo()+"]"+taskList.get(i).getName()+"->");
		}
		String rs = sb.toString();
		rs = rs.substring(0,rs.length()-2);
		return rs;
	}
}
