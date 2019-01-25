package com.actionsoft.apps.gyxmgl.web;

import java.util.List;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.commons.database.RowMap;

public class GyPmProcessMapWeb {
	
	/**
	 * 创建固有项目流程地图
	 * @return
	 */
	public String CreateGyPmProcessMap(){
		StringBuffer sb = new StringBuffer();
		sb.append(createProcessList("项目立项"));
		sb.append(createProcessList("项目运行中"));
		sb.append(createProcessList("项目结束"));
		return sb.toString();
	}
	
	public String createProcessList(String jdmc){
		String sql = "select * from "+GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE+" where gllx =? order by pxh";
		//查询固有项目流程阶段配置表
		List<RowMap> rmList = DBSql.getMaps(sql,jdmc);
		StringBuffer sb = new StringBuffer();
		sb.append("<ul>");
		for(RowMap m:rmList){
			if(m.getString("LCFL").equals("A自营项目")){
				sb.append("<li>");
				sb.append(m.getString("LCMC"));
				sb.append("</li>");
			}
			else if(m.getString("LCFL").equals("G公共流程") || m.getString("LCFL").equals("E合同管理") ){
				sb.append("<li class=\"bg2\">");
				sb.append(m.getString("LCMC"));
				sb.append("</li>");
			}
			else if(m.getString("LCFL").equals("C计划财务")){
				sb.append("<li class=\"bg1\">");
				sb.append(m.getString("LCMC"));
				sb.append("</li>");
			}
			else{
				sb.append("<li>");
				sb.append(m.getString("LCMC"));
				sb.append("</li>");
			}
		}
		sb.append("</ul>");
		return sb.toString();
	}
}
