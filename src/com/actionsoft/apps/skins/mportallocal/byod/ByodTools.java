package com.actionsoft.apps.skins.mportallocal.byod;

import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.skins.mportallocal.constant.MportalSkinsConstant;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;

public class ByodTools {
	
	public  ResponseObject getBarCodeHtml(UserContext uc) {
		ResponseObject ro = ResponseObject.newOkResponse();
		String sid = uc.getSessionId();
		AppAPI appAPI = SDK.getAppAPI();
		AppContext byodApp = appAPI.getAppContext(MportalSkinsConstant.APP_BYOD);
		// 服务地址
		String aslp = "aslp://com.actionsoft.apps.byod.helper/getBarCodeHtml";
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("sid", sid);
			ro = appAPI.callASLP(byodApp, aslp, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ro;
	}
}
