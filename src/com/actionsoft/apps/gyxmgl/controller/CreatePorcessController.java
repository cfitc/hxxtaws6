package com.actionsoft.apps.gyxmgl.controller;

import com.actionsoft.apps.gyxmgl.web.CreateProcessWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

@Controller 
public class CreatePorcessController {
	
	/**
	 * 启动流程项目类
	 * @param sid
	 * @param me
	 * @param fundId
	 * @param processDefId
	 * @param operation
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.createProcess")
	public String createProcess(String sid, UserContext me,String xmid,String xmmc,String lcuuid,String lcmc) {
		return new CreateProcessWeb(me).createProcess(xmid, xmmc, lcuuid, lcmc);
	}

}
