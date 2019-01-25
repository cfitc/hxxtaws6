package com.actionsoft.apps.gyxmgl.controller;

import java.util.HashMap;

import com.actionsoft.apps.gyxmgl.web.GyPmProcessMapWeb;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

@Controller
public class GyPMProcessMapController {
	/**
	 * 固有项目流程地图页面展现
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.processMap")
	public String getGyPMProcessMap(){
		GyPmProcessMapWeb gyMap = new GyPmProcessMapWeb();
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("gyMap", gyMap.CreateGyPmProcessMap());
		return HtmlPageTemplate.merge("com.actionsoft.apps.gyxmgl","gyProcessMap.htm", m);
	}
}
