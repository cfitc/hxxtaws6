package com.actionsoft.apps.hrm.web;

import java.util.List;
import java.util.Set;

import com.actionsoft.apps.hrm.util.GlobleParams;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.commons.database.RowMap;

public class OaProcessMapWeb {
	
	/**
	 * 组装流程地图界面
	 * @return
	 */
	public String createOaProcessWeb(UserContext me,String lcmc){
		String sql = "select CNNAME,ITEMNO from BO_ACT_DICT_KV_ITEM where  DICTKEY='OALCFL' and ISACTIVE=1 order by ORDERINDEX asc";
		List<RowMap> rmList = DBSql.getMaps(sql);
		StringBuffer sb = new StringBuffer();
		for(RowMap m:rmList){
			String getLcListsql = "select * from "+GlobleParams.BO_ACT_OALCFLPZ_TABLE+" where LCFL = ? and SFQY = '是' order by lcpxh";
			if(lcmc != "" && !lcmc.equals("")){
				getLcListsql = "select * from "+GlobleParams.BO_ACT_OALCFLPZ_TABLE+" where LCFL = ? and SFQY = '是' and lcmc like '%"+lcmc+"%' order by lcpxh";
			}
			List<RowMap> lcList = DBSql.getMaps(getLcListsql,m.get("ITEMNO"));
			sb.append("<div class=\"process-item\">");
			sb.append("<div class=\"pItem-title\">");
			sb.append("<div class=\"fl\">");
			sb.append("<img src=\"../apps/com.actionsoft.apps.gyxmgl/images/process/open.png\" class=\"up-down\"/>");
			sb.append("</div>");
			sb.append("<div class=\"fl\">");
			sb.append(m.get("CNNAME")+"(共"+lcList.size()+"个流程)");
			sb.append("</div>");
			sb.append("</div>");
			sb.append(" <div class=\"pItem-content\">");
			sb.append("<ul class=\"progess-item\">");
			for(int i=0;i<lcList.size();i++){
				sb.append("<li><a href=\"javascript:void(0)\" onclick=\"createOaProcess('"+lcList.get(i).getString("LCUUID")+"','"+lcList.get(i).getString("LCMC")+"')\">"+lcList.get(i).getString("LCMC")+"</a></li>");
			} 
			sb.append("</ul>");
			sb.append("</div> </div>");
			
		}
		System.out.print(sb.toString());
		return sb.toString();
	}
}
