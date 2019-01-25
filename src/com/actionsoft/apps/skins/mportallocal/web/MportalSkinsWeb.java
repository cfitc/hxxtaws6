package com.actionsoft.apps.skins.mportallocal.web;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.actionsoft.apps.lifecycle.log.AppsLogger;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.skins.mportallocal.byod.ByodTools;
import com.actionsoft.apps.skins.mportallocal.constant.MportalSkinsConstant;
import com.actionsoft.apps.skins.mportallocal.dao.MportalNavDao;
import com.actionsoft.apps.skins.mportallocal.model.MportalNavModel;
import com.actionsoft.apps.skins.mportallocal.model.MportalNavUsedAlModel;
import com.actionsoft.apps.skins.mportallocal.plugins.Plugins;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.commons.portal.skins.notifier.PortletNotificationMessage;
import com.actionsoft.bpms.commons.portal.skins.notifier.PortletNotifier;
import com.actionsoft.bpms.commons.portal.skins.notifier.PortletNotifierInterface;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.fs.DCContext;
import com.actionsoft.bpms.util.UUIDGener;
import com.actionsoft.bpms.util.UtilIO;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.exception.AWSAPIException;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;
import com.actionsoft.sdk.local.api.PortalAPI;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MportalSkinsWeb extends ActionWeb {

	public MportalSkinsWeb(UserContext ctx) {
		super(ctx);
	}

	/**
	 * 获取所有有权限的功能菜单
	 *
	 * @return
	 */
	public String getNavAll() {
		ResponseObject result = ResponseObject.newOkResponse();
		try {
			result.put("allSysNav", getAllSysNavArray());
		} catch (Exception e) {
			e.printStackTrace();
			result.err(e.getMessage());
		}
		return result.toString();
	}

	/**
	 * 获取主题风格自定义的功能菜单
	 *
	 * @return
	 */
	public String getMetroNav() {
		ResponseObject result = ResponseObject.newOkResponse();
		try {
			String curUserId = getContext().getUID();
			List<MportalNavModel> navFuncList = new LinkedList<MportalNavModel>();
			MportalNavModel model;
			// 获取固定的功能菜单
			AppAPI appApi = SDK.getAppAPI();
			String freezeFunc = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
					MportalSkinsConstant.METRO_FREEZE_FUNC);
			if (!UtilString.isEmptyByTrim(freezeFunc)) {
				String[] funcIds = freezeFunc.split(",");
				for (String funcId : funcIds) {
					model = new MportalNavModel();
					model.setId(funcId);
					model.setFunctionId(funcId);
					model.setFreeze(true);
					navFuncList.add(model);
				}
			}

			// 获取自定义的功能菜单
			MportalNavDao dao = new MportalNavDao();
			List<MportalNavModel> metroNavList = dao.getMetroNav(curUserId);
			if (metroNavList != null && metroNavList.size() >= 0) {
				for (MportalNavModel nav : metroNavList) {
					if (nav.getFunctionId() == null) // 去除垃圾数据
						continue;
					if (!freezeFunc.contains(nav.getFunctionId())) {
						navFuncList.add(nav);
					}
				}
			}
			result.put("metroNavs", record2JSON(navFuncList));
		} catch (Exception e) {
			e.printStackTrace();
			result.err(e.getMessage());
		}
		return result.toString();
	}

	/**
	 * 获取当前用户有权限的所有功能菜单
	 *
	 * @return JSONArray
	 * @throws Exception
	 */
	private JSONArray getAllSysNavArray() throws Exception {
		PortalAPI portalApi = SDK.getPortalAPI();
		JSONArray metroNavs = new JSONArray();
		try {
			// 获取平台提供的所有功能菜单
			JSONArray navTrees = portalApi.getNavTree(getContext());
			int len = navTrees.size();
			for (int i = 0; i < len; i++) { // 遍历
				// 子系统
				JSONObject subsystem = navTrees.getJSONObject(i);
				if (validateURL(subsystem.getString("url"))) // 有url，就显示
					metroNavs.add(subsystem);
				JSONArray directorys = subsystem.getJSONArray("directory");
				// 功能目录
				for (int ii = 0; ii < directorys.size(); ii++) {
					JSONObject directory = directorys.getJSONObject(ii);
					if (validateURL(directory.getString("url"))) // 有url，就显示
						metroNavs.add(directory);
					// 功能菜单
					JSONArray functions = directory.getJSONArray("function");
					if (functions.size() > 0) {
						for (int iii = 0; iii < functions.size(); iii++) {
							JSONObject function = functions.getJSONObject(iii);
							if (validateURL(function.getString("url"))) // 有url，就显示
								metroNavs.add(function);
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return metroNavs;
	}

	private boolean validateURL(String url) {
		if (UtilString.isEmptyByTrim(url))
			return false;
		url = url.trim();
		if ("/".equals(url) || "temp".equals(url) || "/temp".equals(url))
			return false;
		return true;
	}

	/**
	 * 获取当前用户有权限的所有功能菜单
	 *
	 * @return Map
	 */
	private Map<String, JSONObject> getAllSysNavMap() {
		Map<String, JSONObject> map = new LinkedHashMap<String, JSONObject>();
		PortalAPI portalApi = SDK.getPortalAPI();
		try {
			// 获取平台提供的所有功能菜单
			JSONArray navTrees = portalApi.getNavTree(getContext());
			int len = navTrees.size();
			for (int i = 0; i < len; i++) { // 遍历
				// 子系统
				JSONObject subsystem = navTrees.getJSONObject(i);
				if (validateURL(subsystem.getString("url"))) // 有url，就显示
					map.put(subsystem.getString("id"), subsystem);
				// 菜单目录
				JSONArray directorys = subsystem.getJSONArray("directory");
				for (int ii = 0; ii < directorys.size(); ii++) {
					JSONObject directory = directorys.getJSONObject(ii);
					if (validateURL(directory.getString("url"))) // 有url，就显示
						map.put(directory.getString("id"), directory);
					// 功能菜单
					JSONArray functions = directory.getJSONArray("function");
					if (functions.size() > 0) {
						for (int iii = 0; iii < functions.size(); iii++) {
							JSONObject function = functions.getJSONObject(iii);
							if (validateURL(function.getString("url"))) // 有url，就显示
								map.put(function.getString("id"), function);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, JSONObject> getAllPublicPortlet() {
		Map<String, JSONObject> map = new LinkedHashMap<String, JSONObject>();
		PortalAPI portalApi = SDK.getPortalAPI();
		try {
			// 获取平台提供的所有公共门户
			JSONArray publicPortlets = portalApi.getPublicPortlet(getContext());
			int len = publicPortlets.size();
			for (int i = 0; i < len; i++) { // 遍历
				// 分组
				JSONObject groups = publicPortlets.getJSONObject(i);
				// 公共门户菜单
				JSONArray portlets = groups.getJSONArray("portlets");
				for (int ii = 0; ii < portlets.size(); ii++) {
					JSONObject portlet = portlets.getJSONObject(ii);
					if (validateURL(portlet.getString("url"))) // 有url，就显示
						map.put(portlet.getString("id"), portlet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 保存功能菜单
	 *
	 * @param list
	 * @return
	 */
	public String saveMetroNav(List<MportalNavModel> list) {
		ResponseObject result = ResponseObject.newOkResponse();
		MportalNavDao dao = new MportalNavDao();
		try {
			String curUserId = getContext().getUID();
			// 批量保存功能菜单
			dao.batchCreate(list, curUserId);
			// 保存后重新加载功能菜单
			List<MportalNavModel> navFuncList = new LinkedList<MportalNavModel>();
			MportalNavModel model;
			// 获取固定的功能菜单
			AppAPI appApi = SDK.getAppAPI();
			String freezeFunc = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS,
					MportalSkinsConstant.METRO_FREEZE_FUNC);
			String[] funcIds = freezeFunc.split(",");
			for (String funcId : funcIds) {
				model = new MportalNavModel();
				model.setId(funcId);
				model.setFunctionId(funcId);
				navFuncList.add(model);
			}
			// 获取自定义的功能菜单
			List<MportalNavModel> metroNavList = dao.getMetroNav(curUserId);
			if (metroNavList != null && metroNavList.size() >= 0) {
				navFuncList.addAll(metroNavList);
			}
			result.put("metroNavs", record2JSON(navFuncList));
		} catch (Exception e) {
			e.printStackTrace();
			result.err(e.getMessage());
		}
		return result.toString();
	}

	/**
	 * 加载磁贴消息
	 *
	 * @param ids
	 * @return
	 */
	public String loadPortletNotifier(String ids) {
		ResponseObject result = ResponseObject.newOkResponse();
		Map<String, JSONObject> navFuncAll = getAllSysNavMap();
		Map<String, JSONObject> publicPortletAll = getAllPublicPortlet();
		JSONArray data = new JSONArray();
		JSONObject json;
		String[] idStrs = ids.split(",");
		int len = idStrs.length;
		for (int i = 0; i < len; i++) {
			json = new JSONObject();
			String functionId = idStrs[i];
			// 获取功能菜单相关的属性
			JSONObject navFunc = navFuncAll.get(functionId);
			if (navFunc == null) {
				navFunc = publicPortletAll.get(functionId);
				if (navFunc == null)
					continue;
			}
			// App 上下文
			AppContext app = null;

			String appId = navFunc.get("appId") == null ? "" : navFunc.getString("appId");
			if (UtilString.isEmptyByTrim(appId)) {
				data.add(json);
				continue;
			}
			AppAPI appApi = SDK.getAppAPI();
			app = appApi.getAppContext(appId);
			if (app == null) {
				data.add(json);
				continue;
			}
			String notifier = navFunc.getString("notifier");
			if (UtilString.isEmptyByTrim(notifier)) {
				data.add(json);
				continue;
			}
			PortletNotifierInterface portletNotifier = null;
			try {
				portletNotifier = PortletNotifier.createNotifierInstance(app, notifier);
			} catch (NullPointerException e) {
				AppsLogger.err(app,
						appApi.i18NValue(MportalSkinsConstant.APP_MPORTALSKINS, getContext(), "信息推送接口") + "[" + notifier
								+ "]。" + appApi.i18NValue(MportalSkinsConstant.APP_MPORTALSKINS, getContext(), "不存在")
								+ e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				AppsLogger.err(app, e.getMessage());
				e.printStackTrace();
			}
			if (portletNotifier != null) {
				PortletNotificationMessage message = null;
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("url", navFunc.getString("url"));
					message = portletNotifier.flash(getContext(), params);
				} catch (Exception e) {
					AppsLogger.err(app, e.getMessage());
					e.printStackTrace();
				}
				if (message == null) {
					json.put("functionId", functionId);
					data.add(json);
					continue;
				}
				String bubble = message.getBubble();
				if (!UtilString.isEmptyByTrim(bubble)) // 气泡
					json.put("tile", bubble);
				String content = message.getContent();
				if (!UtilString.isEmptyByTrim(content)) // 内容
					json.put("content", content);
				String metroBoxType = portletNotifier.getMetroBoxType(); // 方块类型(2*1)
				if (UtilString.isEmptyByTrim(metroBoxType))
					metroBoxType = PortletNotificationMessage.METRO_BOX_1X1;
				String[] boxTypes = metroBoxType.split("[*]");
				json.put("rowNumel", boxTypes[0]);
				json.put("colNumel", boxTypes[1]);
			}
			json.put("functionId", functionId);
			data.add(json);
		}
		result.put("portletNotifier", data);
		return result.toString();
	}

	/**
	 * 将记录转换为JSONArray
	 *
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	private JSONArray record2JSON(List<MportalNavModel> list) throws AWSAPIException {
		Map<String, JSONObject> navFuncAll = getAllSysNavMap();
		Map<String, JSONObject> publicPortletAll = getAllPublicPortlet();
		JSONArray array = new JSONArray();
		JSONObject json;
		for (MportalNavModel model : list) {
			json = record2JSON(model);
			// 获取功能菜单相关的属性
			JSONObject navFunc = navFuncAll.get(model.getFunctionId());
			if (navFunc == null) {
				navFunc = publicPortletAll.get(model.getFunctionId());
				if (navFunc == null)
					continue;
			}
			json.putAll(navFunc);
			json.put("id", model.getId()); // 重新设置id为mentroNav的id值
			json.put("functionId", model.getFunctionId());
			// App 上下文
			AppContext app = null;
			String appId = navFunc.get("appId") == null ? "" : navFunc.getString("appId");
			if (UtilString.isEmptyByTrim(appId)) {
				array.add(json);
				continue;
			}
			AppAPI appApi = SDK.getAppAPI();
			app = appApi.getAppContext(appId);
			if (app == null) {
				array.add(json);
				continue;
			}
			String notifier = navFunc.getString("notifier");
			if (UtilString.isEmptyByTrim(notifier)) {
				array.add(json);
				continue;
			}
			PortletNotifierInterface portletNotifier = null;
			try {
				portletNotifier = PortletNotifier.createNotifierInstance(app, notifier);
			} catch (NullPointerException e) {
				AppsLogger.err(app,
						appApi.i18NValue(MportalSkinsConstant.APP_MPORTALSKINS, getContext(), "信息推送接口") + "[" + notifier
								+ "]。" + appApi.i18NValue(MportalSkinsConstant.APP_MPORTALSKINS, getContext(), "不存在")
								+ e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				AppsLogger.err(app, e.getMessage());
				e.printStackTrace();
			}
			if (portletNotifier != null) {
				String metroBoxType = portletNotifier.getMetroBoxType();
				if (UtilString.isEmptyByTrim(metroBoxType))
					metroBoxType = PortletNotificationMessage.METRO_BOX_1X1;
				String[] boxTypes = metroBoxType.split("[*]");
				json.put("rowNumel", boxTypes[0]);
				json.put("colNumel", boxTypes[1]);
			}
			array.add(json);
		}
		return array;
	}

	/**
	 * 设置背景图片
	 *
	 * @param metroBackground
	 * @return
	 */
	public String setBackGround(String metroBackGround) {
		ResponseObject result = ResponseObject.newOkResponse();
		try {
			PortalAPI portalApi = SDK.getPortalAPI();
			boolean bool = portalApi.setUserProfileItem(MportalSkinsConstant.APP_MPORTALSKINS, getContext().getUID(),
					MportalSkinsConstant.METRO_BACKGROUND, MportalSkinsConstant.METRO_BACKGROUND, metroBackGround);
			if (!bool)
				result.err("设置背景图片失败");
		} catch (Exception e) {
			e.printStackTrace();
			result.err(e.getMessage());
		}
		return result.toString();
	}

	/**
	 * 根据子系统导航ID获取当前用户可访问的目录和功能
	 *
	 * @param navSystemId
	 * @return
	 */
	public String getNavBySystem(String navSystemId) {
		ResponseObject result = ResponseObject.newOkResponse();
		try {
			PortalAPI portalApi = SDK.getPortalAPI();
			JSONArray dirList = portalApi.getNavListBySysId(getContext(), navSystemId);
			result.put("dirList", dirList);
			// 如果是首页，加载公共门户的菜单
			if (MportalSkinsConstant.METRO_NAV_HOMEPAGE_ID.equals(navSystemId)) {
				JSONArray publicPortletList = portalApi.getPublicPortlet(getContext());
				result.put("publicPortletList", publicPortletList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.err(e.getMessage());
		}
		return result.toString();
	}

	/**
	 * 根据子系统导航ID 一次性获取当前用户可访问的目录和功能
	 *
	 * @param navSystemId
	 * @return
	 */
	public String getAllDirFunc(String navSystemList) {
		ResponseObject result = ResponseObject.newOkResponse();
		JSONArray allDir = new JSONArray();
		JSONArray publicPortle = new JSONArray();
		JSONArray navSystems = JSONArray.fromObject(navSystemList);
		for (int i = 0; i < navSystems.size(); i++) {
			JSONObject oo = JSONObject.fromObject(navSystems.getString(i));
			try {
				PortalAPI portalApi = SDK.getPortalAPI();
				JSONArray dirList = portalApi.getNavListBySysId(getContext(), oo.getString("id"));
				allDir.add(dirList);
				// 如果是首页，加载公共门户的菜单
				if (MportalSkinsConstant.METRO_NAV_HOMEPAGE_ID.equals(oo.getString("id"))) {
					JSONArray publicPortletList = portalApi.getPublicPortlet(getContext());
					publicPortle.add(publicPortletList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		result.put("allDir", allDir);
		result.put("publicPortle", publicPortle);
		return result.toString();
	}

	/**
	 * 查询全部应用菜单
	 *
	 * @param searchValue
	 * @return
	 */
	public String searchNav(String searchValue) {
		ResponseObject result = ResponseObject.newOkResponse();
		JSONArray navArray = new JSONArray();
		try {
			searchValue = searchValue.toUpperCase();
			Map<String, JSONObject> navFuncAll = getAllSysNavMap();
			Map<String, JSONObject> publicPortletAll = getAllPublicPortlet();
			Iterator<String> iter = navFuncAll.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				JSONObject json = navFuncAll.get(key);
				String name = json.get("name").toString().toUpperCase();
				if (name.indexOf(searchValue) > -1) {
					navArray.add(json);
				}
			}
			iter = publicPortletAll.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				JSONObject json = publicPortletAll.get(key);
				String name = json.get("name").toString().toUpperCase();
				if (name.indexOf(searchValue) > -1) {
					navArray.add(json);
				}
			}
			result.put("searchNavArray", navArray);
		} catch (Exception e) {
			e.printStackTrace();
			result.err(e.getMessage());
		}
		return result.toString();
	}

	private String getFileExt(String fn) {
		int pos = fn.lastIndexOf('.');
		if (pos == -1) {
			return "";
		}
		return fn.substring(pos + 1);
	}

	/**
	 * 裁切图片
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param srcFile
	 * @return
	 * @author WangSz
	 */
	public String cutImage(int x, int y, int width, int height, double ratio, String srcFile) {
		ResponseObject result = ResponseObject.newOkResponse();
		try {
			AppAPI appApi = SDK.getAppAPI();
			AppContext metroApp = appApi.getAppContext(MportalSkinsConstant.APP_MPORTALSKINS);
			String path = metroApp.getPath();
			srcFile = "/home/wsz/workspace/aws6/release/apps/install/com.actionsoft.apps.network/bg_003_big.jpg";
			File dirFile = File.createTempFile(UUIDGener.getUUID(), ".jpg");
			cut(srcFile, dirFile, x, y, width, height, ratio);
			result.put("url", dirFile.getPath());
		} catch (Exception e) {
			e.printStackTrace();
			result.err(e.getMessage());
		}
		return result.toString();
	}

	/**
	 * 图像切割(按指定起点坐标和宽高切割)
	 *
	 * @param srcImageFile  源图像地址
	 * @param destImageFile 切片后的图像
	 * @param x             目标切片起点坐标X
	 * @param y             目标切片起点坐标Y
	 * @param width         目标切片宽度
	 * @param height        目标切片高度
	 * @param ratio         图像缩放比例
	 */
	public void cut(String srcImageFile, File destImageFile, int x, int y, int width, int height, double ratio) {
		try {
			// 读取源图像
			File srcFile = new File(srcImageFile);
			BufferedImage bi = ImageIO.read(srcFile);
			int srcWidth = bi.getWidth(); // 源图宽度
			int srcHeight = bi.getHeight(); // 源图高度
			if (srcWidth > 0 && srcHeight > 0) {
				// 缩放图片
				if (ratio != 1) {
					// 等比例放缩图片
					int destWidth = (int) (srcWidth * ratio);
					int destHeight = (int) (srcHeight * ratio);

					// 使用源图像文件名创建ImageIcon对象。
					ImageIcon imgIcon = new ImageIcon(srcImageFile);
					// 得到Image对象。
					Image srcImage = imgIcon.getImage();

					// 构造一个预定义的图像类型的BufferedImage对象。
					BufferedImage buffImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
					// buffImg.flush();
					// 创建Graphics2D对象，用于在BufferedImage对象上绘图。
					Graphics2D g = buffImg.createGraphics();

					// 设置图形上下文的当前颜色为白色。
					g.setColor(Color.WHITE);
					// 用图形上下文的当前颜色填充指定的矩形区域。
					g.fillRect(0, 0, destWidth, destHeight);
					// 按照缩放的大小在BufferedImage对象上绘制原始图像。
					g.drawImage(srcImage, 0, 0, destWidth, destHeight, null);
					// 释放图形上下文使用的系统资源。
					g.dispose();
					// 刷新此 Image 对象正在使用的所有可重构的资源.
					srcImage.flush();
					bi = buffImg;
				}
				// 裁切图片
				// 四个参数分别为图像起点坐标和宽高
				// 即: CropImageFilter(int x,int y,int width,int height)
				ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
				Image img = Toolkit.getDefaultToolkit()
						.createImage(new FilteredImageSource(bi.getSource(), cropFilter));
				BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g2 = tag.getGraphics();
				g2.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图
				g2.dispose();
				// 输出为文件
				ImageIO.write(tag, "JPG", destImageFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将记录转换为JSONObject
	 *
	 * @param model
	 * @return
	 */
	private JSONObject record2JSON(MportalNavModel model) {
		JSONObject json = JSONObject.fromObject(model);
		return json;
	}

	/**
	 * 检测应用是否为运行状态
	 *
	 * @param appId
	 * @return
	 * @author ZZ
	 */
	public String checkAppActive(String appId) {
		ResponseObject result = ResponseObject.newOkResponse();
		try {
			if (!UtilString.isEmptyByTrim(appId)) {
				AppAPI appApi = SDK.getAppAPI();
				AppContext appContext = appApi.getAppContext(appId);
				if (appContext != null) {
					boolean isActiv = SDK.getAppAPI().isActive(appContext);// 运行状态
					if (!isActiv) {
						result.err();
						result.msg("该应用暂不可用");
						return result.toString();
					}
				} else {
					result.err();
					result.msg("该应用信息不存在或已卸载");
					return result.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.err(e.getMessage());
		}
		return result.toString();
	}

	/**
	 * 设置背景
	 *
	 * @param appId
	 * @return
	 * @author ZZ
	 */
	public String setLeftBarBackground(UserContext me, String csslink) {
		// TODO Auto-generated method stub
		ResponseObject result = ResponseObject.newOkResponse();
		PortalAPI portalApi = SDK.getPortalAPI();
		JSONObject json = new JSONObject();
		json.put("csslink", csslink);
		Boolean bol = SDK.getPortalAPI().setUserProfileSchema(MportalSkinsConstant.APP_MPORTALSKINS, me.getUID(),
				MportalSkinsConstant.SKINS_CSS_LINK, json);
		result.put("rs", bol);
		return result.toString();
	}

	/**
	 * 添加、修改门户信息
	 *
	 * @param mhProcFlag 添加、修改标志，1 添加 0修改
	 * @return
	 * @author ZZ
	 */
	public String addMhInfo(UserContext me, String addmhInfo) {
		// TODO Auto-generated method stub
		ResponseObject result = ResponseObject.newOkResponse();
		AppAPI appApi = SDK.getAppAPI();
		// 获取本地门户信息
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
			geren.put("mtarget", "_self");
			mhItemsJson.add(geren);
		}
		// 添加、修改的信息
		JSONObject mhobj = JSONObject.fromObject(addmhInfo);
		// 根据mhid判断是否是添加或修改
		if (mhobj.containsKey("mhId")) { // 修改
			String checkmhId = mhobj.getString("mhId");
			for (int i = 0; i < mhItemsJson.size(); i++) {
				JSONObject oldmhobj = JSONObject.fromObject(mhItemsJson.getString(i));
				if (!"".equals(checkmhId) && !"".equals(oldmhobj.getString("mhId"))) {
					if (checkmhId.equals(oldmhobj.getString("mhId"))) {
						mhItemsJson.remove(oldmhobj);
						mhItemsJson.add(i, mhobj);
					}
				}
			}
		} else { // 添加
			mhobj.put("mhId", UUIDGener.getUUID()); // 为门户信息创建唯一标示
			mhItemsJson.add(mhobj);
		}
		appApi.setProperty(MportalSkinsConstant.APP_MPORTALSKINS, "mhItems", mhItemsJson.toString());
		result.put("newmhItemObj", getmhAc(me, mhItemsJson));
		return result.toString();
	}

	// 获取对应的门户的AC访问权限
	public JSONArray getmhAc(UserContext me, JSONArray mhItemsJson) {
		for (int m = 0; m < mhItemsJson.size(); m++) {
			String mhid = JSONObject.fromObject(mhItemsJson.getString(m)).getString("mhId");
			boolean acAuth = SDK.getPermAPI().havingACPermission(me.getUID(), "mportallocal.plugin.AC", mhid, 0, false);
			if (!acAuth) {
				mhItemsJson.remove(m);
			}
		}
		return mhItemsJson;
	}

	/**
	 * 根据门户id 批量删除门户信息
	 *
	 * @param
	 * @return
	 * @author ZZ
	 */
	public String delMhInfo(UserContext me, String delmhInfo) {
		// TODO Auto-generated method stub
		ResponseObject result = ResponseObject.newOkResponse();
		AppAPI appApi = SDK.getAppAPI();
		String mhItems = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "mhItems");
		JSONArray mhItemsJson = JSONArray.fromObject(mhItems);
		JSONArray mhobj = JSONArray.fromObject(delmhInfo);
		for (int i = 0; i < mhobj.size(); i++) {
			String mhId = mhobj.getString(i);
			JSONObject itemInfo = JSONObject.fromObject(mhId);
			for (int j = 0; j < mhItemsJson.size(); j++) {
				String curmhId = mhItemsJson.getString(j);
				JSONObject curitemInfo = JSONObject.fromObject(curmhId);
				if (itemInfo.getString("mhId").equals(curitemInfo.getString("mhId"))) {
					mhItemsJson.remove(j);
				}
			}
		}
		appApi.setProperty(MportalSkinsConstant.APP_MPORTALSKINS, "mhItems", mhItemsJson.toString());
		String newmhItems = appApi.getProperty(MportalSkinsConstant.APP_MPORTALSKINS, "mhItems");
		JSONArray newmhItemObj = JSONArray.fromObject(newmhItems);
		result.put("newmhItemObj", getmhAc(me, newmhItemObj));
		return result.toString();
	}

	/**
	 * 设置顶部导航自定义代码区域
	 *
	 * @param
	 * @return
	 * @author xwp
	 */
	public String setNavHtmlOne(UserContext me, String htmls, String jss) {
		ResponseObject result = ResponseObject.newOkResponse();
		AppAPI appApi = SDK.getAppAPI();
		JSONArray jsonhtml = new JSONArray();
		JSONArray jsonjs = new JSONArray();
		jsonhtml.add(htmls);
		jsonjs.add(jss);
		appApi.setProperty(MportalSkinsConstant.APP_MPORTALSKINS, "setNavHtmlOne", jsonhtml.toString());
		appApi.setProperty(MportalSkinsConstant.APP_MPORTALSKINS, "setNavJs", jsonjs.toString());
		result.put("ok", "ok");
		return result.toString();
	}

	/**
	 * 自定义客户logo、favicon
	 *
	 * @param
	 * @return
	 * @author xwp
	 */
	public String setFavicon(UserContext me, String path, String upflag) {
		ResponseObject result = ResponseObject.newOkResponse();
		AppAPI appApi = SDK.getAppAPI();
		if (!"".equals(upflag) && upflag != null) {
			if (upflag.equals("favicon")) {
				DCContext favicondc = new DCContext(me, Plugins.faviconDc, "admin", "favicons");
				favicondc.setFileName("tempfav.ico");
				String tempPath = favicondc.getFilePath();
				favicondc.setFileName("favicon.ico");
				String npath = favicondc.getFilePath();
				try {
					UtilIO.copy(new DataInputStream(new FileInputStream(tempPath)),
							new DataOutputStream(new FileOutputStream(npath)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (upflag.equals("clogo")) {
				String[] filevalues = { "clogo", "greenclogo", "orangeclogo", "ziseclogo", "slanclogo" };
				for (int i = 0; i < filevalues.length; i++) {
					DCContext clogodc = new DCContext(me, Plugins.clogoDc, "admin", filevalues[i]);
					clogodc.setFileName("tempclogo.jpg");
					String tempPath = clogodc.getFilePath();
					clogodc.setFileName("clogo.jpg");
					String npath = clogodc.getFilePath();
					try {
						UtilIO.copy(new DataInputStream(new FileInputStream(tempPath)),
								new DataOutputStream(new FileOutputStream(npath)));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
		result.put("ok", "ok");
		return result.toString();
	}

	/**
	 * 门户排序
	 *
	 * @param
	 * @return
	 * @author xwp
	 */
	public String mhOrder(String iteminfoArr) {
		ResponseObject result = ResponseObject.newOkResponse();
		AppAPI appApi = SDK.getAppAPI();
		JSONArray mharr = JSONArray.fromObject(iteminfoArr);
		appApi.setProperty(MportalSkinsConstant.APP_MPORTALSKINS, "mhItems", mharr.toString());
		result.put("ok", "ok");
		return result.toString();
	}

	/**
	 * 查询所有站点 cms
	 *
	 * @param
	 * @return
	 * @author xwp
	 */
	public String findCms(UserContext me) {
		ResponseObject result = ResponseObject.newOkResponse();
		// 查询所有站点 cms
		String sourceAppId = MportalSkinsConstant.APP_MPORTALSKINS;
		// 服务地址
		String aslp = "aslp://com.actionsoft.apps.cms/querySite";
		Map params = new HashMap<String, Object>();
		params.put("sid", me.getSessionId());
		ResponseObject ro = SDK.getAppAPI().callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
		String cmsMsg = ro.getMsg();
		String siteList = "false";
		if (cmsMsg != "" && cmsMsg != null) {
			// siteList = "false";
		} else {
			siteList = ((JSONObject) ro.toJsonObject().get("data")).get("siteList").toString();
		}
		result.put("cmsList", siteList);
		return result.toString();
	}

	public String setLogoBg(String ifbg) {
		ResponseObject result = ResponseObject.newOkResponse();
		AppAPI appApi = SDK.getAppAPI();
		appApi.setProperty(MportalSkinsConstant.APP_MPORTALSKINS, "setLogoBg", ifbg);
		result.put("ok", "ok");
		return result.toString();
	}

	public String getNavUsedAlways(UserContext me) {
		// TODO Auto-generated method stub
		ResponseObject result = ResponseObject.newOkResponse();
		MportalNavDao dao = new MportalNavDao();
		List<MportalNavUsedAlModel> usedNavList = new ArrayList<MportalNavUsedAlModel>();
		try {
			usedNavList = dao.getNavUsedAlways(me);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		result.put("usedNavList", usedNavList);
		return result.toString();
	}

	public String addNavUsedAlways(UserContext me, String navInfo) {
		ResponseObject result = ResponseObject.newOkResponse();
		MportalNavDao dao = new MportalNavDao();
		int r = dao.addNavUsed(navInfo, me);
		result.put("result", r);
		return result.toString();
	}

	public String delNavUsedAlways(UserContext me, String funNavId) {
		ResponseObject result = ResponseObject.newOkResponse();
		MportalNavDao dao = new MportalNavDao();
		int r = dao.delNavUsed(me, funNavId);
		result.put("result", r);
		return result.toString();
	}

	public String cmsSearchOfAll(String searchwords, String start, String limit, String rsflag) {
		Map<String, Object> macroLibraries = new HashMap<String, Object>();
		ResponseObject result = ResponseObject.newOkResponse();
		AppAPI appAPI = SDK.getAppAPI();
		String sourceAppId = MportalSkinsConstant.APP_MPORTALSKINS;
		// 服务地址
		String aslp = "aslp://com.actionsoft.apps.cms/queryFullSearchData";
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("sid", getContext().getSessionId());
		params.put("searchValue", searchwords);
		params.put("start", Integer.valueOf(start));
		params.put("limit", Integer.valueOf(limit));
		Map<String, Object> paramsAll = new HashMap<String, Object>();
		paramsAll.put("sid", getContext().getSessionId());
		paramsAll.put("searchValue", searchwords);
		ResponseObject roa = appAPI.callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, paramsAll);
		JSONObject dataa = JSONObject.fromObject(roa.toJsonObject().getString("data"));
		JSONArray msglista = JSONArray.fromObject(dataa.getString("messageList"));
		int magnum = msglista.size();
		ResponseObject ro = appAPI.callASLP(SDK.getAppAPI().getAppContext(sourceAppId), aslp, params);
		JSONObject data = JSONObject.fromObject(ro.toJsonObject().getString("data"));
		JSONArray msglist = JSONArray.fromObject(data.getString("messageList"));
		String html = "";
		int msgListLen = msglist.size();
		for (int i = 0; i < msglist.size(); i++) {
			JSONObject msg = JSONObject.fromObject(msglist.get(i));
			String headStr = msg.getString("title");
			String impStr = "<span class='import-word'>" + searchwords + "</span>";
			headStr = headStr.replace(searchwords, impStr);
			String msgSummary = msg.getString("msgSummary");
			msgSummary = msgSummary.replace(searchwords, impStr);
			html += "  <div class='info-item-panel'><div class='sinfo-head'>" + "<div class='sinfo-title'  id='"
					+ msg.getString("id") + "' siteid='" + msg.getString("siteId") + "'>"
					+ "<span><a onclick=\"showSearchInfo('" + msg.getString("id") + "','" + msg.getString("siteId")
					+ "','" + msg.getString("outUrl") + "')\">" + headStr + "</a></span></div>"
					+ "<div class='sinfo-data'><span>" + msg.getString("createTime") + "</span></div></div>"
					+ "<div class='info-desc'><div class='msg-div' onclick=\"showSearchInfo('" + msg.getString("id")
					+ "','" + msg.getString("siteId") + "')\"><a href='#'>" + msgSummary
					+ "</a></div><div class='position-div'><span>位置：" + msg.getString("moduleName") + "</span><span>"
					+ msg.getString("createUser") + "</span><span>点击量：" + msg.getString("hasAttachment")
					+ "</span></div></div></div>";

		}
		result.put("showhtml", html);
		macroLibraries.put("sid", getContext().getSessionId());
		macroLibraries.put("showhtml", html);
		macroLibraries.put("magnum", magnum);
		macroLibraries.put("searchwords", searchwords);
		if (rsflag.equals("rs")) {
			return result.toString();
		} else {
			return HtmlPageTemplate.merge(MportalSkinsConstant.APP_MPORTALSKINS, "search.html", macroLibraries);
		}
	}

	public String getByodBarCode(UserContext me) {
		ResponseObject result = ResponseObject.newOkResponse();
		ByodTools tools = new ByodTools();
		result = tools.getBarCodeHtml(me);
		return result.toString();
	}
}
