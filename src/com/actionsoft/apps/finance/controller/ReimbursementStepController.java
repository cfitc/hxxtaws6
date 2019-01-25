package com.actionsoft.apps.finance.controller;

import java.util.HashMap;

import com.actionsoft.apps.finance.web.ReimbursementStepWeb;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

//获取节点审批人姓名
public class ReimbursementStepController {

	
	@Mapping("com.actionsoft.apps.finance.step")
	public String goToInitialisePage(String sid,UserContext me ,String bindid ){
		HashMap m = new HashMap();
		m.put("sid", sid);
		ReimbursementStepWeb  reimbursementStepWeb = new ReimbursementStepWeb();
		return reimbursementStepWeb.ReimbursementStep(me,bindid);
	
	}
}
