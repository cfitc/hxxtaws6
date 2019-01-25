package com.actionsoft.apps.gyxmgl.controller;

import java.util.HashMap;

import com.actionsoft.apps.gyxmgl.web.GyPMAttatchmentWeb;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

@Controller
public class GyPMAttatchmentController {
	/**
	 * 固有项目附件信息
	 *<p>Title: GyPMAttatchmentController.java<／p>
	 *<p>Description: <／p>
	 * @author 王明
	 * 2017年10月12日
	 */       
	@Mapping("com.actionsoft.apps.gyxmgl.GyPMAttatchment")
	public String GetGyPMAttatchment(String xmid,String inputvalue,UserContext me){
		GyPMAttatchmentWeb gyattatchment = new GyPMAttatchmentWeb();
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("WebForGyxmPMView", gyattatchment.GyPMMassage(xmid,me));
		m.put("WebForGyxmfjView", gyattatchment.GyPMAttachment(xmid,inputvalue,me));
		return HtmlPageTemplate.merge("com.actionsoft.apps.gyxmgl","WebForGyxmfjView.html", m);
	}
}
