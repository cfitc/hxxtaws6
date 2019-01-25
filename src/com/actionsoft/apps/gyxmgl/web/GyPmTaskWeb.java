package com.actionsoft.apps.gyxmgl.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.apps.gyxmgl.service.pmTaskDoCheckUtil;
import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.apps.gyxmgl.util.StringUtil;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

/**
 * 项目任务卡页面 -事务管理
 * @author Administrator
 *
 */
public class GyPmTaskWeb {
	
	
	public String CreatePmProgressInfoHtml(String xmid,UserContext me){
		RowMap m = DBSql.getMap("select x.xmmc,j.xmjdmc from "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" x left join "
				+GlobleParams.BO_ACT_PM_XMJDB_TABLE+" j on x.xmjdid = j.id where x.xmid=?",xmid);
		StringBuffer sb = new StringBuffer();
		String xmmc = m.getString("xmmc");
		String xmjdmc = m.getString("xmjdmc");
		sb.append("<div class=\"title fl\">");
		sb.append("<b>"+m.getString("xmmc")+"</b>");
		sb.append("</div>");
		sb.append("<div class=\"headlr fr\">");
		if("项目储备".equals(xmjdmc)){
			sb.append("<div class=\"ite ite-ing\">项目储备</div>");
		}else{
			sb.append("<div class=\"ite ite-before\">项目储备</div>");
		}
		if("项目立项".equals(xmjdmc)){
			sb.append("<div class=\"ite ite-ing\">项目立项</div>");
		}else{
			sb.append("<div class=\"ite ite-before\">项目立项</div>");
		}
		if("项目运行中".equals(xmjdmc)){
			sb.append("<div class=\"ite ite-ing\">项目运行中</div>");
		}else{
			sb.append("<div class=\"ite ite-before\">项目运行中</div>");
		}
		if("项目结束".equals(xmjdmc)){
			sb.append("<div class=\"ite ite-ing\">项目结束</div>");
		}else{
			sb.append("<div class=\"ite ite-before\">项目结束</div>");
		}
		sb.append("</div>");
		return sb.toString();
	}
	/**
	 * 创建项目基本信息表单
	 * @param xmid
	 * @param me
	 * @return
	 */
	public String CreatePmBaseIfnoHtml(String xmid,UserContext me){
		
		Map<String, Object> map = new HashMap<String, Object>();
		map = DBSql.getMap("select a.xmid,a.xmbh,a.xmmc,a.xmjdid,a.xmzt,a.xmxz,a.clrq,a.jsrq,a.xmjl,a.clgm,a.xmyjgmmin,a.xmyjgmmax from "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" a  where xmid=?", xmid);
		StringBuffer XmjbxxTb = new StringBuffer();
		//项目经理
		String xmjl = objToString(map.get("xmjl"));
		//项目经理UserID
		String userid = xmjl.split("<")[0];
		//部门名称
		String departmentname = DBSql.getString("select b.departmentname from orguser a join orgdepartment b on a.departmentid=b.id  where userid='"+userid+"'", "departmentname");
		XmjbxxTb.append("<table class=\"tab1\" border=\"1px\">");
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\" width='15%'>项目编号</td>");
		XmjbxxTb.append("<td width='35%'>"+objToString(map.get("xmbh"))+"</td>");
		XmjbxxTb.append("<td class=\"td-title\" width='15%'>项目名称</td>");
		XmjbxxTb.append("<td width='35%'>"+objToString(map.get("xmmc"))+"</td>");
		XmjbxxTb.append("</tr>");
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目性质</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("xmxz"))+"</td>");
		XmjbxxTb.append("<td class=\"td-title\">部门名称</td>");
		XmjbxxTb.append("<td>"+departmentname+"</td>");
		XmjbxxTb.append("</tr>");
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目经理</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("xmjl"))+"</td>");
		XmjbxxTb.append("<td class=\"td-title\">项目状态</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("xmzt"))+"</td>");
		XmjbxxTb.append("</tr>");
		
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目开始日期</td>");
		if(map.get("CLRQ") != null){
			XmjbxxTb.append("<td>"+StringUtil.getYMDDate(objToString(map.get("clrq")))+"</td>");
		}else{
			XmjbxxTb.append("<td></td>");
		}
		XmjbxxTb.append("<td class=\"td-title\">项目结束日期</td>");
		if(map.get("JSRQ") != null){
			XmjbxxTb.append("<td>"+StringUtil.getYMDDate(objToString(map.get("jsrq")))+"</td>");
		}else{
			XmjbxxTb.append("<td></td>");
		}
		XmjbxxTb.append("</tr>");
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目规模（元）</td>");
		if(objToString("clgm")==null){
			XmjbxxTb.append("<td>0</td>");
		}else{
			XmjbxxTb.append("<td> "+objToString(map.get("clgm"))+"</td>");
		}
		XmjbxxTb.append("<td class=\"td-title\">项目预计规模（元）</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("xmyjgmmax"))+"</td>");
		XmjbxxTb.append("</tr>");
		XmjbxxTb.append("</table>");
		return XmjbxxTb.toString();
	}
	
	/**
	 * 创建项目可发起任务信息表单
	 * @param xmid
	 * @param me
	 * @return
	 */
	public String CreatePmTaskStartHtml(String xmid,UserContext me){
		//操作者角色
		String me_roleName = me.getRoleModel().getName();
		//项目ID
		RowMap xmxx = DBSql.getMap("select xmjdid,xmxz,xmmc from "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" where xmid = ?", xmid);
		//项目阶段ID
		String xmjdid = xmxx.getString("xmjdid");
		//项目性质
		String xmxz = xmxx.getString("xmxz");
		//项目名称
		String xmmc = xmxx.getString("xmmc");
		//可发起的流程清单
		String taskInfo_Sql = "select pm.xmlbmc,jd.xmjdmc,jd.id jdid,jdlc.lcz,jdlc.lcmc,jdlc.lcid,jdlc.lcuuid,jdlc.lcfl from " 
   		         +GlobleParams.BO_ACT_PM_XMFLB_TABLE+"   pm join  "+GlobleParams.BO_ACT_PM_XMJDB_TABLE+" jd on pm.bindid = jd.bindid and jd.sfqy='是' join  " 
   		         +GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE+"   jdlc on jd.id = jdlc.jdid and (jdlc.FJTJ is null or jdlc.FJTJ like ?) " 
   			     +"  where jd.id=?";
		if(!me_roleName.equals("系统管理员") && me_roleName!="系统管理员"){
			taskInfo_Sql+="  and (jdlc.lcjs='"+me_roleName+"' or jdlc.lcjs is null) ";
		}
			taskInfo_Sql+= " order by jdlc.lcfl,pm.pxh,jd.pxh,jdlc.pxh,jdlc.lcz";
		//判断流程的可发起次数
		pmTaskDoCheckUtil pmtaskche=new pmTaskDoCheckUtil();
		
        StringBuffer sb = new StringBuffer();
        sb.append("<table class=\"table21\" border=\"1px\" >");
        sb.append("<tr class=\"tr-title\">");
        sb.append("<td>序号</td>");
        sb.append("<td>项目阶段</td>");
        sb.append("<td>流程分类</td>");
        sb.append("<td>可发起事务</td>");
        sb.append("<td>操作</td>");
        sb.append("</tr>");
        List<RowMap> list = DBSql.getMaps(taskInfo_Sql,'%'+xmxz+'%',xmjdid);
        for(int i=0;i<list.size();i++){
        	RowMap m = list.get(i);
        	//流程ID
        	String lcid = m.getString("lcid");
        	//阶段IDl
        	String jdid = m.getString("jdid");
        	//流程定义ID
        	String lcuuid = m.getString("lcuuid");
        	//流程名称
        	String lcmc = m.getString("lcmc");
        	sb.append("<tr>");
        	sb.append("<td>"+(i+1)+"</td>");
        	sb.append("<td>"+m.getString("xmjdmc")+"</td>");
        	sb.append("<td>"+m.getString("lcfl")+"</td>");
        	sb.append("<td>"+m.getString("lcmc")+"</td>");
        	boolean bl = pmtaskche.pmTaskDoCheck(xmid, lcid, jdid);
        	if(bl){
				sb.append("<td> <input type=button value='启动'     class ='actionsoftButton send' onClick=\"UtilcreateWorkflow('"+xmid+"','"+xmmc+"','"+lcuuid+"','"+lcmc+"');return false;\" name='newInstance'  border='0'> </td>");	
			}else{
				//onClick=\"UtilcreateWorkflow(frmMain,'"+lcuuid+"',"+pmId+");return false;\"
				sb.append("<td class=\"actionsoftData\" align='center'> <input id=\"oButton\" stype=\"oButton\" type=button value='启动' class ='actionsoftButton' name='newInstance'  border='0'> </td>");
			}
        	sb.append("</tr>");
        }
        sb.append("</table>");
		return sb.toString();
	}
	
	/**
	 * 创建项目待办信息表单
	 * 查询该项目的所有待办信息，只能办理自己的待办
	 * @param xmid
	 * @param me
	 * @return
	 */
	public String CreatePmTaskInfoHtml(String xmid,UserContext me){
		//项目阶段ID
		String xmjdID = DBSql.getString("select xmjdid from "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" where xmid = "+xmid,"xmjdid");
		String sql = "select jdgl.lcmc,tj.lcbt, wf.processinstid,wf.owner,wf.target,wf.begintime"
			  +"from "+GlobleParams.BO_ACT_PM_WF_TJXX_TABLE+" tj"
			  +"left join "+GlobleParams.BO_ACT_PM_XMJDLCGXB_TABLE+" jdgl"
			  +" on tj.xmjdid = jdgl.jdid"
			  +" and jdgl.jdid = '"+xmjdID+"'"
			  +" and jdgl.lcuuid = tj.lcid"
			  +"left join "+GlobleParams.WFC_TASK_TABLE+" wf"
			  +"  on tj.lcbindid = wf.processinstid"
			  +"where tj.xmid = '"+xmid+"'"
			  +" and wf.taskstate = '1'";
		System.out.println(sql);
		return null;
	}
	/**
	 * 创建项目完整信息表单
	 * @param xmid
	 * @param me
	 * @return
	 */
	public String CreatePmcompleteIfnoHtml(String xmid,UserContext me){
		
		Map<String, Object> map = new HashMap<String, Object>();
		String pingsql="a.XMJC,a.XMQX,a.XMSX,a.XMXZ,a.CLRQ,a.JSRQ,a.HKLY,a.XMZT,a.XMZHSYLMAX,a.XMYJGMMAX,a.ZJTX,a.YJSR,a.CLGM,a.XMZCY,a.XMGS,a.ZXCS,a.FXKZSD,a.DJZBYX,a.MEMO";
		map = DBSql.getMap("select a.xmid,a.xmbh,a.xmmc,a.xmjdid,a.xmzt,a.xmxz,a.clrq,a.jsrq,a.xmjl,a.clgm,"
				+ "a.xmyjgmmin,a.xmyjgmmax,"+pingsql+" from "+GlobleParams.BO_ACT_PM_GYXMXXB_TABLE+" a  where xmid=?", xmid);
		StringBuffer XmjbxxTb = new StringBuffer();
		//项目经理
		String xmjl = objToString(map.get("xmjl"));
		//项目经理UserID
		String userid = xmjl.split("<")[0];
		//部门名称
		String departmentname = DBSql.getString("select b.departmentname from orguser a join orgdepartment b on a.departmentid=b.id  where userid='"+userid+"'", "departmentname");
		XmjbxxTb.append("<table class=\"tab1\" border=\"1px\">");
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\" style=\"width:15%\">项目简称</td>");
		XmjbxxTb.append("<td style=\"width:35%\">"+objToString(map.get("XMJC"))+"</td>");
		XmjbxxTb.append("<td class=\"td-title\" style=\"width:15%\">项目期限</td>");
		XmjbxxTb.append("<td style=\"width:35%\">"+objToString(map.get("XMQX"))+"</td>");
		XmjbxxTb.append("</tr>");
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目属性</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("XMSX"))+"</td>");
		XmjbxxTb.append("<td class=\"td-title\">项目性质</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("XMXZ"))+"</td>");
		XmjbxxTb.append("</tr>");
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目成立日期</td>");
		if(map.get("CLRQ") != null){
			XmjbxxTb.append("<td>"+StringUtil.getYMDDate(objToString(map.get("clrq")))+"</td>");
		}else{
			XmjbxxTb.append("<td></td>");
		}
		XmjbxxTb.append("<td class=\"td-title\">项目结束日期</td>");
		if(map.get("JSRQ") != null){
			XmjbxxTb.append("<td>"+StringUtil.getYMDDate(objToString(map.get("jsrq")))+"</td>");
		}else{
			XmjbxxTb.append("<td></td>");
		}
		XmjbxxTb.append("</tr>");
		
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">收益实现方式</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("HKLY"))+"</td>");
		XmjbxxTb.append("<td class=\"td-title\">项目状态</td>");
		XmjbxxTb.append("<td colspan='2'>"+objToString(map.get("XMZT"))+"</td>");
		XmjbxxTb.append("</tr>");
		
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目综合收益率%</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("XMZHSYLMAX"))+"</td>");
		XmjbxxTb.append("<td class=\"td-title\">预期收益(元)</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("YJSR"))+"</td>");
		XmjbxxTb.append("</tr>");
		
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">预计项目规模(元)</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("XMYJGMMAX"))+"</td>");
		XmjbxxTb.append("<td class=\"td-title\">项目成立规模(元)</td>");
		XmjbxxTb.append("<td>"+objToString(map.get("CLGM"))+"</td>");
		XmjbxxTb.append("</tr>");
		
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">资金投向</td>");
		XmjbxxTb.append("<td colspan='3'>"+objToString(map.get("ZJTX"))+"</td>");
		XmjbxxTb.append("</tr>");
		
		
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目组成员</td>");
		XmjbxxTb.append("<td colspan='3'>"+objToString(map.get("XMZCY"))+"</td>");
		XmjbxxTb.append("</tr>");
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">项目概述</td>");
		XmjbxxTb.append("<td colspan='3'>"+objToString(map.get("XMGS"))+"</td>");
		XmjbxxTb.append("</tr>");
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">增信措施</td>");
		XmjbxxTb.append("<td colspan='3'>"+objToString(map.get("ZXCS"))+"</td>");
		XmjbxxTb.append("</tr>");
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">风险控制手段</td>");
		XmjbxxTb.append("<td colspan='3'>"+objToString(map.get("FXKZSD"))+"</td>");
		XmjbxxTb.append("</tr>");
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">对净资本影响</td>");
		XmjbxxTb.append("<td colspan='3'>"+objToString(map.get("DJZBYX"))+"</td>");
		XmjbxxTb.append("</tr>");
		XmjbxxTb.append("<tr>");
		XmjbxxTb.append("<td class=\"td-title\">备注</td>");
		XmjbxxTb.append("<td colspan='3'>"+objToString(map.get("MEMO"))+"</td>");
		XmjbxxTb.append("</tr>");
		XmjbxxTb.append("</table>");
		return XmjbxxTb.toString();
	}
	
	/**
	 * 创建项目已办信息表单
	 * @param xmid
	 * @param me
	 * @return
	 */
	public String CreatePmTaskHistoryInfoHtml(String xmid,UserContext me){
		return null;
	}
	
	/**
	 * 创建项目干系人信息表单
	 * @param xmid
	 * @param me
	 * @return
	 */
	public String CreatePmGxrInfoHtml(String xmid,UserContext me){
		return null;
	}
	
	/**
	 * 创建项目文档信息表单
	 * @param xmid
	 * @param me
	 * @return
	 */
	public String CreatePmFileInfoHtml(String xmid,UserContext me){
		return null;
	}
	
	public static String objToString(Object obj) {
		return obj == null || obj.equals(null) ? "" : obj.toString();
	}

}
