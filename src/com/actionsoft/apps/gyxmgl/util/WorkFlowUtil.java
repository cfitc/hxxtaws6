package com.actionsoft.apps.gyxmgl.util;


import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.exception.AWSException;
import com.actionsoft.sdk.local.SDK;


public class WorkFlowUtil {
	/**
	 * 为仅存储的流程进行初始化
	 * @param processDefId 流程定义ID
	 * @param uid 创建人，一个合法的AWS账户名
	 * @param title 信息标题，建议给定一个方便用户识别的扼要信息
	 * @param boName
	 * @param boDate
	 * @return bindID
	 */
	public static String CreateJCCData(String processDefId, String uid, String title, String boName, BO boDate){
		String bindId = "";
		try {
			bindId = SDK.getProcessAPI().createBOProcessInstance(processDefId, uid, title).getId();
			SDK.getBOAPI().create(boName, boDate, bindId, uid);
		} catch (AWSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.err);
		}
		return bindId;
	}
	
	/**
	 * 创建一个仅存储实例控制，适用于DW，并返回bindid
	 * @param processDefId
	 * @param uid
	 * @param title
	 * @return
	 */
	public static String GetBoProcessInstance(String processDefId,String uid,String title){
		String bindId = "";
		bindId = SDK.getProcessAPI().createBOProcessInstance(processDefId, uid, title).getId();
		return bindId;
	}
}
