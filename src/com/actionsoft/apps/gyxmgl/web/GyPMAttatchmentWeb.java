package com.actionsoft.apps.gyxmgl.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;

public class GyPMAttatchmentWeb {


	public String GyPMMassage(String xmid,UserContext me){
		Map<String, Object> map = new HashMap<String, Object>();
		map = DBSql.getMap("select XMBH,XMMC,XMXZ,XMFQBM,XMJL,XMZT,CLRQ,JSRQ,CLGM,XMYJGMMAX from BO_ACT_PM_GYXMXXB where xmid =?",xmid);
		StringBuffer pmmsg = new StringBuffer();
		pmmsg.append("<table width='90%' border='0' cellspacing='0' cellpadding='0' align='center'>");
		pmmsg.append("<tr>");
		pmmsg.append("<td style='font-size:12px;'> 项目编号："+objToString(map.get("XMBH"))+"</td>");
		pmmsg.append("<td style='font-size:12px;'> 项目名称："+objToString(map.get("XMMC"))+"</td>");
		pmmsg.append("<td style='font-size:12px;'> 项目性质："+objToString(map.get("XMXZ"))+"</td>");
		pmmsg.append("</tr><tr>");
		pmmsg.append("<td style='font-size:12px;'> 部门名称："+objToString(map.get("XMFQBM"))+"</td>");
		pmmsg.append("<td style='font-size:12px;'> 项目经理："+objToString(map.get("XMJL"))+"</td>");
		pmmsg.append("<td style='font-size:12px;'> 项目状态："+objToString(map.get("XMZT"))+"</td>");
		pmmsg.append("</tr><tr>");
		pmmsg.append("<td style='font-size:12px;'> 项目开始日期："+objToString(map.get("CLRQ"))+"</td>");
		pmmsg.append("<td style='font-size:12px;'> 项目结束日期："+objToString(map.get("JSRQ"))+"</td>");
		pmmsg.append("<td style='font-size:12px;'> 项目规模（元）："+objToString(map.get("CLGM"))+"</td>");
		pmmsg.append("</tr><tr>");
		pmmsg.append("<td colspan='3' style='font-size:12px;'> 项目预计规模（元）："+objToString(map.get("XMYJGMMAX"))+"</td>");
		pmmsg.append("</tr></table>");
		return pmmsg.toString();
		
	}

	public String GyPMAttachment(String xmid,String inputvalue,UserContext me){
		StringBuffer att = new StringBuffer();
		List<RowMap> list = DBSql.getMaps("select doc.id,doc.xmjd,doc.scr,doc.scsj,doc.wjbh,doc.wjmc,doc.wdlb,doc.dzwj,doc.wjzt,jd.xmjdmc,tjxx.lcbt as lcmc from BO_ACT_ATTACH doc join BO_ACT_PM_WF_TJXX tjxx on doc.bindid = tjxx.lcbindid and doc.xmjd = tjxx.xmjdid join BO_ACT_PM_XMJDB jd on tjxx.xmjdid = jd.id where jd.sfqy = '是' and doc.xmid = ? and (jd.xmjdmc like ? or tjxx.lcbt like ? or doc.scr like ? or doc.wdlb like ? or doc.dzwj like ? or doc.wjbh like ? or doc.wjmc like ? or doc.wjzt like ?) order by doc.wdlb, jd.pxh desc, doc.scsj desc",xmid,inputvalue,inputvalue,inputvalue,inputvalue,inputvalue,inputvalue,inputvalue,inputvalue);
		for(int i=0;i<list.size();i++){
			RowMap m = list.get(i);
			att.append("<tr>");
			att.append("<td>"+(i+1)+"</td>");
			att.append("<td width='10%' style='font-size:12px;'>"+objToString(m.get("xmjdmc"))+"</td>");
			att.append("<td width='15%' style='font-size:12px;'>"+objToString(m.get("lcbt"))+"</td>");
			att.append("<td width='10%' style='font-size:12px;'>"+objToString(m.get("wdlb"))+"</td>");
			att.append("<td width='15%' style='font-size:12px;'>"+objToString(m.get("dzwj"))+"</td>");
			att.append("<td width='5%' style='font-size:12px;'>"+objToString(m.get("wjzt"))+"</td>");
			att.append("<td width='10%' style='font-size:12px;'>"+objToString(m.get("scr"))+"</td>");
			att.append("<td width='10%' style='font-size:12px;'>"+objToString(m.get("scsj"))+"</td>");
			att.append("</tr>");			
		}
		att.append("<tr><td colspan='10' align='right'></td></tr>");
		return att.toString();
		
	}

	public static String objToString(Object obj) {
		return obj == null || obj.equals(null) ? "" : obj.toString();
	}

}
