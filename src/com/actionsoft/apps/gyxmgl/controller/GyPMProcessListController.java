package com.actionsoft.apps.gyxmgl.controller;

import java.util.HashMap;

import com.actionsoft.apps.gyxmgl.web.GyPMProcessListWeb;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

@Controller
public class GyPMProcessListController {
	/**
	 * 固有项目流程列表
	 *<p>Title: GyPMProcessListController.java<／p>
	 *<p>Description: <／p>
	 * @author 陈昱
	 * 2017年10月11日
	 * @param me
	 * @return
	 */       
	@Mapping("com.actionsoft.apps.gyxmgl.processList")
	public String GetGyPMProcessList(UserContext me){
		GyPMProcessListWeb gyprocessList = new GyPMProcessListWeb();
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("WebForGyxmWorkFlowView", gyprocessList.createGyPMProcessList());
		return HtmlPageTemplate.merge("com.actionsoft.apps.gyxmgl","WebForGyxmWorkFlowView.htm", m);
	}
}
