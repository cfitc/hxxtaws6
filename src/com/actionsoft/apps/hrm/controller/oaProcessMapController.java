package com.actionsoft.apps.hrm.controller;

import java.util.HashMap;

import com.actionsoft.apps.hrm.web.OaProcessMapWeb;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

@Controller
public class oaProcessMapController {
	/**
	 * OA流程地图页面展现
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.oaProcessMap")
	public String getoaProcessMap(String sid,UserContext me,String lcmc){
		OaProcessMapWeb gyMap = new OaProcessMapWeb();
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("sid", sid);
		m.put("oaMap", gyMap.createOaProcessWeb(me,lcmc));
		return HtmlPageTemplate.merge("com.actionsoft.apps.gyxmgl","oaProcessMap.htm", m);
	}
}
