package com.actionsoft.apps.gyxmgl.controller;

import com.actionsoft.apps.gyxmgl.web.GyxmTaskInfoWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

/**
 * @author胥强
 */
@Controller
public class GyxmTaskInfoController {

	/**
	 * 查询项目待办
	 * @param uc
	 * @param parentId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.todoprocess")
	public String getTodoProcessJson(UserContext uc, int curPage,
			int rowsPerPage,String xmid,String keyWord) {
		GyxmTaskInfoWeb mgrWeb = new GyxmTaskInfoWeb(uc);
		return mgrWeb.getTodoProcessJson(uc, curPage, rowsPerPage,xmid,keyWord);
	}
	
	/**
	 * 查询项目已办
	 * @param uc
	 * @param parentId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.havetodoprocess")
	public String getHaveTodoProcessJson(UserContext uc, int curPage,
			int rowsPerPage,String xmid) {
		GyxmTaskInfoWeb mgrWeb = new GyxmTaskInfoWeb(uc);
		return mgrWeb.getHaveTodoProcessJson(uc, curPage, rowsPerPage,xmid);
	}
	/**
	 * 查询项目文档
	 * @param uc
	 * @param parentId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.projectfile")
	public String getProjectFileJson(String sid,UserContext uc, int curPage,
			int rowsPerPage,String xmid) {
		GyxmTaskInfoWeb mgrWeb = new GyxmTaskInfoWeb(uc);
		String uid_l = uc.getUID();
		return mgrWeb.getProjectFileJson(uc,sid,uid_l, curPage, rowsPerPage,xmid);
	}
	/**
	 * 查询项目团队
	 * @param uc
	 * @param parentId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.gyxmgl.projectteam")
	public String getProjectTeamJson(UserContext uc, int curPage,
			int rowsPerPage,String xmid) {
		GyxmTaskInfoWeb mgrWeb = new GyxmTaskInfoWeb(uc);
		String uid_l = uc.getUID();
		return mgrWeb.getProjectTeamJson(uid_l, curPage, rowsPerPage,xmid);
	}
}
