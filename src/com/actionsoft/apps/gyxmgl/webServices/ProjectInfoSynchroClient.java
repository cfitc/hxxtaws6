package com.actionsoft.apps.gyxmgl.webServices;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.actionsoft.apps.gyxmgl.util.GlobleParams;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.cc.SOAPAPI;
import com.aws.bcf.webservers.ProjectInfoService;

public class ProjectInfoSynchroClient {

	/**
	 * 同步客户信息到5平台
	 * 
	 * @return
	 */
	public static String synchroXmxx(String bindid) {

		// 客户信息查询
		BO kh = SDK.getBOAPI().query("BO_ACT_PM_KHXXB").detailByBindId(bindid);
		// 查询股东信息
		List<BO> list_gd = SDK.getBOAPI().query("BO_ACT_PM_KHGDXX")
				.bindId(bindid).list();
		// 查询银行账户信息
		List<BO> list_zh = SDK.getBOAPI().query("BO_ACT_PM_KHYHZHB")
				.bindId(bindid).list();
		String khxx = kh.toJson();
		JSONArray gdJsonArray = new JSONArray();
		for (BO gdbo : list_gd) {
			gdJsonArray.add(gdbo.toJson());
		}
		JSONArray yhzhJsonArray = new JSONArray();
		for (BO yhzhbo : list_zh) {
			yhzhJsonArray.add(yhzhbo.toJson());
		}
		JSONObject jo = new JSONObject();
		jo.put("KHXX", khxx);
		jo.put("GDXX", gdJsonArray.toString());
		jo.put("YHZHXX", yhzhJsonArray.toString());
		System.out.print(jo.toString());
		SOAPAPI soapApi = SDK.getCCAPI().getSOAPAPI("18a9355d-4ff3-4085-8de7-1bbd6e512afc");
		ProjectInfoService projectInfoServices = soapApi.getPort(ProjectInfoService.class);
		String result = projectInfoServices.saveOrUpdateCustomersInfo(jo.toString());
		JSONObject jsonObject = JSONObject.fromObject(result);
		if (!jsonObject.get("DATA").equals("")) {
			JSONObject dataObject = (JSONObject) jsonObject.get("DATA");
			String KHXXID = dataObject.getString("KHXXID");
			DBSql.update("update BO_ACT_PM_KHXXB set KHXXID='"+KHXXID+"' where bindid='"+bindid+"'");
			System.out.println(result);
		}
		return null;
	}

	/**
	 * 同步项目信息到5平台
	 * 
	 * @param GyxmxxBo
	 * @return
	 */
	public static String xmxxToPmsFive(String bindId) {
		BO bo=SDK.getBOAPI().query(GlobleParams.BO_ACT_PM_GYXMXXB_TABLE).detailByBindId(bindId);
		bo.set("XMID", bo.getString("YXMID"));
		bo.set("JYDSID", bo.getString("YJYDSID"));
		bo.set("XMJDID", bo.getString("YJDID"));
		SOAPAPI soapApi = SDK.getCCAPI().getSOAPAPI(
				"18a9355d-4ff3-4085-8de7-1bbd6e512afc");
		System.out.println(bo.toJson());
		ProjectInfoService projectInfoServices = soapApi.getPort(ProjectInfoService.class);
		String result = projectInfoServices.saveorUpdateGyProjectInfo(bo.toJson());
		JSONObject jsonObject = JSONObject.fromObject(result);
		if (!jsonObject.get("DATA").equals("")) {

			JSONObject dataObject = (JSONObject) jsonObject.get("DATA");
			String xmid = dataObject.getString("XMID");
			DBSql.update("update BO_ACT_PM_GYXMXXB set yxmid='"+xmid+"' where bindid='"+bindId+"'");
		}

		/*
		 * ProjectInfoService ps = sp.getPort(ProjectInfoService.class); String
		 * result =
		 * ps.getProjectInfo("{\"xmlx\":\"信托项目\",\"xmmc\":\"\",\"xmid\":\"\"}");
		 * System.out.println(result);
		 */
		return null;
	}

	/**
	 * 同步机构信息到5平台
	 * 
	 * @param GyxmxxBo
	 * @return
	 */
	public static String JgToPmsFive(String bindid) {
		// 机构信息查询
		BO JG = SDK.getBOAPI().query("BO_ACT_PM_KHJGWH").detailByBindId(bindid);
		// 查询联系人信息
		List<BO> list_lxr = SDK.getBOAPI().query("BO_ACT_PM_KHJGLXR")
				.bindId(bindid).list();
		String jgxx=JG.toJson();
		JSONArray lxrJsonArray = new JSONArray();
		for (BO gdbo : list_lxr) {
			lxrJsonArray.add(gdbo.toJson());
		}
		JSONObject jo = new JSONObject();
		jo.put("JGXX", jgxx);
		jo.put("LXRXX", lxrJsonArray.toString());
		System.out.print(jo.toString());
		SOAPAPI soapApi = SDK.getCCAPI().getSOAPAPI(
				"18a9355d-4ff3-4085-8de7-1bbd6e512afc");
		ProjectInfoService projectInfoServices = soapApi.getPort(ProjectInfoService.class);
		String result = projectInfoServices.saveorUpdateOrgInfo(jo.toString());
		System.out.print(result);
		System.out.print(result);
		JSONObject jsonObject = JSONObject.fromObject(result);
		if (!jsonObject.get("DATA").equals("")) {
			JSONObject dataObject = (JSONObject) jsonObject.get("DATA");
			String JGXXID = dataObject.getString("JGXXID");
			DBSql.update("update BO_ACT_PM_KHJGWH set JGXXID='"+JGXXID+"' where bindid='"+bindid+"'");
		}
		return "";
	}
	/**
	 * 同步账户信息到5平台
	 * 
	 * @param GyxmxxBo
	 * @return
	 */
	public static String ZhToPmsFive(String bindid) {
		// 账户信息查询
		BO ZH = SDK.getBOAPI().query("BO_ACT_PM_GYXMZHXXB").detailByBindId(bindid);
		String zhxx=ZH.toJson();
		JSONObject jo = new JSONObject();
		jo.put("ZHXX", zhxx);
		System.out.print(jo.toString());
		SOAPAPI soapApi = SDK.getCCAPI().getSOAPAPI(
				"18a9355d-4ff3-4085-8de7-1bbd6e512afc");
		ProjectInfoService projectInfoServices = soapApi.getPort(ProjectInfoService.class);
		String result = projectInfoServices.saveOrUpdateGyAccountInfo(jo.toString());
		System.out.print(result);
		JSONObject jsonObject = JSONObject.fromObject(result);
		if (!jsonObject.get("DATA").equals("")) {
			JSONObject dataObject = (JSONObject) jsonObject.get("DATA");
			String ZHXXID = dataObject.getString("ZHXXID");
			DBSql.update("update BO_ACT_PM_GYXMZHXXB set ZHXXID='"+ZHXXID+"' where bindid='"+bindid+"'");
		}

		return "";
	}

}
