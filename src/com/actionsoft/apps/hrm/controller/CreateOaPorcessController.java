package com.actionsoft.apps.hrm.controller;

import com.actionsoft.apps.hrm.web.CreateOaProcessWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

@Controller 
public class CreateOaPorcessController {
	
	/**
	 * 启动流程项目类
	 * @param sid
	 * @param me
	 * @param fundId
	 * @param processDefId
	 * @param operation
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.createOaProcess")
	public String createProcess(UserContext me,String lcuuid,String lcmc) {
		return new CreateOaProcessWeb(me).createProcess(lcuuid, lcmc);
	}

}
