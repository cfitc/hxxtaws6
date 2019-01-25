package com.actionsoft.apps.skins.mportallocal.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.actionsoft.apps.AppsConst;
import com.actionsoft.apps.skins.mportallocal.constant.MportalSkinsConstant;
import com.actionsoft.apps.skins.mportallocal.model.MportalNavModel;
import com.actionsoft.apps.skins.mportallocal.web.MportalSkinsWeb;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.RequestParams;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;
import com.actionsoft.sdk.local.api.PortalAPI;

import net.sf.json.JSONObject;

@Controller
public class MportalSkinsController {

	/**
	 * 修改密码
	 *
	 * @param oldPassword
	 * @param password
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_update_password")
	public String updatePassword(UserContext me, RequestParams params, String oldPassword, String password) {
		PortalAPI portalApi = SDK.getPortalAPI();
		// 返回由ResponseObject生成的JSON串，result=ok修改成功，否则检查msg(如不允许修改口令、口令不合法)
		return portalApi.changePasswd(me, oldPassword, password);
	}

	/**
	 * 皮肤设置
	 *
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_setLeftBarBackground")
	public String setLeftBarBackground(UserContext me, String csslink) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.setLeftBarBackground(me, csslink);
	}

	/**
	 * 自定义代码导航区域一
	 *
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_setNavHtmlOne")
	public String setNavHtmlOne(UserContext me, String htmls, String jss) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.setNavHtmlOne(me, htmls, jss);
	}

	/**
	 * 加载个人信息
	 *
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_load_userinfo")
	public String loadUserInfo(UserContext me, RequestParams params) {
		PortalAPI portalApi = SDK.getPortalAPI();
		String curUserId = me.getUID();
		ResponseObject result = ResponseObject.newOkResponse();
		JSONObject userInfo = JSONObject.fromObject(portalApi.getUserInfo(me, curUserId));
		result.put("userInfo", userInfo);
		return result.toString();
	}

	/**
	 * 修改用户信息
	 *
	 * @param officePhone
	 * @param outerEmail
	 * @param mobile
	 * @param mobileMsg
	 * @param officeFax
	 * @param familyPhone
	 * @param emergencyContact
	 * @param emergencyContactPhone
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_update_userinfo")
	public String updateUserInfo(UserContext me, RequestParams params, String email, String mobile, String smId,
			String officeFax, String officeTel) {
		PortalAPI portalApi = SDK.getPortalAPI();
		JSONObject data = new JSONObject();
		data.put("email", email);
		data.put("mobile", mobile);
		data.put("officeFax", officeFax);
		data.put("officeTel", officeTel);
		// 修改用户信息
		return portalApi.setUserInfo(me, data);
	}

	/**
	 * 用户登录日志
	 *
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_get_user_login_log")
	public String getUserloginLog(UserContext me, RequestParams params, int logCount) {
		ResponseObject result = ResponseObject.newOkResponse();
		PortalAPI portalApi = SDK.getPortalAPI();
		AppAPI appApi = SDK.getAppAPI();
		// 最新登录日志记录数
		int userLoginLogCount = MportalSkinsConstant.USER_LOGIN_LOG_COUNT_DFT;
		String property = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
				MportalSkinsConstant.USER_LOGIN_LOG_COUNT);
		if (!UtilString.isEmptyByTrim(property))
			userLoginLogCount = Integer.parseInt(property);
		result.put("userLoginLog", portalApi.getUserLoginLog(me, userLoginLogCount));
		return result.toString();
	}

	/**
	 * 获取所有的功能菜单
	 *
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_get_all_sys_nav")
	public String getNavAll(UserContext me, RequestParams params) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.getNavAll();
	}

	/**
	 * 获取Metro的功能菜单
	 *
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_get_mportallocal_nav")
	public String getMetroNav(UserContext me, RequestParams params) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.getMetroNav();
	}

	/**
	 * 保存Metro功能菜单
	 *
	 * @param funcIds
	 * @param rowNumels
	 * @param colNumels
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_save_mportallocal_nav")
	public String saveMetroNav(UserContext me, RequestParams params, String funcIds) {
		String[] funcIdArray = funcIds.split(",");
		String curUserId = me.getUID();
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		List<MportalNavModel> list = new LinkedList<MportalNavModel>();
		int len = funcIdArray.length;
		MportalNavModel model;
		String funcId;
		for (int i = 0; i < len; i++) {
			model = new MportalNavModel();
			try {
				funcId = funcIdArray[i];
			} catch (Exception e) {
				funcId = "";
				e.printStackTrace();
			}
			model.setFunctionId(funcId);
			model.setOrderNo(i + 1);
			model.setCreateUser(curUserId);
			model.setCreateTime(new Timestamp(System.currentTimeMillis()));
			list.add(model);
		}
		return web.saveMetroNav(list);
	}

	@Mapping("com.actionsoft.apps.skins.mportallocal_logout")
	public String logout(UserContext me, String sid) {
		PortalAPI portalApi = SDK.getPortalAPI();
		portalApi.closeSession(sid);
		return HtmlPageTemplate.merge(AppsConst.SYS_APP_PORTAL, "client.user.sys.logout.htm",
				new HashMap<String, Object>());
	}

	/**
	 * 加载门户磁贴
	 *
	 * @param me
	 * @param params
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_load_portlet_notifier")
	public String loadPortletNotifier(UserContext me, RequestParams params, String ids) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.loadPortletNotifier(ids);
	}

	/**
	 * 设置背景图片
	 *
	 * @param me
	 * @param params
	 * @param ids
	 * @param metroBackground
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_set_background")
	public String setBackGround(UserContext me, RequestParams params, String metroBackGround) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.setBackGround(metroBackGround);
	}

	/**
	 * 是否自动匹配背景色
	 *
	 * @param me
	 * @param params
	 * @param ids
	 * @param metroBackground
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_set_setLogoBg")
	public String setLogoBg(UserContext me, String ifbg) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.setLogoBg(ifbg);
	}

	/**
	 * 根据子系统导航ID获取当前用户可访问的目录和功能
	 *
	 * @param me
	 * @param params
	 * @param navSystemId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_get_dir_func")
	public String getNavBySystem(UserContext me, RequestParams params, String navSystemId) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.getNavBySystem(navSystemId);
	}

	/**
	 * 根据子系统导航ID 一次性获取当前用户可访问的目录和功能 all
	 *
	 * @param me
	 * @param params
	 * @param navSystemId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_getAllDirFunc")
	public String getAllDirFunc(UserContext me, RequestParams params, String navSystemList) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.getAllDirFunc(navSystemList);
	}

	/**
	 * 查询全部应用菜单
	 *
	 * @param me
	 * @param params
	 * @param searchValue
	 * @return
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_search_nav")
	public String searchNav(UserContext me, RequestParams params, String searchValue) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.searchNav(searchValue);
	}

	/**
	 * 验证应用是否运行状态
	 *
	 * @param me
	 * @return
	 * @author ZZ
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_checkactive")
	public String checkActive(UserContext me, String appId) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.checkAppActive(appId);
	}

	/**
	 * 添加新门户，修改门户
	 *
	 * @param me
	 * @return
	 * @author ZZ
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_addmhInfo")
	public String addmhInfo(UserContext me, String addmhInfo, String mhProcFlag) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.addMhInfo(me, addmhInfo);
	}

	/**
	 * 删除门户
	 *
	 * @param me
	 * @return
	 * @author ZZ
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_delmhInfo")
	public String delmhInfo(UserContext me, String delmhInfo) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.delMhInfo(me, delmhInfo);
	}

	/**
	 * 门户排序
	 *
	 * @param me
	 * @return
	 * @author ZZ
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_mhOrder")
	public String mhOrder(UserContext me, String iteminfoArr) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.mhOrder(iteminfoArr);
	}

	/**
	 * 设置favicon
	 *
	 * @param me
	 * @return
	 * @author
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_setFavicon")
	public String setFavicon(UserContext me, String fapath, String upflag) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.setFavicon(me, fapath, upflag);
	}

	/**
	 * 查询所有站点cms
	 *
	 * @param me
	 * @return
	 * @author
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_findCms")
	public String findCms(UserContext me) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.findCms(me);
	}

	/**
	 * 查询常用菜单
	 *
	 * @param me
	 * @return
	 * @author
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_getNavUsedAlways")
	public String getNavUsedAlways(UserContext me) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.getNavUsedAlways(me);
	}

	/**
	 * 添加常用菜单
	 *
	 * @param me
	 * @return
	 * @author
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_addNavUsedAlways")
	public String addNavUsedAlways(UserContext me, String navInfo) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.addNavUsedAlways(me, navInfo);
	}

	/**
	 * 删除常用菜单
	 *
	 * @param me
	 * @return
	 * @author
	 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_delNavUsedAlways")
	public String delNavUsedAlways(UserContext me, String funNavId) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.delNavUsedAlways(me, funNavId);
	}

	/* cms全文检索 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_cmsSearchOfAll")
	public String cmsSearchOfAll(UserContext me, String searchwords, String start, String limit, String rsflag) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.cmsSearchOfAll(searchwords, start, limit, rsflag);
	}

	/* cms全文检索 */
	@Mapping("com.actionsoft.apps.skins.mportallocal_get_byod_barcode")
	public String getByodBarCode(UserContext me) {
		MportalSkinsWeb web = new MportalSkinsWeb(me);
		return web.getByodBarCode(me);
	}

}
