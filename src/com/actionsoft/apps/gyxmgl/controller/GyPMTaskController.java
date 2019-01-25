package com.actionsoft.apps.gyxmgl.controller;

import java.util.HashMap;

import com.actionsoft.apps.gyxmgl.web.GyPmTaskWeb;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

@Controller
public class GyPMTaskController {
	/**
	 * 初始化项目事务管理卡页面
	 * @param xmid
	 * @param sid
	 * @param me
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.gypmTask")
	public String AjaxSocketCmdForGyPmTask(String xmid,String sid,UserContext me){
		GyPmTaskWeb gyTaskWeb = new GyPmTaskWeb();
		//项目名称及进度条
		String pmProgress = gyTaskWeb.CreatePmProgressInfoHtml(xmid, me);
		//项目基本信息
		String pmBaseInfo = gyTaskWeb.CreatePmBaseIfnoHtml(xmid, me);
		//项目可发起流程信息
		String pmTaskStart = gyTaskWeb.CreatePmTaskStartHtml(xmid, me);
		//项目待办
		//String pmTask = gyTaskWeb.CreatePmTaskInfoHtml(xmid, me);
		
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("sid", sid);
		m.put("xmid", xmid);
		m.put("pmProgress", pmProgress);
		m.put("pmBaseInfo", pmBaseInfo);
		m.put("pmTaskStart", pmTaskStart);
		return HtmlPageTemplate.merge("com.actionsoft.apps.gyxmgl","gyxmTaskInfo.htm", m);
	}
	/**
	 * 初始化项目看板页面
	 * @param xmid
	 * @param sid
	 * @param me
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.gypmboard")
	public String AjaxSocketCmdForGyPmboard(String xmid,String sid,UserContext me,String keyword){
		GyPmTaskWeb gyTaskWeb = new GyPmTaskWeb();
		//项目名称及进度条
		String pmProgress = gyTaskWeb.CreatePmProgressInfoHtml(xmid, me);
		//项目基本信息
		String pmBaseInfo = gyTaskWeb.CreatePmBaseIfnoHtml(xmid, me);
		//项目可发起流程信息
		String pmTaskStart = gyTaskWeb.CreatePmTaskStartHtml(xmid, me);
		//项目详细信息
		String pmcompleteinfo = gyTaskWeb.CreatePmcompleteIfnoHtml(xmid, me);
		//项目待办
		//String pmTask = gyTaskWeb.CreatePmTaskInfoHtml(xmid, me);
		
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("sid", sid);
		m.put("xmid", xmid);
		m.put("pmProgress", pmProgress);
		m.put("pmBaseInfo", pmBaseInfo);
		m.put("pmTaskStart", pmTaskStart);
		m.put("pmcompleteinfo", pmcompleteinfo);
		return HtmlPageTemplate.merge("com.actionsoft.apps.gyxmgl","gyxmTaskboard.htm", m);
	}
}
