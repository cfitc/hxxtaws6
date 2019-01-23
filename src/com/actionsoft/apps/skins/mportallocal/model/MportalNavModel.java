package com.actionsoft.apps.skins.mportallocal.model;

import java.sql.Timestamp;

import com.actionsoft.bpms.commons.mvc.model.ModelBean;

/**
 * Metro主题风格Model
 * 
 * @author wangsz
 * 
 */
public class MportalNavModel extends ModelBean {

	public static final String DATABASE_ENTITY = "APP_ACT_MPORTALLOCAL_NAV"; // 表名
	public static final String FIELD_ID = "ID"; // 主键
	public static final String FIELD_FUNCTION_ID = "FUNCTIONID"; // 功能ID
	public static final String FIELD_ORDER_NO = "ORDERNO"; // 排序号
	public static final String FIELD_CREATE_USER = "CREATEUSER"; // 创建人
	public static final String FIELD_CREATE_TIME = "CREATETIME"; // 创建时间

	private String id; // 主键
	private String functionId; // 功能ID
	private int orderNo; // 排序号
	private String createUser; // 创建人
	private Timestamp createTime; // 创建时间
	private boolean freeze; // 是否冻结

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public boolean isFreeze() {
		return freeze;
	}

	public void setFreeze(boolean freeze) {
		this.freeze = freeze;
	}

}
