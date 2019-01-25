package com.actionsoft.apps.gyxmgl.util;


public class GlobleParams {
	
	
	
	
	//平台表注册
	public static String ORGUSER_TABLE = "ORGUSER"; //用户信息表
	public static String WFC_TASK_TABLE = "wfc_task";//任务信息表
	public static String WFC_PROCESS_TABLE = "Wfc_Process";//任务信息表
	
	
	
	
	
	public static String gydyzt_ydy = "已抵押";      //抵质押 抵押状态
	
	
	
	//固有项目14状态说明
	public static String gyxmcbStatus = "项目储备";							// 项目储备
	public static String gyxmlxspStatusIng="立项审批中";					//固有项目立项审批中
	public static String gyxmlxspSuccessStatus="立项批准";				//
	public static String gyxmyxzStatusIng="项目运行中";					    //
	public static String gyxmzxcsshzStatusIng="增信落实中";			//
	public static String gyxmzxcsshSuccessStatus="增信落实通过";
	public static String gyxmzzStatusIng="项目中止中";
	public static String gyxmzzSuccessStatus="项目中止";
	public static String gyxmyjStatusIng="项目移交中";
	public static String gyxmyjSuccessStatus="项目移交";
	public static String gyxmfabgStatusIng="方案变更中";
	public static String gyxmfabgSuccessStatus="方案变更";
	
	public static String gyxmfabglsStatusIng="方案变更落实中";
	public static String gyxmfabglsSuccessStatus="方案变更已落实";
	
	public static String gyxmxmjsSuccessStatus="项目结束";
	public static String gyxmxmyjsSuccessStatus="项目已结束";
	
	public static String gyxmxmgdStatusIng="项目归档中";
	public static String gyxmxmxmjsSuccessStatus="项目已归档";
	
	public static String gyxmyjlsStatusIng="意见落实中";
	public static String gyxmyjlsSuccessStatus="意见已落实";
	
	// 固有项目阶段说明 4个阶段
	public static String gyxmlxsp = "立项审批(固有项目)";// 立项审批(固有项目)
	public static String gyxmxmyxz = "项目运行中(固有项目)";
	public static String gyxmxmjs = "项目结束(固有项目)";
	public static String gyxmxmyjs = "项目已结束(固有项目)";
	
	
	
	// 固有项目主数据表注册处
	public static String BO_ACT_PM_GYXMXXB_TABLE = "BO_ACT_PM_GYXMXXB";//固有项目信息BO表
	public static String BO_ACT_PM_GYXMCB_TABLE = "BO_ACT_PM_GYXMCB";//固有项目储备BO表
	public static String BO_ACT_PM_XMJDLCGXB_TABLE = "BO_ACT_PM_XMJDLCGXB";//项目阶段类型关系表
	public static String BO_ACT_PM_WF_TJXX_TABLE = "BO_ACT_PM_WF_TJXX";//项目流程统计信息表
	public static String BO_ACT_PM_XMFLB_TABLE = "BO_ACT_PM_XMFLB";//项目分类表
	public static String BO_ACT_PM_XMJDB_TABLE = "BO_ACT_PM_XMJDB";//项目阶段表
	public static String BO_ACT_PM_GYXMTWHSP_TABLE = "BO_ACT_PM_GYXMTWHSP";//投委会审批表
	public static String BO_ACT_PM_GYXMGXRB_TABLE = "BO_ACT_PM_GYXMGXRB";//项目干系人表
	public static String BO_ACT_PM_GYXMXMZZB_TABLE ="BO_ACT_PM_GYXMXMZZB";//项目中止审批主表
	public static String BO_ACT_PM_GYFABGLSB_TABLE ="BO_ACT_PM_GYFABGLSB";//固有项目方案变更落实表(项目信息管理)
	public static String BO_ACT_PM_KHXXB_TABLE = "BO_ACT_PM_KHXXB";//客户信息表
	
	
	//OA表单注册处
	public static String BO_ACT_BXPD_APPLY_TABLE ="BO_ACT_BXPD_APPLY";//报销凭单表
	public static String BO_ACT_BXPD_APPLY_S_TABLE ="BO_ACT_BXPD_APPLY_S";//报销凭单子表BO_ACT_BXPD_APPLY
	public static String BO_ACT_CLFBXD_APPLY_S_TABLE ="BO_ACT_CLFBXD_APPLY_S";//差旅费报销子表
	public static String BO_ACT_CLFBXD_APPLY_TABLE ="BO_ACT_CLFBXD_APPLY";//差旅费报销表
	
	//仅存储数据流程UUID注册处 
	public static String GYXMXX_UUID    = "obj_3174df5d323e4fcfb5ad50f360b6eb6c"; //固有项目基础信息UUID
	public static String GYXMGXR_UUID    = "obj_509be051f09b4e259e93843027cbf1f5"; //固有干系人UUID
	public static String GYXMDYWXX_UUID = ""; //固有项目抵押物信息UUID
	public static String GYXMZYWXX_UUID = ""; //固有项目质押物信息UUID
	public static String GYXMYYHTXX_UUID= ""; //固有项目运用合同信息UUID
	public static String GYXMDBHTXX_UUID= ""; //固有项目担保合同信息UUID

	//OA注册表
	public static String BO_ACT_OALCFLPZ_TABLE = "BO_ACT_OALCFLPZ";

}
