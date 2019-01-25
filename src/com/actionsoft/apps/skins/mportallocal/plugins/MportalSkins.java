package com.actionsoft.apps.skins.mportallocal.plugins;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.actionsoft.apps.AppsConst;
import com.actionsoft.apps.lifecycle.api.AppsAPIManager;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.skins.mportallocal.constant.MportalSkinsConstant;
import com.actionsoft.apps.skins.mportallocal.dao.MportalNavDao;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.portal.pub.web.PublicPortalWeb;
import com.actionsoft.bpms.commons.portal.skins.AbstPortalSkins;
import com.actionsoft.bpms.commons.portal.skins.PortalSkinsInterface;
import com.actionsoft.bpms.commons.security.mgtgrade.util.GradeSecurityUtil;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.Passwd;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.conf.portal.AWSPortalConf;
import com.actionsoft.bpms.server.fs.DCContext;
import com.actionsoft.bpms.server.fs.impl.PhotoProcessor;
import com.actionsoft.bpms.util.ClassReflect;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.i18n.I18nRes;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;
import com.actionsoft.sdk.local.api.PortalAPI;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MportalSkins extends AbstPortalSkins implements PortalSkinsInterface {

	/**
	 * 登录成功后首页面
	 */
	@Override
	public String getHomePage(UserContext me) {

		AppAPI appApi = SDK.getAppAPI();
		PortalAPI portalApi = SDK.getPortalAPI();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("sid", me.getSessionId());
		result.put("windowTitle",
				appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, MportalSkinsConstant.WINDOW_TITLE)); // 窗口标题
		// 特定的背景图片
		String specificBackGround = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
				MportalSkinsConstant.METRO_SPECIFIC_BACKGROUND);
		String metroBackGround = "";
		// 修改背景图片的提示信息，若为空值，不提供该功能
		String changeBackGround = "";
		// 如果未指定特定的背景图片，采用常规背景图片
		if (UtilString.isEmptyByTrim(specificBackGround)) {
			// 背景图片
			metroBackGround = portalApi.getUserProfileItem(MportalSkinsConstant.APP_MPORTALSKINS, me.getUID(),
					MportalSkinsConstant.METRO_BACKGROUND, MportalSkinsConstant.METRO_BACKGROUND);
			if (UtilString.isEmptyByTrim(metroBackGround))
				metroBackGround = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
						MportalSkinsConstant.METRO_BACKGROUND);
			if (UtilString.isEmptyByTrim(metroBackGround))
				metroBackGround = MportalSkinsConstant.METRO_BACKGROUND_DFT;

			changeBackGround = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
					MportalSkinsConstant.METRO_CHANGE_BACKGROUND);
		} else {
			// 如果指定了特定的背景图片，即使用该图片，不使用常规的背景图片，同时取消更改背景图片功能
		}
		result.put("specificBackGround", specificBackGround);
		result.put("metroBackGround", metroBackGround);
		result.put("changeBackGround", I18nRes.findValue(MportalSkinsConstant.APP_MPORTALSKINS, changeBackGround));
		// 公司信息
		String companyInfo = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
				MportalSkinsConstant.METRO_COMPANY_INFO);
		result.put("companyInfo", companyInfo);

		// 用户信息：公司名称、部门名称、用户名、姓名
		UserModel userModel = me.getUserModel();
		result.put("companyName", me.getCompanyModel().getName());
		result.put("departmentName", me.getDepartmentModel().getName());
		result.put("uid", userModel.getUID());
		result.put("uniqueId", userModel.getUniqueId());
		result.put("userName", userModel.getUserName());

		result.put("userPhoto", portalApi.getUserPhoto(me, me.getUID()));
		result.put("userPhotoTmp", PhotoProcessor.getTmpPhotoUrl(me, me.getUID()));
		// 求真像
		String isPromptUploadPortraitStr = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
				MportalSkinsConstant.USER_IS_PROMPT_UPLOAD_PORTRAIT);
		boolean isPromptUploadPortrait = false;
		isPromptUploadPortrait = MportalSkinsConstant.SYSTEM_DEFAULT_USER_PHOTO
				.equals(result.get("userPhoto") == null ? "" : result.get("userPhoto").toString());
		result.put("isPromptUploadPortrait", isPromptUploadPortrait);
		// 新用户是否弹出上传头像窗口
		String uploadPortraitPop = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "uploadPortraitPop");
		result.put("uploadPortraitPop", uploadPortraitPop);
		// 用户登录日志记录数
		String userLoginLogCount = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
				MportalSkinsConstant.USER_LOGIN_LOG_COUNT);
		result.put("userLoginLogCount",
				UtilString.isEmptyByTrim(userLoginLogCount) ? MportalSkinsConstant.USER_LOGIN_LOG_COUNT_DFT
						: userLoginLogCount);

		result.put("sysAppId", AppsConst.SYS_APP_PLATFORM);
		AppsAPIManager appsApiManager = AppsAPIManager.getInstance();
		result.put("icon16", appsApiManager.getIcon16URL(MportalSkinsConstant.APP_MPORTALSKINS, me));
		result.put("icon96", appsApiManager.getIcon96URL(MportalSkinsConstant.APP_MPORTALSKINS, me));

		// 子系统功能菜单
		JSONArray navSystemList = portalApi.getNavList(me, null);
		result.put("navSystemList", navSystemList);
		// 首页子系统菜单ID
		result.put("navHomePageId", MportalSkinsConstant.METRO_NAV_HOMEPAGE_ID);
		// 不提供功能的AppId，例如（公众微博）
		result.put("notPresentFuncAppId", appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
				MportalSkinsConstant.METRO_NOT_PRESENT_FUNC_APPID));
		// 公共方法 判断密码修改周期
		try {
			Constructor _cons = null;
			Class[] parameterTypes = { UserContext.class };
			_cons = ClassReflect.getConstructor("com.actionsoft.bpms.commons.portal.pub.web.PublicPortalWeb",
					parameterTypes);
			if (_cons != null) {
				Object[] paras = { me };
				Object o = _cons.newInstance(paras);
				Method loginCheckSecurityMethod = PublicPortalWeb.class.getMethod("loginCheckSecurity", null);
				try {
					Object dparams = loginCheckSecurityMethod.invoke(o, null);
					JSONObject ob = (JSONObject) dparams;
					boolean forceChangePwd = false;
					if ("true".equals(ob.optString("forceChangePwd")))
						forceChangePwd = true;
					boolean isSecurityPwdComplexity = false;
					if ("true".equals(ob.optString("isSecurityPwdComplexity")))
						isSecurityPwdComplexity = true;
					boolean isSecurityPwdChange = false;
					if ("true".equals(ob.optString("isSecurityPwdChange")))
						isSecurityPwdChange = true;
					result.put("isSecurityPwdChange", isSecurityPwdChange);
					result.put("forceChangePwd", forceChangePwd);
					result.put("isSecurityPwdComplexity", isSecurityPwdComplexity);
					result.put("securityMinPwdLength", Integer.valueOf(AWSPortalConf.getSecurityMinPwdLength()));
					result.put("securityMaxPwdLength", Integer.valueOf(AWSPortalConf.getSecurityMaxPwdLength()));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			boolean isSecurityPwdChange = AWSPortalConf.isSecurityPwdChange();
			result.put("isSecurityPwdChange", isSecurityPwdChange);
			// 默认口令验证，是否强制修改默认密码
			Passwd passwd = new Passwd();
			boolean forceChangePwd = false;
			if (isSecurityPwdChange
					&& userModel.getPassword().equals(passwd.deepMD5(AWSPortalConf.getSecurityPwdDefault()))) {
				forceChangePwd = true;
			}
			// 验证口令修改周期
			int securityPwdCycle = AWSPortalConf.getSecurityPwdCycle(); // 口令修改周期，单位（天）
			if (isSecurityPwdChange && securityPwdCycle > 0) { // 允许修改密码，且设置了口令修改周期
				MportalNavDao metroDao = new MportalNavDao();
				// 获取上次登录时间
				long prevLoginTime = metroDao.getPrevLoginTime(userModel.getUID());
				long currentTime = System.currentTimeMillis();
				forceChangePwd = forceChangePwd
						|| (currentTime - prevLoginTime >= (securityPwdCycle * 24 * 60 * 60 * 1000));
			}
			result.put("forceChangePwd", forceChangePwd);
			result.put("isSecurityPwdComplexity", AWSPortalConf.isSecurityPwdComplexity());
			result.put("securityMinPwdLength", AWSPortalConf.getSecurityMinPwdLength());
			result.put("securityMaxPwdLength", AWSPortalConf.getSecurityMaxPwdLength());
		}

		// 需要用户必须完善的信息，默认可以为空，支持email、officeTel、mobile三个选项，多个可用|隔开。
		String requiredUserInfo = portalApi.getRequiredUserInfo(me);
		String[] requiredUserInfoArray = requiredUserInfo.split("[|]");
		boolean requiredEmail = false;
		boolean requiredOfficeTel = false;
		boolean requiredMobile = false;
		for (String reqUserInfo : requiredUserInfoArray) {
			if ("email".equals(reqUserInfo.toLowerCase())) {
				requiredEmail = true;
			} else if ("officetel".equals(reqUserInfo.toLowerCase())) {
				requiredOfficeTel = true;
			} else if ("mobile".equals(reqUserInfo.toLowerCase())) {
				requiredMobile = true;
			}
		}
		result.put("requiredEmail", requiredEmail);
		result.put("requiredOfficeTel", requiredOfficeTel);
		result.put("requiredMobile", requiredMobile);

		// 是否强制上传头像 未修改头像的情况下
		String openThirdNavPanle = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "openThirdNavPanle");
		result.put("openThirdNavPanle", openThirdNavPanle);
		// 左侧导航菜单面板是否默认闭合
		String closeLeftNavPanel = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "closeLeftNavPanel");
		result.put("closeLeftNavPanel", closeLeftNavPanel);
		// 是否启用通知中心App
		boolean isStartNotification = false;
		AppContext appCtx = appApi.getAppContext(MportalSkinsConstant.APP_NOTIFICATION);
		if (appCtx != null) {
			isStartNotification = AppsConst.RUNTIME_STATE_ACTIVE.equals(appCtx.getRuntimeState());
		}
		result.put("isStartNotification", isStartNotification);
		// 是否启用 移动设备
		boolean isStartByodHelper = false;
		AppContext byodHelperCtx = appApi.getAppContext(MportalSkinsConstant.APP_BYOD);
		if (byodHelperCtx != null) {
			isStartByodHelper = AppsConst.RUNTIME_STATE_ACTIVE.equals(byodHelperCtx.getRuntimeState());
		}
		result.put("isStartByodHelper", isStartByodHelper);
		boolean notificationSoundTips = false; // 是否开启消息到达声音提醒
		int notificationMsgLoadFrequency = 60; // notificationMsgLoadFrequency
		if (isStartNotification) {
			JSONObject soundTipJson = portalApi.getUserProfileSchema(MportalSkinsConstant.APP_NOTIFICATION, "admin",
					MportalSkinsConstant.USERPROFILE_SOUND_TIPS);
			if (soundTipJson != null && soundTipJson.get("soundTips") != null) {
				notificationSoundTips = soundTipJson.getBoolean("soundTips");
			}
			if (!UtilString.isEmptyByTrim(appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
					MportalSkinsConstant.USER_IS_PROMPT_UPLOAD_PORTRAIT))) {
				notificationMsgLoadFrequency = Integer.parseInt(appApi.getProperty(
						MportalSkinsConstant.APP_MPORTALSKINS, MportalSkinsConstant.NOTIFICATION_MSG_LOAD_FREQUENCY));
			}
		}
		result.put("notificationSoundTips", notificationSoundTips);
		result.put("notificationMsgLoadFrequency", notificationMsgLoadFrequency);
		JSONObject csslinkObj = portalApi.getUserProfileSchema(MportalSkinsConstant.APP_MPORTALSKINS, me.getUID(),
				MportalSkinsConstant.SKINS_CSS_LINK);
		// 判断用户角色
		Boolean ismanagerUser = (GradeSecurityUtil.isSuperMaster(userModel.getUID())
				|| GradeSecurityUtil.isSystemMaster(userModel.getUID()));
		// 获取用户扩展信息
		String userExtendInfo = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "userExtenInfo");
		String userExtendInfoHtm = "";
		if (!UtilString.isEmpty(userExtendInfo)) {
			JSONArray userarr = JSONArray.fromObject(userExtendInfo);
			for (int i = 0; i < userarr.size(); i++) {
				JSONObject obj = JSONObject.fromObject(userarr.get(i));
				String value = obj.getString("value");
				String regex = "^@.*";
				if (value.matches(regex)) {
					value = SDK.getRuleAPI().executeAtScript(value, me);
				}
				String kk = obj.getString("key");
				String vv = value;
				userExtendInfoHtm += "<li id='userDesc" + i + "' ><span class='skey'><pre>" + obj.getString("key")
						+ value + "</pre></span></li>";
				obj.put("value", value);
			}
		}
		result.put("userExtendInfo", userExtendInfoHtm);
		// 是否改变左侧栏颜色
		String ifChangeLeftNavCol = "false";
		ifChangeLeftNavCol = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "ifChangeLeftNavCol");
		result.put("ifChangeLeftNavCol", ifChangeLeftNavCol);
		// 是否展示邮箱
		String ifShowEmail = "false";
		ifShowEmail = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "ifShowEmail");
		result.put("ifShowEmail", ifShowEmail);
		// 是否展示全文检索
		String ifSupportSearch = "true";
		ifSupportSearch = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "ifSupportSearch");
		result.put("ifSupportSearch", ifSupportSearch);

		// 是否展示'常用'功能
		String isSupportAlwaysUsed = "true";
		isSupportAlwaysUsed = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "isSupportAlwaysUsed");
		result.put("isSupportAlwaysUsed", isSupportAlwaysUsed);

		// 皮肤设置
		String csslink = "../apps/com.actionsoft.apps.skins.mportallocal/css/orange.css"; // 首次登录使用默认皮肤 红色
		String clientSetBg = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "clientSetBg");
		if (!clientSetBg.equals("") && clientSetBg != null && !clientSetBg.equals("\"\"")) {
			csslink = clientSetBg;
		}
		if (csslinkObj.containsKey("csslink")) { // 客户修改的颜色链接
			csslink = csslinkObj.getString("csslink");
		}
		result.put("csslink", csslink);

		// 获取用户自定义的代码
		String snavHtmlOne = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "setNavHtmlOne");
		JSONArray navHtmlOne = new JSONArray();
		if (snavHtmlOne.length() > 0) {
			navHtmlOne = JSONArray.fromObject(snavHtmlOne);
		}
		String snavJsOne = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "setNavJs");
		JSONArray navJsOne = new JSONArray();
		String navJsStr = "";
		if (snavJsOne.length() > 0) {
			navJsOne = JSONArray.fromObject(snavJsOne);
			navJsStr = navJsOne.getString(0);
		}
		// 顶部导航展示个数
		Integer topNavShowNum = Integer
				.valueOf(appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "topNavShowNum"));
		if (topNavShowNum < 1) {
			topNavShowNum = 1;
		}
		result.put("topNavShowNum", topNavShowNum);
		// 是否自动匹配背景色
		String setLogoBg = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "setLogoBg");
		result.put("setLogoBg", setLogoBg);
		String[] filevalues = { "clogo", "greenclogo", "orangeclogo", "ziseclogo", "slanclogo" };
		JSONObject logos = new JSONObject();
		for (int i = 0; i < filevalues.length; i++) {
			DCContext clogodc = new DCContext(me, Plugins.clogoDc, "admin", filevalues[i]);
			File clogodir = new File(clogodc.getPath() + "/clogo.jpg");
			if (clogodir.exists() && clogodir != null) { // 初始化或者未上传新文件时，使用默认图片
				clogodc.setFileName("clogo.jpg");
				logos.put(filevalues[i], clogodc.getDownloadURL());
			} else {
				logos.put(filevalues[i], "../apps/com.actionsoft.apps.skins.mportallocal/img/logoAction-red.png");
			}
		}
		result.put("logos", logos);
		String clientLogo = "../apps/com.actionsoft.apps.skins.mportallocal/img/logoAction-red.png";
		String bottomColor = "#f14c49";
		// 获取客户对应的logo
		String bgColor = csslink.substring(46);
		if (setLogoBg.equals("true")) {
			if (bgColor.equals("blue.css")) { // 深紫色背景色
				clientLogo = logos.getString("slanclogo");
				bottomColor = "#5b84d0";
			} else if (bgColor.equals("green.css")) { // 绿色背景色
				clientLogo = logos.getString("greenclogo");
				bottomColor = "#26a77a";
			} else if (bgColor.equals("moren.css")) { // 蓝色背景色
				clientLogo = logos.getString("clogo");
				bottomColor = "#3597cd";
			} else if (bgColor.equals("zise.css")) { // 紫色背景色
				clientLogo = logos.getString("ziseclogo");
				bottomColor = "#9264d6";
			} else { // 默认背景色
				clientLogo = logos.getString("orangeclogo");
			}
		} else {
			clientLogo = logos.getString("orangeclogo");
			if (bgColor.equals("blue.css")) { // 深紫色背景色
				bottomColor = "#5b84d0";
			} else if (bgColor.equals("green.css")) { // 绿色背景色
				bottomColor = "#26a77a";
			} else if (bgColor.equals("moren.css")) { // 蓝色背景色
				bottomColor = "#3597cd";
			} else if (bgColor.equals("zise.css")) { // 紫色背景色
				bottomColor = "#9264d6";
			}
		}

		// 自定义favicon
		DCContext favicondc = new DCContext(me, Plugins.faviconDc, "admin", "favicons");
		File faviconDir = new File(favicondc.getPath() + "/favicon.ico");
		String favicon = "../apps/com.actionsoft.apps.skins.mportallocal/img/favicon.ico";
		if (faviconDir.exists() && faviconDir != null) { // 初始化或者未上传新文件时，使用默认图片
			favicondc.setFileName("favicon.ico");
			favicon = favicondc.getDownloadURL();
		}
		String notificationShowType = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "notificationShowType");
		String notifyCollectShowNumStr = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
				"notifyCollectShowNum");
		int notifyCollectShowNum = 0;
		if (!UtilString.isEmpty(notifyCollectShowNumStr)) {
			notifyCollectShowNum = Integer.parseInt(notifyCollectShowNumStr);
		}
		result.put("bottomColor", bottomColor);
		result.put("clientLogo", clientLogo);
		result.put("navHtmlOne", navHtmlOne);
		result.put("navJsOne", navJsOne);
		result.put("navJsStr", navJsStr);
		result.put("favicon", favicon);
		result.put("ismanagerUser", ismanagerUser);
		result.put("notificationShowType", notificationShowType); // 显示通知类型
		result.put("notifyCollectShowNum", notifyCollectShowNum); // 通知合并展示，数量控制

		// 是否显示html5通知
		String isHtml5Notification = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "isHtml5Notification");
		result.put("isHtml5Notification", isHtml5Notification);
		// 判断是否拥有访问权限 AC授权
		// 获取门户子系统
		String mhItems = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "mhItems");
		JSONArray mhItemsJson = new JSONArray();
		try {
			mhItemsJson = JSONArray.fromObject(mhItems);
		} catch (Exception e) {
			JSONObject geren = new JSONObject();
			geren.put("name", "个人门户");
			geren.put("icon", "../apps/com.actionsoft.apps.skins.mportallocal/img/mhflag.png");
			geren.put("url", "/");
			geren.put("mhId", "599ab279-645d-4253-82fa-675c8980570b");
			mhItemsJson.add(geren);
		}
		JSONArray acjson = new JSONArray();
		for (int m = 0; m < mhItemsJson.size(); m++) {
			String mhid = JSONObject.fromObject(mhItemsJson.getString(m)).getString("mhId");
			boolean acAuth = SDK.getPermAPI().havingACPermission(me.getUID(), "mportal.plugin.AC", mhid, 0, false);
			if (acAuth) {
				acjson.add(mhItemsJson.get(m));
			}
		}
		result.put("mhItemsJson", acjson);
		// 判断是否开启cms
		String ifOpenCms = "false";
		AppContext cmsApp = appApi.getAppContext("com.actionsoft.apps.cms");
		if (cmsApp != null && SDK.getAppAPI().isActive(cmsApp.getId())) {
			ifOpenCms = "true";
		}

		result.put("ifOpenCms", ifOpenCms);
		String filterParam = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "filterParam");
		result.put("filterParam", filterParam);
		String showNavShadow = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "showNavShadow");
		result.put("showNavShadow", showNavShadow);
		result.put("userPosition", userModel.getPositionName());
		// 一级导航菜单id,点击该菜单，左侧导航关闭
		String firstNavsClickCloseLeft = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
				"firstNavsClickCloseLeft");
		result.put("firstNavsClickCloseLeft", firstNavsClickCloseLeft);
		return HtmlPageTemplate.merge(MportalSkinsConstant.APP_MPORTALSKINS, "mportal.html", result);
	}

	/**
	 * 退出提示页面
	 */
	public String getLogoutPage(UserContext me) {
		PortalAPI portalApi = SDK.getPortalAPI();
		AppAPI appApi = SDK.getAppAPI();
		// 关闭session
		boolean isClosed = portalApi.closeSession(me.getSessionId());
		if (!isClosed) {
			System.err.println(appApi.i18NValue(MportalSkinsConstant.APP_MPORTALSKINS, me, "session关闭异常"));
		}
		// 调转到登出页面
		return HtmlPageTemplate.merge(AppsConst.SYS_APP_PORTAL, "client.user.sys.logout.htm",
				new HashMap<String, Object>());
	}
}
