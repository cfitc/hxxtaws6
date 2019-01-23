package com.actionsoft.apps.skins.mportallocal.model;


public class MportalNavUsedAlModel {
	// TODO Auto-generated constructor stub
	public static final String DATABASE_ENTITY_USED = "APP_ACT_MPORTAL_NAV_USED"; // 表名
	private String id; // 主键
	private String name; // 菜单名称
	private String target;
	private String url; // 链接
	private String appId;
	private String icon64;
	private String navId;
	private String userid; // 对应的用户id
	private String funnavid; // 导航id

	public String getFunnavid() {
		return funnavid;
	}

	public void setFunnavid(String funnavid) {
		this.funnavid = funnavid;
	}

	public MportalNavUsedAlModel() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getIcon64() {
		return icon64;
	}

	public void setIcon64(String icon64) {
		this.icon64 = icon64;
	}

	public String getNavId() {
		return navId;
	}

	public void setNavId(String navId) {
		this.navId = navId;
	}

}
