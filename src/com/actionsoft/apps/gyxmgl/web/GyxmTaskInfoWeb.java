package com.actionsoft.apps.gyxmgl.web;

import com.actionsoft.apps.gyxmgl.service.GyxmTaskInfoService;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.fs.DCContext;
import com.actionsoft.bpms.util.UtilURL;
import com.alibaba.fastjson.JSONArray;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.formfile.model.delegate.FormFile;
import com.actionsoft.sdk.local.SDK;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class GyxmTaskInfoWeb extends ActionWeb{
	
	public GyxmTaskInfoWeb(UserContext me) {
		super(me);
	}
	
	/**
	 * 查询待办任务
	 * @param uc
	 * @param parentId
	 * @return
	 */
	public String getTodoProcessJson(UserContext uc,int curPage, int rowsPerPage,String xmid,String keyWord){
		ResponseObject responseObject = ResponseObject.newOkResponse();
		JSONArray raiseJA = new JSONArray();
		GyxmTaskInfoService gyxmTaskInfoService = new GyxmTaskInfoService();
		List<RowMap> listsMap = gyxmTaskInfoService.queryGyxmTaskInfo(curPage, rowsPerPage,xmid,keyWord);
		for(RowMap m : listsMap){
			JSONObject raiseJO = new JSONObject();
			raiseJO.put("lcmc", isNull(m.get("lcmc")));
			raiseJO.put("lcbt", isNull(m.get("lcbt")));
			raiseJO.put("processinstid", isNull(m.get("processinstid")));
			raiseJO.put("owner", isNull(m.get("owner")));
			raiseJO.put("target", isNull(m.get("target")));
			raiseJO.put("begintime", isNull(m.get("begintime")));
			//如果是自己的待办直接打开待办，不是则打开已办页面
			String url = "";
			if(uc.getUID().equals(m.get("target"))){
				url = "./w?sid=" + uc.getSessionId() + "&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN" + "&processInstId=" + m.get("processinstid") + "&taskInstId=" + m.get("taskId") + "&openState=1" ;
			}else{
				url = "./w?sid=" + uc.getSessionId() + "&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN" + "&processInstId=" + m.get("processinstid") + "&taskInstId=" + m.get("taskId") + "&openState=2" ;
			}
			//流程跟踪url
			String urlTrack = "./w?sid="+uc.getSessionId()+"&cmd=CLIENT_BPM_FORM_TRACK_OPEN" + "&processInstId=" + m.get("processinstid") +"&sourceApp=prm";
			raiseJO.put("urlTrack",urlTrack);
			raiseJO.put("url",url);
			raiseJA.add(raiseJO);
		}
		List<RowMap> listsMap_five = gyxmTaskInfoService.queryGyxmTaskInfo_five(xmid,keyWord);
		for (RowMap rowMap : listsMap_five) {
			JSONObject raiseJO = new JSONObject();
			raiseJO.put("lcmc", isNull(rowMap.get("lcmc")));
			raiseJO.put("lcbt", isNull(rowMap.get("lcbt")));
			raiseJO.put("processinstid", isNull(rowMap.get("bind_id")));
			raiseJO.put("owner", isNull(rowMap.get("owner")));
			raiseJO.put("target", isNull(rowMap.get("target")));
			raiseJO.put("begintime", isNull(rowMap.get("begintime")));
			String bindid=rowMap.getString("bind_id");
			String taskid=SDK.getCCAPI().getRDSAPI("e0ee6447-f01b-4079-b3a3-89150eae6f64").getString("select id from WF_TASK where BIND_ID='"+bindid+"'");
			//如果是自己的待办直接打开待办，不是则打开已办页面
			String url = "";
			if(uc.getUID().equals(rowMap.get("target"))){
				url = "./w?sid=" + uc.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=%2Fworkflow%2Flogin.wf%3Fsid%3D%23sid5%26cmd%3DWorkFlow_Execute_Worklist_File_Open" + "%26task_id%3D" + taskid + "%26openstate%3D1%26id%3D"+ rowMap.get("bind_id");
				}else{
				url = "./w?sid=" + uc.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=%2Fworkflow%2Flogin.wf%3Fsid%3D%23sid5%26cmd%3DWorkFlow_Execute_Worklist_File_Open" + "%26task_id%3D0%26openstate%3D8%26id%3D"+ rowMap.get("bind_id");
			}
			//流程跟踪url
			String urlTrack = "./w?sid=" + uc.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=%2Fworkflow%2Flogin.wf%3Fsid%3D%23sid5%26cmd%3DWorkFlow_Monitor_Track_List" + "%26task_id%3D0%26openstate%3D8%26id%3D"+ rowMap.get("bind_id");
			raiseJO.put("urlTrack",urlTrack);
			raiseJO.put("url",url);
			raiseJA.add(raiseJO);
		}

		//增加5平台的待办查询
		JSONObject dataJson = new JSONObject();
		dataJson.put("data", raiseJA);
		dataJson.put("totalRecords", Long.valueOf(gyxmTaskInfoService.totalRecordsSchema));
		dataJson.put("curPage", Integer.valueOf(curPage));
		responseObject.setData(dataJson.toString());
		return responseObject.toString();
	}
	
	/**
	 * 查询已办任务
	 * @param uc
	 * @param parentId
	 * @return
	 */
	public String getHaveTodoProcessJson(UserContext uc,int curPage, int rowsPerPage,String xmid){
		ResponseObject responseObject = ResponseObject.newOkResponse();
		JSONArray raiseJA = new JSONArray();
		GyxmTaskInfoService gyxmTaskInfoService = new GyxmTaskInfoService();
		List<RowMap> listsMap = gyxmTaskInfoService.queryGyxmTaskInfoHavetodo(curPage, rowsPerPage,xmid);
		for(RowMap m : listsMap){
			JSONObject raiseJO = new JSONObject();
			raiseJO.put("xmjdmc", isNull(m.get("xmjdmc")));
			raiseJO.put("lcfl", isNull(m.get("lcfl")));
			raiseJO.put("lcmc", isNull(m.get("lcmc")));
			raiseJO.put("lcbt", isNull(m.get("lcbt")));
			raiseJO.put("starttime", isNull(m.get("starttime")));
			raiseJO.put("endtime", isNull(m.get("endtime")));
			raiseJO.put("processinstid", isNull(m.get("id")));
			String url  = "./w?sid=" + uc.getSessionId() + "&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN" + "&processInstId=" + m.get("processinstid") + "&taskInstId=" + m.get("taskId") + "&openState=2" ;
			raiseJO.put("url", url);
			String urlTrack = "./w?sid="+uc.getSessionId()+"&cmd=CLIENT_BPM_FORM_TRACK_OPEN" + "&processInstId=" + m.get("processinstid") +"&sourceApp=prm";
			raiseJO.put("urlTrack",urlTrack);
			raiseJA.add(raiseJO);
		}
		List<RowMap> listsMapf = gyxmTaskInfoService.queryGyxmTaskInfoHavetodo_five(xmid);
		for(RowMap m : listsMapf){
			JSONObject raiseJO = new JSONObject();
			raiseJO.put("xmjdmc", isNull(m.get("xmjdmc")));
			raiseJO.put("lcfl", isNull(m.get("lcfl")));
			raiseJO.put("lcmc", isNull(m.get("lcmc")));
			raiseJO.put("lcbt", isNull(m.get("lcbt")));
			raiseJO.put("starttime", isNull(m.get("begintime")));
			raiseJO.put("endtime", isNull(m.get("endtime")));
			raiseJO.put("processinstid", isNull(m.get("id")));
			String url = "./w?sid=" + uc.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=%2Fworkflow%2Flogin.wf%3Fsid%3D%23sid5%26cmd%3DWorkFlow_Execute_Worklist_File_Open" + "%26task_id%3D0%26openstate%3D8%26id%3D"+ m.get("bind_id");
			raiseJO.put("url", url);
			String urlTrack = "./w?sid=" + uc.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=%2Fworkflow%2Flogin.wf%3Fsid%3D%23sid5%26cmd%3DWorkFlow_Monitor_Track_List" + "%26task_id%3D0%26openstate%3D8%26id%3D"+ m.get("bind_id");
			raiseJO.put("urlTrack",urlTrack);
			raiseJA.add(raiseJO);
		}
		JSONObject dataJson = new JSONObject();
		dataJson.put("data", raiseJA);
		dataJson.put("totalRecords", Long.valueOf(gyxmTaskInfoService.totalRecordsSchema));
		dataJson.put("curPage", Integer.valueOf(curPage));
		responseObject.setData(dataJson.toString());
		return responseObject.toString();
	}
	
	/**
	 * 查询项目文档
	 * @param uc
	 * @param parentId
	 * @return
	 */
	public String getProjectFileJson(UserContext uc,String sid,String uid_l,int curPage, int rowsPerPage,String xmid){
		ResponseObject responseObject = ResponseObject.newOkResponse();
		JSONArray raiseJA = new JSONArray();
		GyxmTaskInfoService gyxmTaskInfoService = new GyxmTaskInfoService();
		List<RowMap> listsMap = gyxmTaskInfoService.queryGyxmTaskInfoProjectFile(curPage, rowsPerPage,xmid);
		for(RowMap m : listsMap){
			JSONObject raiseJO = new JSONObject();
			raiseJO.put("XMJDMC", isNull(m.get("XMJDMC")));//项目阶段
			raiseJO.put("LCMC", isNull(m.get("LCMC")));//流程名称
			raiseJO.put("WDLB", isNull(m.get("WDLB")));//文件类别
			raiseJO.put("DZWJ", isNull(m.get("DZWJ")));//电子文件
			raiseJO.put("WJZT", isNull(m.get("WJZT")));//文件状态
			raiseJO.put("SCR", isNull(m.get("SCR")));//上传人
			raiseJO.put("SCSJ", isNull(m.get("SCSJ")));//上传时间
			String boid = (String) isNull(m.get("ID"));
			List<FormFile> attfiles = SDK.getBOAPI().getFiles(boid, "DZWJ");// FUJIAN表中附件属性名称
			String path = null;
			for (FormFile formFilea : attfiles) {
					DCContext dcContext = SDK
							.getBOAPI()
							.getFileDCContext(formFilea);
					path = dcContext.getDownloadURL();
			}
			if(path!=null){
				path = path.replace("sid=null", "sid="+ sid);
			}
			raiseJO.put("path", path);//附件链接
			raiseJA.add(raiseJO);
		}
		List<RowMap> listsMapfi = gyxmTaskInfoService.queryGyxmTaskInfoProjectFile_five(curPage, rowsPerPage,xmid);
		for(RowMap m : listsMapfi){
			JSONObject raiseJO = new JSONObject();
			raiseJO.put("XMJDMC", isNull(m.get("XMJDMC")));//项目阶段
			raiseJO.put("LCMC", isNull(m.get("LCMC")));//流程名称
			raiseJO.put("WDLB", isNull(m.get("WDLB")));//文件类别
			raiseJO.put("DZWJ", isNull(m.get("DZWJ")));//电子文件
			raiseJO.put("WJZT", isNull(m.get("WJZT")));//文件状态
			raiseJO.put("SCR", isNull(m.get("SCR")));//上传人
			raiseJO.put("SCSJ", isNull(m.get("SCSJ")));//上传时间
			String boid = (String) isNull(m.get("ID"));
			List<FormFile> attfiles = SDK.getBOAPI().getFiles(boid, "DZWJ");// FUJIAN表中附件属性名称
			String flog2=SDK.getCCAPI().getRDSAPI("e0ee6447-f01b-4079-b3a3-89150eae6f64").getString("select b.id from sys_business_metadata a,sys_business_metadata_map b where a.id=b.metadata_id and b.FIELD_NAME='DZWJ' and a.entity_name='BO_BCF_PM_ATTACH'");
			String filenamecode="";
			try {
				filenamecode=URLEncoder.encode((String) m.get("DZWJ"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String path = "./w?sid=" + uc.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=%2Fworkflow%2Fdownfile%2Ewf%3Fsid%3D%23sid5%26flag1%3D"+m.get("id")+"%26flag2%3D"+flog2+"%26filename%3D"+filenamecode+"%26rootDir%3DFormFile";
//			String path="./w?sid=" + uc.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=" + UtilURL.URLEncode(new StringBuilder("/Fworkflow/downfile.wf?sid=#sid&flag1="+m.get("id")+"&flag2="+flog2+"&filename="+filenamecode+"&rootDir%3DFormFile"));
			raiseJO.put("path", path);//附件链接
			raiseJA.add(raiseJO);
		}
		JSONObject dataJson = new JSONObject();
		dataJson.put("data", raiseJA);
		dataJson.put("totalRecords", Long.valueOf(gyxmTaskInfoService.totalRecordsSchema));
		dataJson.put("curPage", Integer.valueOf(curPage));
		responseObject.setData(dataJson.toString());
		return responseObject.toString();
	}
	
	/**
	 * 查询项目团队
	 * @param uc
	 * @param parentId
	 * @return
	 */
	public String getProjectTeamJson(String uid_l,int curPage, int rowsPerPage,String xmid){
		ResponseObject responseObject = ResponseObject.newOkResponse();
		JSONArray raiseJA = new JSONArray();
		GyxmTaskInfoService gyxmTaskInfoService = new GyxmTaskInfoService();
		List<RowMap> listsMap = gyxmTaskInfoService.queryGyxmTaskInfoProjectTeam(curPage, rowsPerPage,xmid);
		for(RowMap m : listsMap){
			JSONObject raiseJO = new JSONObject();
			raiseJO.put("gxrmz", isNull(m.get("gxrmz")));//姓名
			raiseJO.put("gxrjs", isNull(m.get("gxrjs")));//项目角色
			raiseJO.put("updatedate", isNull(m.get("updatedate")));//日期
			raiseJO.put("gxrdw", isNull(m.get("gxrdw")));//单位
			raiseJO.put("gxrdh", isNull(m.get("gxrdh")));//电话
			raiseJO.put("gxrsj", isNull(m.get("gxrsj")));//移动电话
			raiseJO.put("gxryx", isNull(m.get("gxryx")));//邮箱
			raiseJA.add(raiseJO);
		}
		JSONObject dataJson = new JSONObject();
		dataJson.put("data", raiseJA);
		dataJson.put("totalRecords", Long.valueOf(gyxmTaskInfoService.totalRecordsSchema));
		dataJson.put("curPage", Integer.valueOf(curPage));
		responseObject.setData(dataJson.toString());
		return responseObject.toString();
	}
	
	private Object isNull(Object obj) {
		return obj == null || obj.equals(null) ? "" : obj.toString();
	}
}
